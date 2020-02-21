package com.gmail.andrewandy.customoregen.hooks.skyblock.generators;

import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceWrapper;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents an ore generator which only works on a specific island.
 */
public class IslandOreGenerator extends IslandRegionGenerator {

    private GenerationChanceWrapper spawnChances;


    public IslandOreGenerator(UUID generatorID) {
        super(generatorID);
        validateHook();
        String jsonMapped = getDataSection().getString("SpawnChanceWrapper");
        setSpawnChances(jsonMapped);
    }

    public IslandOreGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    public IslandOreGenerator(ItemMeta meta) {
        super(meta);
        validateHook();
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        String jsonMapped = wrapper.getString("SpawnChanceWrapper");
        setSpawnChances(jsonMapped);
    }

    public IslandOreGenerator(Island island, int maxLevel, int level) {
        super(island, maxLevel, level);
        validateHook();
    }

    public IslandOreGenerator(Island island, int maxLevel, int level, Priority priority) {
        super(island, maxLevel, level, priority);
        validateHook();
    }

    public IslandOreGenerator(String islandID, int maxLevel, int level) {
        super(islandID, maxLevel, level);
        validateHook();
    }

    public IslandOreGenerator(String islandID, int maxLevel, int level, Priority priority) {
        super(islandID, maxLevel, level, priority);
        validateHook();
    }

    private void setSpawnChances(String serial) {
        spawnChances = new GenerationChanceWrapper(serial);
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
        return withinRegion(location);
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
        spawnChances = spawnChances == null ? new GenerationChanceWrapper() : spawnChances;
        ConfigurationSection section = getDataSection();
        section.set("SpawnChanceWrapper", spawnChances.serialise());
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        //Mutates the original meta.
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        this.spawnChances = this.spawnChances == null ? new GenerationChanceWrapper() : this.spawnChances;
        wrapper.setString("SpawnChanceWrapper", this.spawnChances.serialise());
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
