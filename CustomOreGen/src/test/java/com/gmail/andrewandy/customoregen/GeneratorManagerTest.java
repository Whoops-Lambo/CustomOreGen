package com.gmail.andrewandy.customoregen;

import be.seeseemelk.mockbukkit.WorldMock;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.implementations.TestGenerator;
import com.gmail.andrewandy.customoregen.util.GeneratorManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorManagerTest {

    private static final World mocked = new WorldMock();
    private static final Location evenLocation = new Location(mocked, 0, 0, 0);
    private static final Location oddLocation = new Location(mocked, 1, 1, 1);
    private static GeneratorManager testManager = new GeneratorManager();
    private static Collection<OddLocationGenerator> oddGenerators = new HashSet<>();

    @Test()
    public void testAdd() {
        int maxLevel = ThreadLocalRandom.current().nextInt(2, 50);
        for (int index = 1; index < maxLevel - 1; index++) {
            OddLocationGenerator generator = new OddLocationGenerator(maxLevel, index);
            oddGenerators.add(generator);
            testManager.registerGenerator(generator);
        }
        Assert.assertTrue(testManager.getGeneratorsAt(oddLocation).containsAll(oddGenerators));
        Assert.assertFalse(testManager.getGeneratorsAt(evenLocation).containsAll(oddGenerators));
    }

    @Test
    @Order(1)
    public void testRemove() {
        Iterator<OddLocationGenerator> iterator = oddGenerators.iterator();
        OddLocationGenerator generator = iterator.next();
        //Try removing a single generator
        testManager.unregisterGenerator(generator);
        Assert.assertFalse(testManager.getGeneratorsAt(oddLocation).contains(generator));
        //Try removing all even location generators.
        testManager.unregisterAllActiveAt(evenLocation);
        oddGenerators.remove(generator);
        OddLocationGenerator evenGen = new OddLocationGenerator(4, 2);
        evenGen.setCheckOdd(false);
        testManager.registerGenerator(evenGen);
        //Try retaining all even location generators.
        Collection<BlockGenerator> removed = testManager.retainAllActiveAt(evenLocation);
        Assert.assertTrue(removed.containsAll(oddGenerators) && !removed.contains(evenGen));
        //Test if the even generator was persisted.
        Assert.assertTrue(testManager.getGeneratorsAt(evenLocation).contains(evenGen));
        //Check if retaining all generators active at an odd location removed the even generator.
        removed = testManager.retainAllActiveAt(oddLocation);
        Assert.assertTrue(removed.contains(evenGen));
    }

    private static class OddLocationGenerator extends TestGenerator {

        private boolean checkOdd = true;

        public OddLocationGenerator(ItemStack fromItem) {
            super(fromItem);
        }

        public OddLocationGenerator(ItemMeta fromMeta) {
            super(fromMeta);
        }

        public OddLocationGenerator(int maxLevel, int level) {
            super(maxLevel, level);
        }

        public OddLocationGenerator(UUID fromID) throws IllegalArgumentException {
            super(fromID);
        }

        public void setCheckOdd(boolean checkOdd) {
            this.checkOdd = checkOdd;
        }

        @Override
        public boolean isActiveAtLocation(Location location) {
            boolean isAllEven = location.getBlockX() % 2 == 0 && location.getBlockY() % 2 == 0 && location.getBlockY() % 2 == 0;
            return checkOdd != isAllEven;
        }
    }


}
