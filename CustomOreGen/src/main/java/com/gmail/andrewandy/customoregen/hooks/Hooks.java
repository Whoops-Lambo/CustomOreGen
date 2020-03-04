package com.gmail.andrewandy.customoregen.hooks;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public final class Hooks {

    private static Collection<Hook> hooks = new HashSet<>();

    public static void registerHook(Hook hook) {
        if (hook == null) {
            return;
        }
        unregisterHook(hook.getClass());
        hooks.add(hook);
    }

    public static void unregisterHook(Class<? extends Hook> clazz) {
        Objects.requireNonNull(clazz);
        hooks.removeIf((Hook target) -> target.getClass() == clazz);
    }

    public static void onEnable() {
        hooks.forEach(Hook::onEnable);
    }

    public static void onDisable() {
        hooks.forEach(Hook::onDisable);
    }

    public static void onReload() {
        onDisable();
        onEnable();
    }

}
