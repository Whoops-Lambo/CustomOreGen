package com.gmail.andrewandy.customoregen.addon.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a manager of island trackers.
 */
public class IslandTrackingManager {

    public static final transient Type TYPE = new TypeToken<IslandTrackingManager>() {
    }.getType();

    private Map<String, IslandTracker> trackerMap = new HashMap<>();

    public IslandTrackingManager() {
    }

    public static Optional<IslandTrackingManager> fromJson(String json) {
        Gson gson = new GsonBuilder().create();
        return Optional.of(gson.fromJson(json, TYPE));
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

    public String toJson() {
        return new GsonBuilder().create().toJson(this, TYPE);
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
