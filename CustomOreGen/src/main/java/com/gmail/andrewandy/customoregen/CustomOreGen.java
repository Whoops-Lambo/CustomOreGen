package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.commands.BaseCommand;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import com.gmail.andrewandy.customoregen.hooks.economy.VaultHook;
import com.gmail.andrewandy.customoregen.listener.CobbleGeneratorHandler;
import com.gmail.andrewandy.customoregen.util.DataContainer;
import com.gmail.andrewandy.customoregen.util.FileUtil;
import com.gmail.andrewandy.customoregen.util.GeneratorManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

public class CustomOreGen extends JavaPlugin {

    private static final String logPrefix = "&3[CustomOreGen]";
    private static final GeneratorManager generatorManager = new GeneratorManager();
    private static CustomOreGen instance;
    private static YamlConfiguration settings, defaults;

    public static GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public static CustomOreGen getInstance() {
        return instance;
    }

    public static YamlConfiguration getSettings() {
        return settings;
    }

    public static YamlConfiguration getDefaults() {
        return defaults;
    }

    public static void loadConfig() throws IOException {
        ClassLoader classLoader = CustomOreGen.class.getClassLoader();
        try (InputStream defaultsStream = classLoader.getResourceAsStream("defaults.yml");
             InputStream settingsStream = classLoader.getResourceAsStream("settings.yml")) {
            if (defaultsStream == null) {
                Common.log(Level.SEVERE, "&cUnable to locate defaults file from jar!");
                return;
            }
            if (settingsStream == null) {
                Common.log(Level.SEVERE, "&cUnable to locate settings file from jar!");
                return;
            }
            File folder = getInstance().getDataFolder();
            if (!folder.isDirectory()) {
                folder.mkdir();
            }
            File settingsFile = new File(folder.getAbsolutePath(), "settings.yml");
            File defaultsFile = new File(folder.getAbsolutePath(), "defaults.yml");
            if (!settingsFile.isFile()) {
                if (!settingsFile.createNewFile()) {
                    Common.log(Level.SEVERE, "&caUnable to copy over the builtin settings!");
                    return;
                }
                //Copy contents
                FileUtil.copy(settingsStream, settingsFile);
            }
            if (!defaultsFile.isFile()) {
                if (!defaultsFile.createNewFile()) {
                    Common.log(Level.SEVERE, "&caUnable to copy over the defaults!");
                    return;
                }
                //Copy contents
                FileUtil.copy(defaultsStream, defaultsFile);
            }
            settings = YamlConfiguration.loadConfiguration(settingsFile);
            defaults = YamlConfiguration.loadConfiguration(defaultsFile);
        }
    }

    @Override
    public void onEnable() {
        long time = System.currentTimeMillis();
        super.onEnable();
        instance = this;
        Common.setPrefix(logPrefix);
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadUtils();
        setupAbstractGenerator();
        loadOverworldGenerator();
        setupCobbleHandler();
        loadHooks();
        getCommand("CustomOreGen").setExecutor(BaseCommand.getInstance());
        Common.log(Level.INFO, "&bPlugin has been enabled! Took " + (System.currentTimeMillis() - time) + "ms");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        AbstractGenerator.globalUpdateFile();
        Common.log(Level.INFO, "&ePlugin has been disabled.");
    }

    private void setupAbstractGenerator() {
        File dataFolder = getDataFolder();
        if (!dataFolder.isDirectory()) {
            dataFolder.mkdir();
        }
        File saveFile = new File(dataFolder.getAbsolutePath(), "generators.yml");
        try {
            if (!saveFile.isFile()) {
                saveFile.createNewFile();
            }
            AbstractGenerator.setDataFile(saveFile);
        } catch (IOException ex) {
            ex.printStackTrace();
            Common.log(Level.SEVERE, "&cUnable to set up generator database!");
        }
    }

    private void setupCobbleHandler() {
        Common.log(Level.INFO, "&BSetting up generation listener");
        getServer().getPluginManager().registerEvents(new CobbleGeneratorHandler(), instance);
    }

    private void loadHooks() {
        if (VaultHook.getInstance() == null) {
            Common.log(Level.WARNING, "&aPlugin cannot function without vault!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
    }

    private void loadUtils() {
        ConfigurationSerialization.registerClass(DataContainer.class);
    }

    private void loadOverworldGenerator() {
        Common.log(Level.INFO, "&bLoading Settings for the Overworld Generator!");
        ConfigurationSection section = defaults.getConfigurationSection("OverworldSettings");
        if (section == null) {
            Common.log(Level.WARNING, "&bNo Overworld generator found in defaults file! Skipping.");
            return;
        }
        Priority priority = Priority.NORMAL;
        int maxLevel = section.getInt("MaxLevel");
        int currentLevel = section.getInt("CurrentLevel");
        if (maxLevel < 1 || currentLevel > maxLevel) {
            Common.log(Level.SEVERE, "&cInvalid Config file detected!",
                    "&cMax level is less than 1 or current level is greater than the max level!");
            return;
        }
        OverworldGenerator generator = new OverworldGenerator(maxLevel, currentLevel, priority);
        ConfigurationSection levels = section.getConfigurationSection("Levels");
        if (levels != null) {
            levels = levels.getConfigurationSection("1");
            GenerationChanceHelper chances = generator.getSpawnChances();
            assert levels != null;
            for (String key : levels.getKeys(false)) {
                try {
                    Material material = Material.getMaterial(key);
                    int numerator = levels.getInt(key);
                    if (material == null || numerator < 1 || !material.isBlock()) {
                        Common.log(Level.WARNING, "&eFound invalid block spawn. Skipping now.");
                        continue;
                    }
                    chances.addBlockChance(Bukkit.createBlockData(material), numerator);
                } catch (IllegalArgumentException ex) {
                    Common.log(Level.WARNING, "&eFound invalid block spawn. Skipping now.");
                }
            }
        } else {
            Common.log(Level.INFO, "&aFound an empty levels section for the overworld generator, skipping...");
        }
        OverworldGenerator.setInstance(generator);
        Common.log(Level.INFO, "&b[Generators] Loaded the Overworld Generator!");
    }
}
