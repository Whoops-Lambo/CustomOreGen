package com.gmail.andrewandy.customoregen.addon.util;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a manager of island trackers.
 */
public class IslandTrackingManager implements ConfigurationSerializable {

    private static final String SERIAL_KEY = "ISLAND_TRACKING_MANAGER";
    private static final String IDENTIFIER_KEY = "ISLAND_TRACKING_IDENTIFY";

    private YamlConfiguration data;
    private Map<String, IslandTracker> trackerMap = new HashMap<>();

    public IslandTrackingManager() {
        data = new YamlConfiguration();
    }

    /**
     * Method as per required for deserialisation.
     */
    public IslandTrackingManager(Map<String, Object> serialMap) {
        if (!Objects.requireNonNull(serialMap).containsKey(IDENTIFIER_KEY)) {
            throw new IllegalArgumentException("Invalid serial provided! Identifier key was missing.");
        }
        this.trackerMap = new HashMap<>();
        data = new YamlConfiguration();
        throw new IllegalArgumentException();
    }

    public static Optional<IslandTrackingManager> fromData(YamlConfiguration configuration) {
        Objects.requireNonNull(configuration);
        IslandTrackingManager target = configuration.getSerializable(SERIAL_KEY, IslandTrackingManager.class);
        if (target == null) {
            return Optional.empty();
        }
        return Optional.of(target);
    }

    /**
     * Get an {@link IslandTracker} of an island or
     * a new instance.
     *
     * @param islandID The UniqueID of the island.
     * @return return the cached tracker or a new instance if
     * no tracker was found in the cache.
     */
    public IslandTracker getTracker(String islandID) {
        IslandTracker tracker = trackerMap.get(islandID);
        return tracker == null ? new IslandTracker(islandID) : tracker;
    }

    public void setTracker(String islandID, IslandTracker tracker) {
        trackerMap.remove(islandID);
        trackerMap.put(islandID, tracker);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put(IDENTIFIER_KEY, "null");
        map.putAll(trackerMap);
        return map;
    }

    /**
     * Save the current state of this manager to disk.
     *
     * @param file The file to save to.
     * @throws IOException Thrown if IO errors ocurred when writing to disk.
     */
    public void saveToFile(File file) throws IOException {
        data.set(SERIAL_KEY, this); //TODO: does this --> Sout(data.saveToString gives "SERIAL_KEY: ==:"packagename.IslandTracker");
        data.save(file);
    }
}
