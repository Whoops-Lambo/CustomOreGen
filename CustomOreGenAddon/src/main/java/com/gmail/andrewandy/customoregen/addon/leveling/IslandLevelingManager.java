package com.gmail.andrewandy.customoregen.addon.leveling;

import com.gmail.andrewandy.customoregen.addon.CustomOreGenAddon;
import com.gmail.andrewandy.customoregen.addon.util.IslandTracker;
import com.gmail.andrewandy.customoregen.addon.util.IslandTrackingManager;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Objects;

public class IslandLevelingManager {

    private static final String GEN_LEVEL_KEY = "GeneratorLevel";
    private static final String MAX_GEN_LEVEL_KEY = "MaxGeneratorLevel";
    private static final IslandLevelingManager instance = new IslandLevelingManager();
    private IslandTrackingManager trackerManager = CustomOreGenAddon.getInstance().getTrackingManager();

    private IslandLevelingManager() {
    }

    public static IslandLevelingManager getInstance() {
        return instance;
    }

    public void setTrackerManager(IslandTrackingManager manager) {
        trackerManager = manager;
    }

    public int getIslandGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerManager.getTracker(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return 0;
        }
        return tracker.getDataContainer().getInt(GEN_LEVEL_KEY).orElse(0);
    }

    public int getIslandGeneratorMaxLevel(String islandID) {
        IslandTracker tracker = trackerManager.getTracker(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return 0;
        }
        return tracker.getDataContainer().getInt(MAX_GEN_LEVEL_KEY).orElse(0);
    }

    public void incrementGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerManager.getTracker(Objects.requireNonNull(islandID));
        if (tracker == null) {
            return;
        }
        int currentLevel = getIslandGeneratorLevel(islandID);
        if (currentLevel > 0 && currentLevel != getIslandGeneratorLevel(islandID)) {
            tracker.getDataContainer().set(MAX_GEN_LEVEL_KEY, currentLevel + 1);
        }
    }

    public void decrementGeneratorLevel(String islandID) {
        IslandTracker tracker = trackerManager.getTracker(Objects.requireNonNull(islandID));
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
     * @param maxLevel The new max level of the {@link com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator} of this island.
     */
    public void setMaxGeneratorLevel(String islandID, int maxLevel) {
        IslandTracker tracker = trackerManager.getTracker(islandID);
        tracker.getDataContainer().set(MAX_GEN_LEVEL_KEY, maxLevel);
    }
}
