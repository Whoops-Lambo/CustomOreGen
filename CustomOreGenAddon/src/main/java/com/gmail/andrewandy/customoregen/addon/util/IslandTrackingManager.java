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

    private Map<String, IslandTracker> trackerMap = new HashMap<>();

    public IslandTrackingManager() {
    }

    /**
     * Method as per required for deserialisation.
     */
    public IslandTrackingManager(Map<String, Object> serialMap) {
        if (!Objects.requireNonNull(serialMap).containsKey(IDENTIFIER_KEY)) {
            throw new IllegalArgumentException("Invalid serial provided! Identifier key was missing.");
        }
        serialMap = new HashMap<>(serialMap);
        serialMap.remove(IDENTIFIER_KEY);
        Map<String, IslandTracker> map = new HashMap<>();
        for (Map.Entry<String, Object> entry : serialMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.contentEquals("==") || value == null) {
                continue;
            }
            if (!(value instanceof IslandTracker)) {
                throw new IllegalArgumentException("Invalid type " + value.getClass() + " detected!");
            }
            map.put(key, (IslandTracker) value);
        }
        this.trackerMap = map;
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
        return trackerMap.computeIfAbsent(islandID, key -> new IslandTracker(islandID));
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
     * @throws IOException Thrown if IO errors occurs when writing to disk.
     */
    public void saveToFile(File file) throws IOException {
        YamlConfiguration newConfig = new YamlConfiguration();
        writeToConfiguration(newConfig);
        newConfig.save(file);
    }

    public void writeToConfiguration(YamlConfiguration yamlConfiguration) {
        Objects.requireNonNull(yamlConfiguration).set(SERIAL_KEY, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IslandTrackingManager manager = (IslandTrackingManager) o;
        return Objects.equals(trackerMap, manager.trackerMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackerMap);
    }
}
