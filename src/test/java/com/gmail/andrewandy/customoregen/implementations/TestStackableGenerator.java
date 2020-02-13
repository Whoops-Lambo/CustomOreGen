package com.gmail.andrewandy.customoregen.implementations;

import com.gmail.andrewandy.customoregen.generator.AbstractStackableGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TestStackableGenerator extends AbstractStackableGenerator {

    public TestStackableGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    public TestStackableGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    public TestStackableGenerator(ItemStack itemStack) {
        super(itemStack);
    }

    public TestStackableGenerator(ItemMeta itemMeta) {
        super(itemMeta);
    }

    public TestStackableGenerator(UUID fromID) {
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
