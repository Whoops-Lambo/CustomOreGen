package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.corelib.util.Common;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class CustomOreGen extends JavaPlugin {

    private static final String logPrefix = "&3[CustomOreGen]";
    private static final GeneratorManager generatorManager = new GeneratorManager();
    private static CustomOreGen instance;

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
        Common.log(Level.INFO, "&b Plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Common.log(Level.INFO, "&e Plugin has been disabled.");
    }
}
