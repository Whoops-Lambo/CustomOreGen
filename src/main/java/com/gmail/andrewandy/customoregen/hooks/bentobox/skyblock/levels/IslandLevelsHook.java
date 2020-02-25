package com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.levels;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.hooks.bentobox.BentoBoxHook;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.SkyblockHook;
import world.bentobox.level.Level;

public class IslandLevelsHook extends BentoBoxHook {

    private static IslandLevelsHook instance;

    private IslandLevelsHook() {
        super("Level");
        if (!(getAddons()[0] instanceof Level)) {
            Common.log(java.util.logging.Level.INFO, "&a[Hooks] &bLevel Addon not found. Skipping...");
            instance = null;
        }
        if (SkyblockHook.getInstance() == null) {
            Common.log(java.util.logging.Level.WARNING, "&a[Hooks] &cNo Skyblock addon was found! Disabling.");
            instance = null;
            return;
        }
        instance = this;
        IslandLevelOreGenerator.registerListener();
        Common.log(java.util.logging.Level.INFO, "&a[Hooks] &e Sucessfully Hooked into Level.");
    }

    public static IslandLevelsHook getInstance() {
        if (instance == null) {
            new IslandLevelsHook();
        }
        return instance;
    }

    @Override
    public boolean isEnabled() {
        return instance != null;
    }

    @Override
    public void onEnable() {
        new IslandLevelsHook();
    }

    @Override
    public void onDisable() {
        IslandLevelOreGenerator.unregisterListener();
        instance = null;
    }
}
