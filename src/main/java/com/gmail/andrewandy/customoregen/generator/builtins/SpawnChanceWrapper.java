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

public class SpawnChanceWrapper {

    private static final Type blockStateChanceType = new TypeToken<Map<String, Integer>>() {
    }.getType();

    private Map<String, Integer> blockStateChances = new HashMap<>();
    private Map<int[], String> chanceMap = new HashMap<>();
    private int denominator = 0;

    public SpawnChanceWrapper() {
    }

    public SpawnChanceWrapper(String serial) {
        blockStateChances = new GsonBuilder().create().fromJson(serial, blockStateChanceType);
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
            int actualNumerator = originalNumerator * denominator;
            chanceMap.put(new int[]{currentNumerator++, currentNumerator += actualNumerator}, entry.getKey());
        }
    }

    /**
     * Add a chance for a block to be generated. Overwrites existing keys if present.
     *
     * @param block  The BlockData to be added.
     * @param chance The relative chance for the block to be added. See the skyblock_settings.yml for
     *               an example of this works.
     */
    public SpawnChanceWrapper addBlockChance(BlockData block, int chance) {
        if (blockStateChances.containsKey(block.getAsString())) {
            blockStateChances.replace(block.getAsString(), chance);
        } else {
            blockStateChances.put(block.getAsString(), chance);
        }
        recalculateChances();
        return this;
    }

    public SpawnChanceWrapper removeBlockChance(BlockData block) {
        blockStateChances.remove(block.getAsString());
        chanceMap.values().remove(block.getAsString());
        recalculateChances();
        return this;
    }

    public BlockData getRandomBlock() {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpawnChanceWrapper that = (SpawnChanceWrapper) o;
        return denominator == that.denominator &&
                Objects.equals(blockStateChances, that.blockStateChances) &&
                Objects.equals(chanceMap, that.chanceMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(blockStateChances, chanceMap, denominator);
    }
}
