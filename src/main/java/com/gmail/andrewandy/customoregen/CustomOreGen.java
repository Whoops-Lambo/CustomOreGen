package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.commands.BaseCommand;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceWrapper;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import com.gmail.andrewandy.customoregen.hooks.skyblock.BSkyblockHook;
import com.gmail.andrewandy.customoregen.listener.CobbleGeneratorHandler;
import com.gmail.andrewandy.customoregen.util.GeneratorManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.logging.Level;

public class CustomOreGen extends JavaPlugin {

    private static final String logPrefix = "&3[CustomOreGen]";
    private static final GeneratorManager generatorManager = new GeneratorManager();
    private static CustomOreGen instance;
    private static YamlConfiguration cfg;

    public static GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    public static CustomOreGen getInstance() {
        return instance;
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
        File saveFile = new File(getDataFolder().getAbsolutePath(), "generators.yml");
        if (!saveFile.isFile())
            try {
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
        BSkyblockHook.getInstance();
    }

    public YamlConfiguration getCfg() {
        return cfg;
    }

    public void loadConfig() throws IOException {
        InputStream stream = this.getClassLoader().getResourceAsStream("settings.yml");
        OutputStream outputStream = null;
        try {
            if (stream == null) {
                Common.log(Level.SEVERE, "&cUnable to locate settings file from jar!");
                return;
            }
            File folder = getDataFolder();
            if (!folder.isDirectory()) {
                folder.mkdir();
            }
            File file = new File(folder.getAbsolutePath(), "settings.yml");
            if (!file.isFile()) {
                if (!file.createNewFile()) {
                    Common.log(Level.SEVERE, "&caUnable to copy over the default settings!");
                    return;
                }
                //Copy contents
                byte[] buffer = new byte[1024];
                int length;
                outputStream = new FileOutputStream(file);
                while ((length = stream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
            }
            CustomOreGen.cfg = YamlConfiguration.loadConfiguration(file);
        } catch (IOException ex) {
            throw new IOException(ex);
        } finally {
            if (stream != null) {
                stream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    private void loadOverworldGenerator() {
        Common.log(Level.INFO, "&bLoading Settings for the Overworld Generator!");
        ConfigurationSection section = cfg.getConfigurationSection("OverworldSettings");
        if (section == null) {
            cfg.createSection("OverworldSettings");
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
            GenerationChanceWrapper chances = generator.getSpawnChances();
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
