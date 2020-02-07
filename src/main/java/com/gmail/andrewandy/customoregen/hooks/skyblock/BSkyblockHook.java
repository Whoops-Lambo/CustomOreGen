package com.gmail.andrewandy.customoregen.hooks.skyblock;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.hooks.BentoBoxHook;

import java.util.logging.Level;

public final class BSkyblockHook extends BentoBoxHook {

    private static BSkyblockHook instance;

    private BSkyblockHook() {
        super("BSkyblock");
        if (super.getAddon() != null) {
            Common.log(Level.INFO, "[Hooks] &bHooked into BSkyblock!");
        }
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

    public static BSkyblockHook getInstance() {
        if (instance == null) {
            instance = new BSkyblockHook();
        }
        return instance;
    }
}
