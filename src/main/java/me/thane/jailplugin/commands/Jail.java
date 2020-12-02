package me.thane.jailplugin.commands;

import me.thane.jailplugin.JailPlugin;
import me.thane.jailplugin.prison.Prison;
import me.thane.jailplugin.prison.exceptions.ExistingPrisonerException;
import me.thane.jailplugin.prison.exceptions.FullPrisonException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Jail implements CommandHandler {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!label.equalsIgnoreCase("jail")) return false;
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "You must specify a target player!");
            return false;
        }
        LocalDateTime releaseDate = null;
        if (args.length >= 2) {
            releaseDate = LocalDateTime.now();
            String[] times = args[1].split(":");
            if (times.length >= 1) releaseDate = releaseDate.plusMinutes(Integer.parseInt(times[times.length - 1]));
            if (times.length >= 2) releaseDate = releaseDate.plusHours(Integer.parseInt(times[times.length - 2]));
            if (times.length >= 3) releaseDate = releaseDate.plusDays(Integer.parseInt(times[times.length - 3]));
        }

        OfflinePlayer target = JailPlugin.findPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Target " + args[0] + " cannot be found!");
            return false;
        }
        Prison prison = sender instanceof Player ? Prison.getNearest(((Player) sender).getLocation()) : Prison.PRISONS.get(0);
        try {
            prison.imprison(target, releaseDate);
            Prison.writePrisonsToFile();
            sender.sendMessage(ChatColor.GREEN + "Target " + target.getName()
                    + " has been imprisoned " + (releaseDate == null
                    ? "indefinitely"
                    : "until " + releaseDate.toString())
                    + " at " + prison.getName() + "!");
            return true;
        } catch (FullPrisonException e) {
            sender.sendMessage(ChatColor.RED + "Prison " + e.getPrison().getName() + " has no more available cells!");
            return false;
        } catch (ExistingPrisonerException e) {
            sender.sendMessage(ChatColor.RED + "Prisoner " + e.getPrisoner().getPlayer().getName() + " is already in prison!");
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) return Stream.concat(Bukkit.getOnlinePlayers().stream(), Arrays.stream(Bukkit.getOfflinePlayers()))
                .filter(p -> !Prison.isPrisoner(p))
                .map(OfflinePlayer::getName)
                .collect(Collectors.toList());
        return new ArrayList<>();
    }
}
