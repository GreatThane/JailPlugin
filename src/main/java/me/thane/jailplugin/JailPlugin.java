package me.thane.jailplugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.thane.jailplugin.commands.*;
import me.thane.jailplugin.prison.Prison;
import me.thane.jailplugin.prison.Prisoner;
import me.thane.jailplugin.prison.PrisonerListeners;
import me.thane.jailplugin.typeadapters.JailTypeAdapterFactory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.time.LocalDateTime;
import java.util.Arrays;

public final class JailPlugin extends JavaPlugin {

    public static JailPlugin INSTANCE;
    public static Gson GSON;

    public JailPlugin() {
        super();
        INSTANCE = this;
        GSON = new GsonBuilder().registerTypeAdapterFactory(new JailTypeAdapterFactory()).setPrettyPrinting().create();
    }

    @Override
    public void onEnable() {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            if (!getDataFolder().exists()) {
                try {
                    getDataFolder().mkdir();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Prison.readPrisonsFromFile();

            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                for (Prison prison : Prison.PRISONS) {
                    for (Prisoner prisoner : prison.getPrisoners()) {

                        if (prisoner.getReleaseDate() != null && LocalDateTime.now().isAfter(prisoner.getReleaseDate())) {
                            Bukkit.getScheduler().runTask(this, () -> prison.free(prisoner));
                        }
                    }
                }
            }, 10, 20);
        });
        setCommandHandler("jail", new Jail());
        setCommandHandler("unjail", new Unjail());
        setCommandHandler("viewsentence", new ViewSentence());
        setCommandHandler("createprison", new CreatePrison());
        setCommandHandler("deleteprison", new DeletePrison());
        setCommandHandler("addcell", new AddCell());
        setCommandHandler("removecell", new RemoveCell());
        setCommandHandler("reloadconfig", new ReloadConfig());

        this.getServer().getPluginManager().registerEvents(new PrisonerListeners(), this);
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void setCommandHandler(String command, CommandHandler handler) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) throw new RuntimeException("Command " + command + " is not found.");
        cmd.setExecutor(handler);
        cmd.setTabCompleter(handler);
    }

    private void setCommandHandler(String command, CommandExecutor handler) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) throw new RuntimeException("Command " + command + " is not found.");
        cmd.setExecutor(handler);
    }

    private void setCommandHandler(String command, TabCompleter handler) {
        PluginCommand cmd = this.getCommand(command);
        if (cmd == null) throw new RuntimeException("Command " + command + " is not found.");
        cmd.setTabCompleter(handler);
    }

    public static OfflinePlayer findPlayer(String name) {
        OfflinePlayer target = Bukkit.getPlayerExact(name);
        if (target == null) {
            target = Arrays.stream(Bukkit.getOfflinePlayers())
                    .filter(p -> p.getName() != null)
                    .filter(p -> p.getName().equalsIgnoreCase(name))
                    .findFirst().orElse(null);
        }
        return target;
    }
}
