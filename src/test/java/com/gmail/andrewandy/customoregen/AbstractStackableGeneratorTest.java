package com.gmail.andrewandy.customoregen;

import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock;
import com.gmail.andrewandy.customoregen.generator.AbstractStackableGenerator;
import com.gmail.andrewandy.customoregen.implementations.TestStackableGenerator;
import com.gmail.andrewandy.customoregen.util.Stackable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.Collection;
import java.util.HashSet;

public class AbstractStackableGeneratorTest {

    @Test()
    @Order(0)
    public void stackingTest() {
        int maxLevel = 10;
        AbstractStackableGenerator main = new TestStackableGenerator(15, 5);
        Collection<AbstractStackableGenerator> collection = new HashSet<>(maxLevel);
        for (int index = 1; index < maxLevel; index++) {
            AbstractStackableGenerator generator = new TestStackableGenerator(maxLevel, index);
            collection.add(generator);
            main.stack(generator.convertToStacked());
        }
        main.save();
        ItemMeta fakeMeta = new ItemMetaMock();
        main.writeToMeta(fakeMeta);
        AbstractStackableGenerator reconstructed = new TestStackableGenerator(fakeMeta);
        Assert.assertEquals(reconstructed, main);
        for (Stackable.StackedObject<ItemStack> stackedObject : reconstructed.getStacked()) {
            AbstractStackableGenerator generator = new TestStackableGenerator(stackedObject.getOriginal());
            Assert.assertTrue(collection.contains(generator));
        }
    }
}
