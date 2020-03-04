package com.gmail.andrewandy.customoregen.addon;

import com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.addon.util.IslandTracker;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.util.DataContainer;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class IslandTrackerTest {

    static {
        CustomOreGenAddon.setInstance(new CustomOreGenAddon());
    }

    private static final String islandID = UUID.randomUUID().toString();
    private static final IslandTracker islandTracker = new IslandTracker(islandID);
    private static final IslandOreGenerator generator = new IslandOreGenerator(islandID, 10, 5);

    @BeforeClass
    public static void setup() {
        CustomOreGenAddon.class.getClassLoader();
        ConfigurationSerialization.registerClass(IslandTracker.class);
        ConfigurationSerialization.registerClass(DataContainer.class);
        try {
            AbstractGenerator.setDataFile(File.createTempFile("Temp", ".yml"));
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        }
    }


    @Test()
    @Order(0)
    public void invalidSerialTest() {
        String ID_KEY;
        try {
            Field field = IslandTracker.class.getDeclaredField("IDENTIFIER_KEY");
            field.setAccessible(true);
            ID_KEY = (String) field.get(null);
            field.setAccessible(false);
        } catch (ReflectiveOperationException ex) {
            Assert.fail(ex.getMessage());
            return;
        }
        Map<String, Object> serial = islandTracker.serialize();
        serial.remove(ID_KEY);
        try {
            IslandTracker.deserialise(serial);
            Assert.fail("Invalid map lacking key was not caught!");
        } catch (IllegalArgumentException ignored) {
        }
        try {
            IslandTracker.fromDataContainer(new DataContainer(serial));
            Assert.fail("Invalid map lacking key was not caught!");
        } catch (IllegalArgumentException ignored) {

        }
    }


    @Test
    @Order(1)
    public void serialisationTest() {
        islandTracker.setGenerator(generator);
        generator.save();
        DataContainer container = new DataContainer(islandTracker.getDataContainer());
        Map<String, Object> serial = islandTracker.serialize();
        Assert.assertEquals(container.serialize(), serial);
        DataContainer reconstructedContainer = new DataContainer(serial);
        Assert.assertEquals(container, reconstructedContainer);
        IslandTracker reconstructedTracker = IslandTracker.deserialise(serial);
        Assert.assertEquals(islandTracker, reconstructedTracker);
        reconstructedContainer = reconstructedTracker.getDataContainer();
        Assert.assertEquals(container, reconstructedContainer);
    }
}
