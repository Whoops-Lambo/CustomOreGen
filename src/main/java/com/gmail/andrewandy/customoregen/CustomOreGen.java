package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.OverworldGenerator;
import com.gmail.andrewandy.customoregen.util.GeneratorManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        super.onEnable();
        instance = this;
        Common.setPrefix(logPrefix);
        try {
            loadConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadOverworldGenerator();
        Common.log(Level.INFO, "&b Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Common.log(Level.INFO, "&e Plugin has been disabled.");
    }

    private void loadConfig() throws IOException {
        URL url = this.getClassLoader().getResource("settings.yml");
        URI uri;
        try {
            uri = url.toURI(); //Throws nullpointer
        } catch (URISyntaxException ex) {
            throw new IllegalStateException(ex);
        }
        Path path = Paths.get(uri);
        File folder = getDataFolder();
        File file = new File(folder.getAbsolutePath(), "settings.yml");
        if (!file.isFile()) {
            if (!file.createNewFile()) {
                Common.log(Level.SEVERE, "&caUnable to copy over the default settings!");
                return;
            }
        }
        //Copy the data.
        Files.copy(path, new FileOutputStream(file));
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    private void loadOverworldGenerator() {
        ConfigurationSection section = cfg.getConfigurationSection("OverworldSettings");
        if (section == null) {
            cfg.createSection("OverworldSettings");
        }
        Priority priority = Priority.valueOf(cfg.getString("Priority"));
        int maxLevel = cfg.getInt("MaxLevel");
        int currentLevel = cfg.getInt("CurrentLevel");
        if (maxLevel < 0 || currentLevel > maxLevel) {
            Common.log(Level.SEVERE, "&cInvalid Config file detected!",
                    "&cMax level is less than 0 or current level is greater than the max level!");
            return;
        }
        OverworldGenerator.setInstance(new OverworldGenerator(maxLevel, currentLevel, priority));
        Common.log(Level.INFO, "&b[Generators] Loaded the Overworld Generator!");
    }
}
