package me.thane.jailplugin.prison;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Objects;

public class Cell {
    private BoundingBox boundingBox;
    private Location spawnLocation;

    public Cell() {
    }

    public Cell(BoundingBox boundingBox, Location spawnLocation) {
        this.spawnLocation = spawnLocation;
        this.boundingBox = boundingBox;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public boolean hasPrisoner() {
        Prison prison = Prison.PRISONS.stream().filter(p -> p.getCells().contains(this)).findFirst().orElse(null);
        assert prison != null;
        return prison.getPrisoners().stream()
                .filter(p -> p.getPlayer().isOnline())
                .filter(p -> p instanceof Player)
                .map(p -> (Player) p.getPlayer())
                .anyMatch(p -> p.getBoundingBox().overlaps(boundingBox));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cell)) return false;
        Cell cell = (Cell) o;
        return getBoundingBox().overlaps(cell.getBoundingBox());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoundingBox());
    }
}
