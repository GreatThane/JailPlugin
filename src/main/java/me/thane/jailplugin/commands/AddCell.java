package me.thane.jailplugin.commands;

import me.thane.jailplugin.prison.Cell;
import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

public class AddCell implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("addcell")) return false;
        Prison prison = Prison.getPrison(args[0]);
        int offset = 1;
        if (prison == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "You must specify a prison!");
                return false;
            }
            offset--;
            prison = Prison.getNearest(((Player) sender).getLocation());
            if (prison == null) {
                sender.sendMessage(ChatColor.RED + "Unable to locate a prison!");
                return false;
            }
        }

        if (args.length < offset + 6) {
            sender.sendMessage(ChatColor.RED + "Must specify both corners!");
            return false;
        }

        BoundingBox box = new BoundingBox(
                Double.parseDouble(args[offset]),
                Double.parseDouble(args[offset + 1]),
                Double.parseDouble(args[offset + 2]),
                Double.parseDouble(args[offset + 3]),
                Double.parseDouble(args[offset + 4]),
                Double.parseDouble(args[offset + 5])
        );
        box.expand(0.5);

        Location spawnLocation;
        if (args.length >= offset + 9) {
            spawnLocation = new Location(((Player) sender).getWorld(), Double.parseDouble(args[offset + 6]), Double.parseDouble(args[offset + 7]), Double.parseDouble(args[offset + 8]));
        } else spawnLocation = ((Player) sender).getLocation();

        if (prison.getCells().stream().filter(c -> c.getSpawnLocation().getWorld().getUID().equals(spawnLocation.getWorld().getUID())).map(Cell::getBoundingBox).anyMatch(b -> b.overlaps(box))) {
            sender.sendMessage(ChatColor.RED + "This region already belongs to a cell!");
            return false;
        }

        prison.getCells().add(new Cell(box, spawnLocation));
        Prison.writePrisonsToFile();
        sender.sendMessage(ChatColor.GREEN + "Added cell to " + prison.getName() + ".");
        return true;
    }
}
