package com.gmail.andrewandy.customoregen.hooks.skyblock;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import com.gmail.andrewandy.customoregen.hooks.BentoBoxHook;
import com.gmail.andrewandy.customoregen.hooks.skyblock.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.hooks.skyblock.leveling.IslandLevelingManager;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Hooks into BSkyblock and enables
 */
public final class BSkyblockHook extends BentoBoxHook {

    public static IslandOreGenerator defaultGenerator;
    private static BSkyblockHook instance;


    private BSkyblockHook() {
        super("BSkyblock");
        if (super.getAddon() == null) {
            Common.log(Level.INFO, "&a[Hooks] &eBSkyblock was not found.");
            return;
        }

        ConfigurationSerialization.registerClass(IslandTracker.class);
        loadIslandLevellingManager();
        loadDefaultGenerator();
        Common.log(Level.INFO, "&a[Hooks] &bHooked into BSkyblock!");
        instance = this;
    }

    public static BSkyblockHook getInstance() {
        if (instance == null) {
            new BSkyblockHook();
        }
        return instance;
    }

    public static IslandOreGenerator getDefaultGenerator() {
        return defaultGenerator;
    }

    private void setupListeners() {

    }

    private void loadIslandLevellingManager() {
        File file = getAddon().getDataFolder().getAbsoluteFile();
        File data = new File(file.getAbsolutePath(), "IslandLevelData.yml");
        try {
            if (!data.isFile()) {
                data.createNewFile();
            }
            IslandLevelingManager.getInstance().loadFromFile(data);
        } catch (IOException | InvalidConfigurationException ex) {
            Common.log(Level.SEVERE, ex.getMessage());
            Common.log(Level.SEVERE, "&cUnable to load Island Leveling Data!");
        }
    }


    private void loadDefaultGenerator() {
        ConfigurationSection section = CustomOreGen.getDefaults().getConfigurationSection("IslandSettings");
        assert section != null;
        Priority priority;
        OverworldGenerator instance = OverworldGenerator.getInstance();
        priority = instance == null ? Priority.NORMAL : instance.getPriority().getNext();
        int maxLevel = section.getInt("MaxLevel");
        int currentLevel = section.getInt("CurrentLevel");
        IslandOreGenerator islandGenerator = new IslandOreGenerator("null", maxLevel, currentLevel, priority);
        ConfigurationSection levelSection = section.getConfigurationSection("Levels");
        if (levelSection == null) {
            Common.log(Level.WARNING, "[&aHooks] &eEmpty level section found! Skipping this generator.");
            return;
        }
        for (int index = 1; index <= maxLevel; index++) {
            ConfigurationSection level = levelSection.getConfigurationSection("" + index);
            if (level == null) {
                Common.log(Level.WARNING, "&a[Hooks] &cEmpty Level section found! Skipping...");
                continue;
            }
            GenerationChanceHelper spawnChances = islandGenerator.getSpawnChances(index);
            for (String key : level.getKeys(false)) {
                int chance = level.getInt(key);
                Material material = Material.getMaterial(key);
                if (material == null || !material.isBlock() || chance < 1) {
                    Common.log(Level.WARNING, "&a[Hooks] &cInvalid Material found! Skipping...");
                    continue;
                }
                spawnChances.addBlockChance(material.createBlockData(), chance);
            }
        }
        defaultGenerator = islandGenerator;
    }

    @Override
    public void onEnable() {
        loadDefaultGenerator();
    }

    @Override
    public boolean isEnabled() {
        return instance != null;
    }

    @Override
    public void onDisable() {
        Common.log(Level.INFO, "[Hooks] &eBSkyblock hook has been disabled.");
    }
}
