package com.gmail.andrewandy.customoregen.generator.builtins;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class GenerationChanceHelper {

    private static final Type blockStateChanceType = new TypeToken<Map<String, Integer>>() {
    }.getType();

    private Map<String, Integer> blockStateChances = new HashMap<>();
    private Map<int[], String> chanceMap = new HashMap<>();
    private int denominator = 0;

    public GenerationChanceHelper() {
    }

    public GenerationChanceHelper(String serial) {
        blockStateChances = new GsonBuilder().create().fromJson(serial, blockStateChanceType);
        blockStateChances = blockStateChances == null ? new HashMap<>() : blockStateChances;
        recalculateChances();
    }

    public String serialise() {
        return new GsonBuilder().create().toJson(blockStateChances, blockStateChanceType);
    }

    public void recalculateChances() {
        int denominator = 0;
        for (Map.Entry<String, Integer> entry : blockStateChances.entrySet()) {
            denominator += entry.getValue();
        }
        this.denominator = denominator;
        int currentNumerator = 0;
        for (Map.Entry<String, Integer> entry : blockStateChances.entrySet()) {
            int originalNumerator = entry.getValue();
            chanceMap.put(new int[]{currentNumerator++, currentNumerator += originalNumerator}, entry.getKey());
        }
    }

    /**
     * Add a chance for a block to be generated. Overwrites existing keys if present.
     *
     * @param block  The BlockData to be added.
     * @param chance The relative chance for the block to be added. See the settings.yml for
     *               an example of this works.
     */
    public GenerationChanceHelper addBlockChance(BlockData block, int chance) {
        return addBlockChance(Objects.requireNonNull(block).getAsString(), chance);
    }

    public GenerationChanceHelper addBlockChance(String blockData, int chance) {
        if (blockStateChances.containsKey(Objects.requireNonNull(blockData))) {
            blockStateChances.replace(blockData, chance);
        } else {
            blockStateChances.put(blockData, chance);
        }
        recalculateChances();
        return this;
    }

    public GenerationChanceHelper removeBlockChance(BlockData block) {
        return removeBlockChance(Objects.requireNonNull(block));
    }

    public GenerationChanceHelper removeBlockChance(String block) {
        blockStateChances.remove(block);
        chanceMap.values().remove(block);
        recalculateChances();
        return this;
    }

    public BlockData getRandomBlock() {
        return Bukkit.createBlockData(getRandomRawBlockData());
    }

    public double getPercentageChance(String blockData) {
        return 100 * blockStateChances.get(blockData) / (double) denominator;
    }

    public String getRandomRawBlockData() {
        int randomNumerator = ThreadLocalRandom.current().nextInt(1, denominator);
        for (Map.Entry<int[], String> entry : chanceMap.entrySet()) {
            int lowerBound = entry.getKey()[0];
            int upperBound = entry.getKey()[1];
            if (randomNumerator >= lowerBound && randomNumerator <= upperBound) {
                return entry.getValue();
            }
        }
        throw new IllegalStateException("Unable to find block data!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenerationChanceHelper that = (GenerationChanceHelper) o;
        return denominator == that.denominator &&
                Objects.equals(blockStateChances, that.blockStateChances) &&
                Objects.equals(chanceMap, that.chanceMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockStateChances, chanceMap, denominator);
    }
}
