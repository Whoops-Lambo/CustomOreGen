package com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.leveling;

import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.util.IslandTracker;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.generators.IslandOreGenerator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import world.bentobox.bentobox.database.objects.Island;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class IslandLevelingManager {

    private static final String GEN_LEVEL_KEY = "GeneratorLevel";
    private static final String MAX_GEN_LEVEL_KEY = "MaxGeneratorLevel";
    private static final IslandLevelingManager instance = new IslandLevelingManager();
    private Map<String, IslandTracker> trackerRegistry = new HashMap<>();
    private YamlConfiguration data = new YamlConfiguration();

    private IslandLevelingManager() {
    }

    public static IslandLevelingManager getInstance() {
        return instance;
    }

    public void save(File file) throws IOException {
        data.save(file);
    }

    public void loadFromFile(File file) throws IOException, InvalidConfigurationException {
        data.load(file);
    }

    public int getIslandGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerRegistry.get(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return 0;
        }
        return tracker.getDataContainer().getInt(GEN_LEVEL_KEY).orElse(0);
    }

    public int getIslandGeneratorMaxLevel(String islandID) {
        IslandTracker tracker = trackerRegistry.get(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return 0;
        }
        return tracker.getDataContainer().getInt(MAX_GEN_LEVEL_KEY).orElse(0);
    }

    public void incrementGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerRegistry.get(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return;
        }
        int currentLevel = getIslandGeneratorLevel(islandID);
        if (currentLevel > 0 && currentLevel != getIslandGeneratorLevel(islandID)) {
            tracker.getDataContainer().set(MAX_GEN_LEVEL_KEY, currentLevel + 1);
        }
    }

    public void decrementGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerRegistry.get(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return;
        }
        int currentLevel = getIslandGeneratorLevel(islandID);
        if (currentLevel > 1) {
            tracker.getDataContainer().set(MAX_GEN_LEVEL_KEY, currentLevel - 1);
        }
    }

    /**
     * Set the max level of the generator of this island.
     *
     * @param islandID {@link Island#getUniqueId()}
     * @param maxLevel The new max level of the {@link IslandOreGenerator} of this island.
     */
    public void setMaxGeneratorLevel(String islandID, int maxLevel) {

    }
}
