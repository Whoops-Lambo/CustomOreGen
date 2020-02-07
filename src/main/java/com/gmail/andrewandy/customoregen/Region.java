package com.gmail.andrewandy.customoregen;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.util.BoundingBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Region implements ConfigurationSerializable {

    private BoundingBox boundingBox;
    private Location centre;

    public Region(Location first, Location second) {
        if (!first.getWorld().equals(second.getWorld())) {
            throw new IllegalArgumentException("Worlds mis-match!");
        }
        boundingBox = new BoundingBox(first.getX(), first.getY(), first.getZ(), second.getX(), second.getY(), second.getZ());
        centre = boundingBox.getCenter().toLocation(first.getWorld());
    }

    public Region(double x1, double y1, double z1, double x2, double y2, double z2, World world) {
        boundingBox = new BoundingBox(x1, y1, z1, x2, y2, z2);
        centre = boundingBox.getCenter().toLocation(world);
    }

    public Region(BoundingBox boundingBox, World world) {
        this.boundingBox = Objects.requireNonNull(boundingBox);
        centre = boundingBox.getCenter().toLocation(Objects.requireNonNull(world));
    }

    public Region(Map<String, Object> serial) {
        Map<String, Object> rawLoc = (Map<String, Object>) Objects.requireNonNull(serial.get("Centre"));
        centre = Location.deserialize(rawLoc);
        Map<String, Object> rawBoundingBox = (Map<String, Object>) Objects.requireNonNull(serial.get("BoundingBox"));
        this.boundingBox = BoundingBox.deserialize(rawBoundingBox);
    }

    public boolean contains(Location location) {
        return this.centre.getWorld().equals(location.getWorld()) && boundingBox.contains(location.getX(), location.getY(), location.getZ());
    }

    public boolean contains(Region other) {
        return other.boundingBox.contains(this.boundingBox);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("Centre", centre.serialize());
        map.put("BoundingBox", boundingBox.serialize());
        return map;
    }

    public boolean overlaps(Region other) {
        return other.boundingBox.overlaps(this.boundingBox);
    }
}
