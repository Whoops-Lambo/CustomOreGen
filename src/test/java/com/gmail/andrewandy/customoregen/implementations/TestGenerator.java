package com.gmail.andrewandy.customoregen.implementations;

import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class TestGenerator extends AbstractGenerator {

    public TestGenerator(ItemStack fromItem) {
        super(fromItem);
    }

    public TestGenerator(ItemMeta fromMeta) {
        super(fromMeta);
    }

    public TestGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    public TestGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
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
        return super.toBaseItem(Material.SPAWNER);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean isGlobal() {
        return true;
    }
}
