package com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.generators;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.ChanceGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.SingleInstanceGenerator;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.BentoBox;

import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Represents a generator which fundamentally is the same as {@link IslandOreGenerator}
 * but works for all islands.
 */
public class UniversalIslandGenerator extends ChanceGenerator implements SingleInstanceGenerator {

    private static UniversalIslandGenerator instance;

    public UniversalIslandGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    public UniversalIslandGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    protected UniversalIslandGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    protected UniversalIslandGenerator(ItemMeta meta) {
        super(meta);
    }

    public UniversalIslandGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
    }

    public static UniversalIslandGenerator getInstance() {
        return instance;
    }

    public static void setInstance(UniversalIslandGenerator generator) {
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
        //True if an island exists at a given location.
        return BentoBox.getInstance().getIslands().getIslandAt(location).isPresent();
    }

    /**
     * This generator cannot be picked up!
     */
    @Override
    public ItemStack toItemStack() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This generator cannot be picked up!");
    }

    @Override
    public void save() {
        Common.log(Level.WARNING, "&e[Generators] Someone tried to save the universal island generator! This is unsupported.");
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        //Mutates the original meta.
        super.writeToMeta(original);
    }

    @Override
    public boolean isGlobal() {
        return true;
    }

}
