package com.gmail.andrewandy.customoregen.addon;

import com.gmail.andrewandy.customoregen.addon.util.IslandTracker;
import com.gmail.andrewandy.customoregen.addon.util.IslandTrackingManager;
import com.gmail.andrewandy.customoregen.util.DataContainer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IslandTrackingManagerTest {

    private static IslandTrackingManager manager;
    private final int totalIslands = 100;
    private Map<String, IslandTracker> islands = new HashMap<>(totalIslands);

    public IslandTrackingManagerTest() {

    }

    @BeforeClass
    public static void loadVariables() {
        ConfigurationSerialization.registerClass(DataContainer.class);
        ConfigurationSerialization.registerClass(IslandTracker.class);
        manager = new IslandTrackingManager();
    }

    @Test
    @Order(0)
    public void additionTest() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        String randomIslandID = null;
        int randomIndex = random.nextInt(0, totalIslands);
        for (int i = 0; i < totalIslands; i++) {
            String island = UUID.randomUUID().toString();
            IslandTracker tracker = manager.getTracker(island);
            Assert.assertNotNull(tracker);
            islands.put(island, tracker);
            if (randomIndex == i) {
                randomIslandID = island;
            }
        }
        assert randomIslandID != null;
        IslandTracker randomTracker = islands.get(randomIslandID);
        Assert.assertEquals(randomTracker, manager.getTracker(randomIslandID));
    }


    @Test
    @Order(1)
    public void serialisationTest() {
        String serial = manager.toJson();
        Optional<IslandTrackingManager> optional = IslandTrackingManager.fromJson(serial);
        Assert.assertTrue(optional.isPresent());
        Assert.assertEquals(optional.get(), manager);
    }

}
