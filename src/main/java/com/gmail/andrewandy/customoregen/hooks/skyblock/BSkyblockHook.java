package com.gmail.andrewandy.customoregen.hooks.skyblock;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceWrapper;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import com.gmail.andrewandy.customoregen.hooks.BentoBoxHook;
import com.gmail.andrewandy.customoregen.hooks.skyblock.generators.UniversalIslandGenerator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;

/**
 * Hooks into BSkyblock and enables
 */
public final class BSkyblockHook extends BentoBoxHook {

    private static BSkyblockHook instance;

    private BSkyblockHook() {
        super("BSkyblock");
        if (super.getAddon() == null) {
            Common.log(Level.INFO, "&a[Hooks] &eBSkyblock was not found.");
            return;
        }
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

    private void loadDefaultGenerator() {
        ConfigurationSection section = CustomOreGen.getInstance().getCfg().getConfigurationSection("IslandSettings");
        assert section != null;
        Priority priority;
        OverworldGenerator instance = OverworldGenerator.getInstance();
        priority = instance == null ? Priority.NORMAL : instance.getPriority().getNext();
        int maxLevel = section.getInt("MaxLevel");
        int currentLevel = section.getInt("CurrentLevel");
        UniversalIslandGenerator islandGenerator = new UniversalIslandGenerator(maxLevel, currentLevel, priority);
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
            GenerationChanceWrapper spawnChances = islandGenerator.getSpawnChances(index);
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
        UniversalIslandGenerator.setInstance(islandGenerator);
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
