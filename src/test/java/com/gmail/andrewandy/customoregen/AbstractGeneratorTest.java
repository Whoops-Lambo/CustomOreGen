package com.gmail.andrewandy.customoregen;

import be.seeseemelk.mockbukkit.inventory.ItemFactoryMock;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.implementations.TestGenerator;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AbstractGeneratorTest {

    @BeforeAll
    public static void setupFiles() {
        //Create a temp data file.
        File file;
        try {
            file = File.createTempFile("GeneratorDataTest", ".yml");
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
            return;
        }
        AbstractGenerator.setDataFile(file);
        file.deleteOnExit();
    }

    @Test()
    @Order(2)
    public void serialisationTest() {
        //Create a mock generator
        TestGenerator generator = new TestGenerator(10, 1);
        ItemMeta mocked = new ItemFactoryMock().getItemMeta(Material.SPAWNER);
        ItemWrapper wrapper = ItemWrapper.wrap(mocked);
        //Check if random keys will affect deserialisation - it shouldn't!
        String testKey = "abbssjfk";
        long currentTime = System.currentTimeMillis();
        wrapper.setLong(testKey, currentTime);
        generator.writeToMeta(mocked);
        //Check meta
        try {
            Assert.assertEquals(generator, new TestGenerator(mocked));
        } catch (IllegalArgumentException ex) {
            Assert.fail("Unable to deserialise from ItemMeta!");
            return;
        }
        //Check invalid class test case
        try {
            wrapper.setString("Class", Class.class.getName());
            new TestGenerator(mocked);
            Assert.fail("Invalid class was not picked up!");
            return;
        } catch (IllegalStateException ignored) {
        }
        UUID id = generator.getGeneratorID();
        //Try data save
        generator.save();
        try {
            generator.updateFile();
        } catch (IllegalStateException ex) {
            Assert.fail("Unable to save file to disk!");
            return;
        }
        //Try serialising from YamlConfiguration.
        try {
            Assert.assertEquals(generator, new TestGenerator(id));
        } catch (IllegalArgumentException ex) {
            Assert.fail("Failed to read generator from disk, or values mismatch!");
        }
    }

    @Test()
    @Order(0)
    public void instantiationTest() {
        //Check if invalid levels are allowed
        try {
            new TestGenerator(-1, 1);
            Assert.fail("Invalid level values not caught!");
            return;
        } catch (IllegalArgumentException ignored) {
        }
        //Try if random UUID work, it should not.
        try {
            new TestGenerator(UUID.randomUUID());
            Assert.fail("Invalid UUID which does not exist was not caught!");
        } catch (IllegalArgumentException ignored) {
        }
    }
}
