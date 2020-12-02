package me.thane.jailplugin.prison;

import me.thane.jailplugin.JailPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class Prisoner {
    private OfflinePlayer player;
    private LocalDateTime releaseDate;

    public Prisoner() {
    }

    public Prisoner(OfflinePlayer player) {
        this.player = player;
    }

    public Prisoner(OfflinePlayer player, LocalDateTime releaseDate) {
        this.player = player;
        this.releaseDate = releaseDate;
    }

    public OfflinePlayer getPlayer() {
        return player;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Prisoner)) return false;
        Prisoner prisoner = (Prisoner) o;
        return getPlayer().getUniqueId().equals(prisoner.getPlayer().getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlayer());
    }
}
