package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.BentoBox;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a generator which fundamentally is the same as {@link com.gmail.andrewandy.customoregen.generator.builtins.IslandOreGenerator}
 * but works for all islands.
 */
public class UniversalIslandGenerator extends AbstractGenerator {

    private static final Type blockStateChanceType = new TypeToken<Map<String, Integer>>() {
    }.getType();

    private Map<String, Integer> blockStateChances = new HashMap<>();
    private Map<int[], String> chanceMap = new HashMap<>();
    private int denominator = 0;

    protected UniversalIslandGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    protected UniversalIslandGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    protected UniversalIslandGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    protected UniversalIslandGenerator(ItemMeta meta) {
        super(meta);
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        String jsonMapped = wrapper.getString("BlockStateChances");
        this.blockStateChances = new GsonBuilder().create().fromJson(jsonMapped, blockStateChanceType);
    }

    public UniversalIslandGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
        String jsonMapped = getDataSection().getString("BlockStateChances");
        this.blockStateChances = new GsonBuilder().create().fromJson(jsonMapped, blockStateChanceType);
        calculateChances();
    }

    private void calculateChances() {
        int denominator = 0;
        for (Map.Entry<String, Integer> entry : blockStateChances.entrySet()) {
            denominator += entry.getValue();
        }
        this.denominator = denominator;
        int currentNumerator = 0;
        for (Map.Entry<String, Integer> entry : blockStateChances.entrySet()) {
            int originalNumerator = entry.getValue();
            int actualNumerator = originalNumerator * denominator;
            chanceMap.put(new int[]{currentNumerator++, currentNumerator += actualNumerator}, entry.getKey());
        }
    }

    /**
     * Add a chance for a block to be generated. Overwrites existing keys if present.
     *
     * @param block  The BlockData to be added.
     * @param chance The relative chance for the block to be added. See the settings.yml for
     *               an example of this works.
     */
    public UniversalIslandGenerator addBlockChance(BlockData block, int chance) {
        if (blockStateChances.containsKey(block.getAsString())) {
            blockStateChances.replace(block.getAsString(), chance);
        } else {
            blockStateChances.put(block.getAsString(), chance);
        }
        calculateChances();
        return this;
    }

    public UniversalIslandGenerator removeBlockChance(BlockData block) {
        blockStateChances.remove(block.getAsString());
        chanceMap.values().remove(block.getAsString());
        calculateChances();
        return this;
    }

    private BlockData getRandomBlock() {
        int randomNumerator = ThreadLocalRandom.current().nextInt(denominator);
        for (Map.Entry<int[], String> entry : chanceMap.entrySet()) {
            int lowerBound = entry.getKey()[0];
            int upperBound = entry.getKey()[1];
            if (randomNumerator >= lowerBound && randomNumerator <= upperBound) {
                String raw = entry.getValue();
                return Bukkit.createBlockData(raw);
            }
        }
        throw new IllegalStateException("Unable to find block data!");
    }

    @Override
    public BlockData generateBlockAt(Location location) {
        if (!isActiveAtLocation(location)) {
            return null;
        }
        return getRandomBlock();
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
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(blockStateChances, blockStateChanceType);
        ConfigurationSection section = getDataSection();
        section.set("BlockStateChances", json);
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        //Mutates the original meta.
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(blockStateChances, blockStateChanceType);
        wrapper.setString("BlockStateChances", json);
    }
}
