package com.gmail.andrewandy.customoregen.addon;

import com.gmail.andrewandy.customoregen.addon.util.IslandTracker;
import com.gmail.andrewandy.customoregen.addon.util.IslandTrackingManager;
import com.gmail.andrewandy.customoregen.util.DataContainer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
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
        ConfigurationSerialization.registerClass(IslandTrackingManager.class);
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
    public void invalidSerialTest() {
        String IDENTIFIER = null, SERIAL_KEY = null;
        try {
            Field field = IslandTrackingManager.class.getDeclaredField("IDENTIFIER_KEY");
            field.setAccessible(true);
            IDENTIFIER = (String) field.get(null);
            field.setAccessible(false);
            field = IslandTrackingManager.class.getDeclaredField("SERIAL_KEY");
            field.setAccessible(true);
            SERIAL_KEY = (String) field.get(null);
            field.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            Assert.fail(ex.getMessage());
        }
        assert IDENTIFIER != null && SERIAL_KEY != null;
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set("PATH", manager);
        Map<String, Object> map = manager.serialize();
        Assert.assertFalse(IslandTrackingManager.fromData(configuration).isPresent());
        configuration.set(SERIAL_KEY, manager);
        Assert.assertTrue(IslandTrackingManager.fromData(configuration).isPresent());
        map.remove(IDENTIFIER);
        try {
            new IslandTrackingManager(map);
            Assert.fail("Null identifier key not detected!");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @Order(2)
    public void serialisationTest() {
        YamlConfiguration configuration = new YamlConfiguration();
        File file;
        try {
            file = File.createTempFile(UUID.randomUUID().toString(), ".yml");
            file.deleteOnExit();
            manager.saveToFile(file);
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException ex) {
            ex.printStackTrace();
            Assert.fail(ex.getMessage());
            return;
        }
        Optional<IslandTrackingManager> reconstructed = IslandTrackingManager.fromData(configuration);
        Assert.assertTrue(reconstructed.isPresent());
        IslandTrackingManager other = reconstructed.get();
        Assert.assertEquals(other, manager);
    }

}
