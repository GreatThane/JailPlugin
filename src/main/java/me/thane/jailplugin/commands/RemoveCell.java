package me.thane.jailplugin.commands;

import me.thane.jailplugin.prison.Cell;
import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

public class RemoveCell implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("removecell")) return false;
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Must specify the location of a cell to remove.");
            return false;
        }
        for (Prison prison : Prison.PRISONS) {
            for (Cell cell : prison.getCells()) {
                if (cell.getBoundingBox().contains(new Vector(Double.parseDouble(args[0]), Double.parseDouble(args[1]), Double.parseDouble(args[2])))) {
                    if (prison.getCells().remove(cell)) {
                        sender.sendMessage(ChatColor.GREEN + "Removed cell from " + prison.getName() + ".");
                        Prison.writePrisonsToFile();
                        return true;
                    }
                }
            }
        }
        sender.sendMessage(ChatColor.RED + "Unable to find a cell!");
        return false;
    }
}
