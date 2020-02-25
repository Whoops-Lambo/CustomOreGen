package com.gmail.andrewandy.customoregen.hooks.bentobox;

import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.SkyblockHook;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.levels.IslandLevelsHook;
import world.bentobox.bentobox.api.addons.Addon;

public class CustomOreGenAddon extends Addon {

    private static CustomOreGenAddon instance;

    public static CustomOreGenAddon getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        SkyblockHook.getInstance();
        IslandLevelsHook.getInstance();
        instance = this;
    }

    @Override
    public void onDisable() {
        instance = null;
    }
}
