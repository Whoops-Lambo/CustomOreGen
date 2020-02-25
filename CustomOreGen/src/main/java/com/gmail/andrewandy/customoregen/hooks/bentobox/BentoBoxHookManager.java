package com.gmail.andrewandy.customoregen.hooks.bentobox;

import com.gmail.andrewandy.customoregen.hooks.Hook;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.SkyblockHook;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.levels.IslandLevelsHook;

public class BentoBoxHookManager {

    private static BentoBoxHookManager instance;

    public static BentoBoxHookManager getInstance() {
        if (instance == null) {
            new BentoBoxHookManager();
        }
        return instance;
    }

    public void onEnable() {
        SkyblockHook.getInstance();
        IslandLevelsHook.getInstance();
        instance = this;
    }

    public void onDisable() {
        disableHooks(SkyblockHook.getInstance(), IslandLevelsHook.getInstance());
        instance = null;
    }

    private BentoBoxHookManager() {
        onEnable();
    }

    public static void reload() {
        if (instance == null) {
            getInstance();
        } else {
            instance.onDisable();
            getInstance();
        }
    }

    private void disableHooks(Hook... hooks) {
        for (Hook hook : hooks) {
            if (hook != null) {
                hook.onDisable();
            }
        }
    }
}
