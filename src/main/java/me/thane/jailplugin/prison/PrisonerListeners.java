package me.thane.jailplugin.prison;

import me.thane.jailplugin.JailPlugin;
import me.thane.jailplugin.prison.exceptions.FullPrisonException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.stream.Stream;

public class PrisonerListeners implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Location loc = event.getPlayer().getBedSpawnLocation();
        if (Prison.isPrisoner(event.getPlayer())) {
            Prison prison = Prison.PRISONS.stream().filter(p -> p.hasPrisoner(event.getPlayer())).findFirst().orElse(null);
            assert prison != null;
            try {
                Cell cell = prison.getNextAvailableCell();
                event.getPlayer().setBedSpawnLocation(cell.getSpawnLocation(), true);
                event.getPlayer().teleport(cell.getSpawnLocation());
            } catch (FullPrisonException e) {
                Stream<CommandSender> senders = Bukkit.getOperators().stream().filter(OfflinePlayer::isOnline).map(p -> (CommandSender) p);
                senders = Stream.concat(senders, Stream.of(Bukkit.getConsoleSender()));
                senders.forEach(s -> s.sendMessage(ChatColor.RED + "Prisoner " + event.getPlayer().getName() + " was unable to join as there were not enough cells at " + prison + "."));
                event.getPlayer().kickPlayer("Unfortunately there are not enough cells to fit you.");
            }
        } else Bukkit.getScheduler().runTaskAsynchronously(JailPlugin.INSTANCE, () -> {
            if (loc == null) return;
            for (Prison prison : Prison.PRISONS) {
                for (Cell cell : prison.getCells()) {
                    if (cell.getSpawnLocation().getWorld().getUID().equals(loc.getWorld().getUID()) && cell.getBoundingBox().contains(loc.toVector())) {
                        event.getPlayer().setBedSpawnLocation(loc.getWorld().getSpawnLocation());
                        Bukkit.getScheduler().runTask(JailPlugin.INSTANCE, () -> event.getPlayer().teleport(loc.getWorld().getSpawnLocation()));
                    }
                }
            }
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player && Prison.isPrisoner((Player) event.getEntity().getShooter())) event.setCancelled(true);
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onBucketFill(PlayerBucketEmptyEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onMultiBlockPlace(BlockMultiPlaceEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) {
            switch (event.getItem().getType()) {
                case POTION:
                case LINGERING_POTION:
                case SPLASH_POTION:
                case CHORUS_FRUIT:
                case POPPED_CHORUS_FRUIT:
                    event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Prison.isPrisoner(event.getPlayer())) event.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && Prison.isPrisoner((Player) event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onShot(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player && Prison.isPrisoner((Player) event.getEntity()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventory(InventoryOpenEvent event) {
        if (event.getPlayer() instanceof Player) {
            Player player = (Player) event.getPlayer();
            if (Prison.isPrisoner(player)) {
                switch (event.getInventory().getType()) {
                    case ENDER_CHEST:
                        return;
                }
                event.setCancelled(true);
            }
        }
    }
}
