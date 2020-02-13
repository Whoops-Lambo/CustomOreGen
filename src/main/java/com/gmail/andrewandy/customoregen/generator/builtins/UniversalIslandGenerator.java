package com.gmail.andrewandy.customoregen.generator.builtins;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.SingleInstanceGenerator;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.BentoBox;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a generator which fundamentally is the same as {@link com.gmail.andrewandy.customoregen.generator.builtins.IslandOreGenerator}
 * but works for all islands.
 */
public class UniversalIslandGenerator extends AbstractGenerator implements SingleInstanceGenerator {

    private static UniversalIslandGenerator instance;
    private SpawnChanceWrapper spawnChances;

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
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        String jsonMapped = wrapper.getString("SpawnChanceWrapper");
        setSpawnChances(jsonMapped);
    }

    public UniversalIslandGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
        String jsonMapped = getDataSection().getString("BlockStateChances");
        setSpawnChances(jsonMapped);
    }

    public static UniversalIslandGenerator getInstance() {
        return instance;
    }

    public static void setInstance(UniversalIslandGenerator generator) {
        CustomOreGen.getGeneratorManager().unregisterUniversalGenerator(UniversalIslandGenerator.class);
        if (instance != null) {
            instance = generator;
            CustomOreGen.getGeneratorManager().registerUniversalGenerator(instance, true);
        }
    }

    /**
     * Internal method.
     *
     * @param serial The serialised form of an {@link SpawnChanceWrapper}
     */
    private void setSpawnChances(String serial) {
        spawnChances = new SpawnChanceWrapper(serial);
    }

    public SpawnChanceWrapper getSpawnChanceWrapper() {
        return spawnChances;
    }

    @Override
    public BlockData generateBlockAt(Location location) {
        if (!isActiveAtLocation(location) || spawnChances == null) {
            return null;
        }
        return spawnChances.getRandomBlock();
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
        super.save();
        ConfigurationSection section = getDataSection();
        section.set("SpawnChanceWrapper", spawnChances.serialise());
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        //Mutates the original meta.
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        wrapper.setString("SpawnChanceWrapper", spawnChances.serialise());
    }
}
