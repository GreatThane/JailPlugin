package me.thane.jailplugin.commands;

import me.thane.jailplugin.prison.Prison;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfig implements CommandHandler {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("reloadconfig")) return false;
        Prison.readPrisonsFromFile();
        sender.sendMessage(ChatColor.GREEN + "Reloaded config file.");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
