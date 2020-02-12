package com.gmail.andrewandy.customoregen;

import be.seeseemelk.mockbukkit.inventory.meta.ItemMetaMock;
import com.gmail.andrewandy.customoregen.generator.AbstractStackableGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.util.Stackable;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class AbstractStackableGeneratorTest {

    @Test()
    @Order(0)
    public void stackingTest() {
        int maxLevel = 10;
        AbstractStackableGenerator main = new StackImpl(15, 5);
        Collection<AbstractStackableGenerator> collection = new HashSet<>(maxLevel);
        for (int index = 1; index < maxLevel; index++) {
            AbstractStackableGenerator generator = new StackImpl(maxLevel, index);
            collection.add(generator);
            main.stack(generator.convertToStacked());
        }
        main.save();
        ItemMeta fakeMeta = new ItemMetaMock();
        main.writeToMeta(fakeMeta);
        AbstractStackableGenerator reconstructed = new StackImpl(fakeMeta);
        Assert.assertEquals(reconstructed, main);
        for (Stackable.StackedObject<ItemStack> stackedObject : reconstructed.getStacked()) {
            AbstractStackableGenerator generator = new StackImpl(stackedObject.getOriginal());
            Assert.assertTrue(collection.contains(generator));
        }
    }


    static class StackImpl extends AbstractStackableGenerator {


        protected StackImpl(int maxLevel, int level) {
            super(maxLevel, level);
        }

        public StackImpl(int maxLevel, int level, Priority priority) {
            super(maxLevel, level, priority);
        }

        public StackImpl(ItemStack itemStack) {
            super(itemStack);
        }

        public StackImpl(ItemMeta itemMeta) {
            super(itemMeta);
        }

        public StackImpl(UUID fromID) {
            super(fromID);
        }

        @Override
        public boolean canStack(ItemStack itemStack) {
            return true;
        }

        @Override
        public BlockData generateBlockAt(Location location) {
            return null;
        }

        @Override
        public boolean isActiveAtLocation(Location location) {
            return true;
        }

        @Override
        public ItemStack toItemStack() {
            return null;
        }

        @Override
        public void writeToMeta(ItemMeta original) {
            super.writeToMeta(original);
        }

        @Override
        public StackedObject<ItemStack> convertToStacked() {
            return new StackedObject<>(toItemStack());
        }
    }

}
