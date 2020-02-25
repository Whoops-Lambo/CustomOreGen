package com.gmail.andrewandy.customoregen.addon;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.corelib.util.DeregisterableListener;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.addon.leveling.IslandLevelingManager;
import com.gmail.andrewandy.customoregen.addon.levels.IslandTemplateMapper;
import com.gmail.andrewandy.customoregen.addon.listener.IslandDataHandler;
import com.gmail.andrewandy.customoregen.addon.util.IslandTracker;
import com.gmail.andrewandy.customoregen.addon.util.IslandTrackingManager;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;

/**
 * Hooks into BSkyblock and enables
 */
public final class CustomOreGenAddon extends Addon {

    private static final List<String> SPECIAL_KEYS = Arrays.asList("COST");

    public static IslandOreGenerator defaultGenerator;
    private static CustomOreGenAddon instance;
    private final IslandTrackingManager trackingManager = new IslandTrackingManager();
    private DeregisterableListener islandDataHandler = new IslandDataHandler();

    private Collection<String> addonNames = Arrays.asList("BSkyblock", "AcidIsland", "CaveBlock");

    private CustomOreGenAddon() {
        Addon found = null;
        for (String addon : addonNames) {
            Optional<Addon> optionalAddon = BentoBox.getInstance().getAddonsManager().getAddonByName(addon);
            if (optionalAddon.isPresent()) {
                found = optionalAddon.get();
                break;
            }
        }
        if (found == null) {
            Common.log(Level.INFO, "&a[Hooks] &eNo Skyblock addon was not found.");
            return;
        }
        if (instance == null) {
            registerConfigurationSerialisation();
        }
        instance = this;
        loadIslandLevellingManager();
        loadDefaultGenerator();
        setupListeners();
        Common.log(Level.INFO, "&a[Hooks] &bSkyblock features enabled!");
    }

    public static CustomOreGenAddon getInstance() {
        if (instance == null) {
            new CustomOreGenAddon();
        }
        return instance;
    }

    public static IslandOreGenerator getDefaultIslandGenerator(String islandID) {
        if (defaultGenerator == null) {
            return null;
        }
        return new IslandOreGenerator(islandID, defaultGenerator.getLevel(), defaultGenerator.getMaxLevel(), defaultGenerator.getPriority());
    }

    public IslandTrackingManager getTrackingManager() {
        return trackingManager;
    }

    public IslandTracker getIslandTracker(String islandID) {
        return trackingManager.getTracker(islandID);
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(islandDataHandler, CustomOreGen.getInstance());
        ;
    }

    private void disableListeners() {
        islandDataHandler.disable();
    }

    private void registerConfigurationSerialisation() {
        ConfigurationSerialization.registerClass(IslandTracker.class);
        ConfigurationSerialization.registerClass(IslandTemplateMapper.class);
        ConfigurationSerialization.registerClass(IslandTrackingManager.class);
    }

    private void unregisterConfigurationSerialisation() {
        ConfigurationSerialization.unregisterClass(IslandTracker.class);
        ConfigurationSerialization.unregisterClass(IslandTemplateMapper.class);
        ConfigurationSerialization.unregisterClass(IslandTrackingManager.class);
    }

    private void loadIslandLevellingManager() {
        File file = CustomOreGen.getInstance().getDataFolder();
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
        section = section.getConfigurationSection("DEFAULT");
        if (section == null) {
            Common.log(Level.WARNING, "&eNo default generators found!");
            //TODO load all generators in defaults.
            return;
        }
        Priority priority;
        OverworldGenerator currentInstance = OverworldGenerator.getInstance();
        priority = currentInstance == null ? Priority.NORMAL : currentInstance.getPriority().getNext();
        int maxLevel = section.getInt("MaxLevel");
        int defaultLevel = section.getInt("DefaultLevel");
        IslandOreGenerator islandGenerator = new IslandOreGenerator("null", maxLevel, defaultLevel, priority);
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
                if (SPECIAL_KEYS.contains(key.toUpperCase())) {
                    continue;
                }
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
        unregisterConfigurationSerialisation();
        disableListeners();
        Common.log(Level.INFO, "[Hooks] &eBSkyblock hook has been disabled.");
    }
}
