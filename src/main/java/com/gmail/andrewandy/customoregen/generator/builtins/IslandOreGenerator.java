package com.gmail.andrewandy.customoregen.generator.builtins;

import com.gmail.andrewandy.customoregen.generator.IslandRegionGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import world.bentobox.bentobox.database.objects.Island;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class IslandOreGenerator extends IslandRegionGenerator {

    private Map<String, Integer> blockStateChances = new HashMap<>();
    private Map<int[], String> chanceMap = new HashMap<>();
    private int denominator = 0;


    public IslandOreGenerator(UUID generatorID) {
        super(generatorID);
    }

    public IslandOreGenerator(Island island, int maxLevel, int level) {
        super(island, maxLevel, level);
    }

    public IslandOreGenerator(String islandID, int maxLevel, int level) {
        super(islandID, maxLevel, level);
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
    public IslandOreGenerator addBlockChance(BlockData block, int chance) {
        if (blockStateChances.containsKey(block.getAsString())) {
            blockStateChances.replace(block.getAsString(), chance);
        } else {
            blockStateChances.put(block.getAsString(), chance);
        }
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
    public Block generateBlockAt(Location location) {
        BlockData rawData = getRandomBlock();
        Block block = location.getBlock();
        block.setBlockData(rawData);
        return block;
    }

    @Override
    public boolean isActiveAtLocation(Location location) {
        return withinRegion(location);
    }

    /**
     * This generator cannot be picked up!
     */
    @Override
    public ItemStack toItemStack() {
        throw new UnsupportedOperationException();
    }
}
