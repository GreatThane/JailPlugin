package me.thane.jailplugin.commands;

import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeletePrison implements CommandHandler {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("deleteprison")) return false;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Must specify a prison name.");
            return false;
        }
        Prison.PRISONS.remove(new Prison(args[0]));
        Prison.writePrisonsToFile();
        sender.sendMessage(ChatColor.GREEN + "Prison " + args[0] + " has been deleted.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Prison.PRISONS.stream().map(Prison::getName).filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
