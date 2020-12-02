package me.thane.jailplugin.commands;

import me.thane.jailplugin.JailPlugin;
import me.thane.jailplugin.prison.Prison;
import me.thane.jailplugin.prison.Prisoner;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.*;


public class ViewSentence implements CommandHandler {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("viewsentence")) return false;
        OfflinePlayer target = args.length == 0 ? (sender instanceof OfflinePlayer ? (OfflinePlayer) sender : null) : JailPlugin.findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Unable to locate target " + args[0] + ".");
            return false;
        }
        Prison prison = Prison.PRISONS.stream().filter(p -> p.hasPrisoner(target)).findAny().orElse(null);
        if (prison == null) {
            sender.sendMessage(ChatColor.RED + "Target " + target.getName() + " is not currently a prisoner.");
            return false;
        }
        Prisoner prisoner = prison.getPrisoner(target);
        if (prisoner.getReleaseDate() == null) {
            sender.sendMessage(ChatColor.GREEN + String.format("Prisoner %s is indefinitely incarcerated at %s.",
                    target.getName(),
                    prison.getName()));
        } else {
            LocalDateTime now = LocalDateTime.now();
            sender.sendMessage(ChatColor.GREEN + String.format("Prisoner %s will be released in %d days, %d minutes, and %d seconds from %s.",
                    target.getName(),
                    DAYS.between(now, prisoner.getReleaseDate()),
                    MINUTES.between(now, prisoner.getReleaseDate()),
                    SECONDS.between(now, prisoner.getReleaseDate()),
                    prison.getName()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Prison.PRISONS.stream().flatMap(p -> p.getPrisoners().stream()).map(p -> p.getPlayer().getName()).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
