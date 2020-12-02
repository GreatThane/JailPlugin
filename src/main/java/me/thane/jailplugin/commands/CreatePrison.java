package me.thane.jailplugin.commands;

import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CreatePrison implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("createprison")) return false;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Must specify a prison name.");
            return false;
        }
        Prison.PRISONS.add(new Prison(args[0]));
        Prison.writePrisonsToFile();
        sender.sendMessage(ChatColor.GREEN + "Prison " + args[0] + " has been created.");
        return true;
    }
}
