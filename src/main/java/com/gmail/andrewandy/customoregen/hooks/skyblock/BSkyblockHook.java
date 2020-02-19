package com.gmail.andrewandy.customoregen.hooks.skyblock;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.corelib.util.Config;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.UniversalIslandGenerator;
import com.gmail.andrewandy.customoregen.hooks.BentoBoxHook;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.logging.Level;

public final class BSkyblockHook extends BentoBoxHook {

    private static BSkyblockHook instance;
    private static Config skyblockConfig;

    private BSkyblockHook() {
        super("BSkyblock");
        if (super.getAddon() == null) {
            Common.log(Level.INFO, "[Hooks] &aBSkyblock was not found.");
            return;
        }
        skyblockConfig = new Config("settings.yml", CustomOreGen.getInstance());
        loadDefaultGenerator();
        Common.log(Level.INFO, "[Hooks] &bHooked into BSkyblock!");
    }

    public static Config getSkyblockConfig() {
        return skyblockConfig;
    }

    public static BSkyblockHook getInstance() {
        if (instance == null) {
            instance = new BSkyblockHook();
        }
        return instance;
    }

    private void loadDefaultGenerator() {
        ConfigurationSection section = skyblockConfig.getConfigurationSection("IslandSettings");
        assert section != null;
        Priority priority = Priority.valueOf(section.getString("Priority"));
        int maxLevel = section.getInt("MaxLevel");
        int currentLevel = section.getInt("CurrentLevel");
        UniversalIslandGenerator islandGenerator = new UniversalIslandGenerator(maxLevel, currentLevel, priority);
        for (int index = 0; index < maxLevel; index++) {
            ConfigurationSection level = section.getConfigurationSection("" + index);
            if (level == null) {
                Common.log(Level.WARNING, "[Hooks] &cEmpty Level section found! Skipping...");
                continue;
            }
            for (String key : level.getKeys(false)) {
                int chance = level.getInt(key);
                Material material = Material.getMaterial(key);
                if (material == null) {
                    Common.log(Level.WARNING, "[Hooks] &cInvalid Material found! Skipping...");
                    continue;
                }
                islandGenerator.getSpawnChanceWrapper().addBlockChance(material.createBlockData(), chance);
            }
        }
        UniversalIslandGenerator.setInstance(islandGenerator);
    }

    @Override
    public void onEnable() {
        getInstance();
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
