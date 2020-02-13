package com.gmail.andrewandy.customoregen.generator.builtins;

import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.SingleInstanceGenerator;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Represents the default cobblestone generator for the overworld.
 */
public class OverworldGenerator extends AbstractGenerator implements SingleInstanceGenerator {

    private static OverworldGenerator instance;

    public OverworldGenerator(int maxLevel, int level, OverworldGenerator instance) {
        super(maxLevel, level);
    }

    public OverworldGenerator(int maxLevel, int level, Priority priority, OverworldGenerator instance) {
        super(maxLevel, level, priority);
    }

    public OverworldGenerator(ItemStack itemStack, OverworldGenerator instance) {
        super(itemStack);
    }

    public OverworldGenerator(ItemMeta meta, OverworldGenerator instance) {
        super(meta);
    }

    public OverworldGenerator(UUID fromID, OverworldGenerator instance) throws IllegalArgumentException {
        super(fromID);
    }

    public static OverworldGenerator getInstance() {
        return instance;
    }

    public static void setInstance(OverworldGenerator generator) {
        instance = generator;
    }

    @Override
    public BlockData generateBlockAt(Location location) {
        return null;
    }

    @Override
    public boolean isActiveAtLocation(Location location) {
        return false;
    }

    @Override
    public ItemStack toItemStack() {
        return null;
    }
}
