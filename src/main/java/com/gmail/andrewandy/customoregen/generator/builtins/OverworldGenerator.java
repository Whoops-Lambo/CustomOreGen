package com.gmail.andrewandy.customoregen.generator.builtins;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.ChanceGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.SingleInstanceGenerator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

/**
 * Represents the default cobblestone generator for the overworld.
 */
public class OverworldGenerator extends ChanceGenerator implements SingleInstanceGenerator {

    private static OverworldGenerator instance;

    public OverworldGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    public OverworldGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    public OverworldGenerator(ItemStack itemStack) {
        super(itemStack);
    }

    public OverworldGenerator(ItemMeta meta) {
        super(meta);
    }

    public OverworldGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
    }

    public static OverworldGenerator getInstance() {
        return instance;
    }

    public static void setInstance(OverworldGenerator generator) {
        instance = generator;
        CustomOreGen.getGeneratorManager().registerUniversalGenerator(instance, true);
    }

    @Override
    public BlockData generateBlockAt(Location location) {
        if (!isActiveAtLocation(location)) {
            return null;
        }
        return getSpawnChances().getRandomBlock();
    }

    @Override
    public boolean isActiveAtLocation(Location location) {
        if (location == null) {
            return false;

        }
        return location.getWorld().getEnvironment().equals(World.Environment.NORMAL);
    }

    @Override
    public ItemStack toItemStack() {
        throw new UnsupportedOperationException("Generator cannot be picked up!");
    }
}
