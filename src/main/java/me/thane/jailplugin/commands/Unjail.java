package me.thane.jailplugin.commands;

import me.thane.jailplugin.JailPlugin;
import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Unjail implements CommandHandler {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("unjail")) return false;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You must specify a target player!");
            return false;
        }

        OfflinePlayer target = JailPlugin.findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Target " + args[0] + " cannot be found!");
            return false;
        }

        for (Prison prison : Prison.PRISONS) {
            if (prison.free(target)) {
                sender.sendMessage(ChatColor.GREEN + "Freed " + args[0] + " from " + prison.getName() + ".");
                Prison.writePrisonsToFile();
                return true;
            }
        }
        sender.sendMessage(ChatColor.RED + "Target " + args[0] + " is not a prisoner!");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Prison.PRISONS.stream().flatMap(p -> p.getPrisoners().stream()).map(p -> p.getPlayer().getName()).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
