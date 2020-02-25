package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class ChanceGenerator extends AbstractGenerator {

    public static final String GEN_CHANCE_HELPER_KEY = "GenerationChances";
    private List<GenerationChanceHelper> levelChance;

    protected ChanceGenerator(int maxLevel, int level) {
        this(maxLevel, level, Priority.NORMAL);
    }

    protected ChanceGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
        setupSpawnChances();
        fillSpawnChances(true);
    }

    protected ChanceGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    protected ChanceGenerator(ItemMeta meta) {
        super(meta);
        setupSpawnChances();
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        for (int index = 0; index < maxLevel(); index++) {
            String jsonMapped = wrapper.getString(GEN_CHANCE_HELPER_KEY + ":" + index);
            setSpawnChances(index, jsonMapped);
        }
    }

    public ChanceGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
        setupSpawnChances();
        ConfigurationSection section = getDataSection().getConfigurationSection("Levels");
        if (section == null) {
            fillSpawnChances(false);
            return;
        }
        for (int index = 0; index < maxLevel(); index++) {
            String jsonMapped = section.getString(GEN_CHANCE_HELPER_KEY + ":" + index);
            setSpawnChances(index, jsonMapped);
        }
    }

    private void setupSpawnChances() {
        levelChance = maxLevel() > 0 ? new ArrayList<>(maxLevel()) : new LinkedList<>();
    }

    @Override
    public void save() {
        super.save();
        ConfigurationSection section = getDataSection().createSection("Levels");
        for (int index = 0; index < maxLevel(); index++) {
            section.set(GEN_CHANCE_HELPER_KEY + ":" + index, levelChance.get(index).serialise());
        }
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        for (int index = 0; index < maxLevel(); index++) {
            GenerationChanceHelper chances = levelChance.get(index);
            wrapper.setString(GEN_CHANCE_HELPER_KEY + ":" + index, chances.serialise());
        }
    }

    public GenerationChanceHelper getSpawnChances() {
        return getSpawnChances(getLevel());
    }

    public GenerationChanceHelper getSpawnChances(int level) {
        return levelChance.get(level - 1);
    }

    public List<GenerationChanceHelper> getAllSpawnChances() {
        return new ArrayList<>(levelChance);
    }

    private void fillSpawnChances(boolean overwrite) {
        for (int index = 0; index < maxLevel(); index++) {
            if (levelChance.size() == index) {
                levelChance.add(index, new GenerationChanceHelper());
                continue;
            }
            if (levelChance.get(index) != null) {
                if (!overwrite) {
                    continue;
                }
                levelChance.add(index, new GenerationChanceHelper());
            } else {
                levelChance.set(index, new GenerationChanceHelper());
            }
        }
    }

    private void setSpawnChances(int level, String serial) {
        levelChance.set(level, new GenerationChanceHelper(serial));
    }
}
