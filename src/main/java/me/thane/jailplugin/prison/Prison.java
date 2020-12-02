package me.thane.jailplugin.prison;

import com.google.gson.reflect.TypeToken;
import me.thane.jailplugin.JailPlugin;
import me.thane.jailplugin.prison.exceptions.ExistingPrisonerException;
import me.thane.jailplugin.prison.exceptions.FullPrisonException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Prison {

    public static List<Prison> PRISONS = new ArrayList<>();
    public static File PRISON_FILE = new File(JailPlugin.INSTANCE.getDataFolder() + File.separator + "prisons.json");

    public static void readPrisonsFromFile() {
        if (!PRISON_FILE.exists()) {
            writePrisonsToFile();
        } else try (Reader reader = Files.newBufferedReader(PRISON_FILE.toPath())) {
            Type listType = new TypeToken<ArrayList<Prison>>() {}.getType();
            PRISONS = JailPlugin.GSON.fromJson(reader, listType);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    public static void writePrisonsToFile() {
        Bukkit.getScheduler().runTaskAsynchronously(JailPlugin.INSTANCE, () -> {
            try (FileWriter fw = new FileWriter(Prison.PRISON_FILE)) {
                JailPlugin.GSON.toJson(Prison.PRISONS, fw);
            } catch (IOException e) {
                throw new Error(e);
            }
        });
    }

    private String name;
    private List<Cell> cells;
    private List<Prisoner> prisoners;

    public Prison() {
    }

    public Prison(String name) {
        this.name = name;
        this.cells = new ArrayList<>();
        this.prisoners = new ArrayList<>();
    }

    public void imprison(Prisoner prisoner) throws FullPrisonException, ExistingPrisonerException {
        if (Prison.isPrisoner(prisoner)) throw new ExistingPrisonerException("Prisoner " + prisoner.getPlayer().getName() + " is already in prison!", prisoner);
        prisoners.add(prisoner);
        writePrisonsToFile();
        if (prisoner.getPlayer().isOnline()) {
            Player player = (Player) prisoner.getPlayer();

            Cell cell = getNextAvailableCell();
            player.teleport(cell.getSpawnLocation());
            player.setBedSpawnLocation(cell.getSpawnLocation(), true);
        }
    }

    public Cell getNextAvailableCell() throws FullPrisonException {
        for (Cell cell : cells) {
            if (!cell.hasPrisoner()) return cell;
        }
        throw new FullPrisonException("Prison " + getName() + " is full!", this);
    }

    public Prisoner imprison(OfflinePlayer target, LocalDateTime releaseDate) throws FullPrisonException, ExistingPrisonerException {
        Prisoner prisoner = new Prisoner(target, releaseDate);
        imprison(prisoner);
        return prisoner;
    }

    public boolean free(OfflinePlayer target) {
        return free(new Prisoner(target));
    }

    public boolean free(Prisoner prisoner) {
        if (prisoners.remove(prisoner)) {
            writePrisonsToFile();
            if (prisoner.getPlayer().isOnline()) {
                Location loc = Bukkit.getServer().getWorlds().get(0).getSpawnLocation();
                Player player = (Player) prisoner.getPlayer();
                player.setBedSpawnLocation(loc, true);
                player.teleport(loc);
                player.sendMessage(ChatColor.DARK_RED + "Enjoy your freedom, be more careful next time.");
            }
            return true;
        } else return false;
    }

    public boolean hasPrisoner(OfflinePlayer player) {
        return prisoners.stream().anyMatch(p -> p.getPlayer().getUniqueId().equals(player.getUniqueId()));
    }

    public static boolean isPrisoner(OfflinePlayer player) {
        for (Prison prison : Prison.PRISONS) {
            if (prison.hasPrisoner(player)) return true;
        }
        return false;
    }

    public static boolean isPrisoner(Prisoner prisoner) {
        for (Prison prison : Prison.PRISONS) {
            if (prison.hasPrisoner(prisoner)) return true;
        }
        return false;
    }

    public Prisoner getPrisoner(OfflinePlayer player) {
        return prisoners.stream().filter(p -> p.getPlayer().getUniqueId().equals(player.getUniqueId())).findFirst().orElse(null);
    }

    public boolean hasPrisoner(Prisoner prisoner) {
        return prisoners.stream().anyMatch(p -> p.equals(prisoner));
    }

    public List<Cell> getCells() {
        return cells;
    }

    public List<Prisoner> getPrisoners() {
        return prisoners;
    }

    public Location getLocation() {
        double x = cells.stream().map(l -> l.getSpawnLocation().getX()).mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
        double y = cells.stream().map(l -> l.getSpawnLocation().getY()).mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
        double z = cells.stream().map(l -> l.getSpawnLocation().getZ()).mapToDouble(Double::doubleValue).summaryStatistics().getAverage();
        return new Location(cells.get(0).getSpawnLocation().getWorld(), x, y, z);
    }

    public String getName() {
        return name;
    }

    public static Prison getPrison(String name) {
        return PRISONS.stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Prison getNearest(Location location) {
        return PRISONS.stream()
                .min((o1, o2) -> (int) (o1.getLocation().distanceSquared(location) - o2.getLocation().distanceSquared(location)))
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prison)) return false;
        Prison prison = (Prison) o;
        return Objects.equals(getName(), prison.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
