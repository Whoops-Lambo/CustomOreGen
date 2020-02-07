package com.gmail.andrewandy.customoregen.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public interface Hook extends Comparable<Hook> {

    String getTargetPluginName();
    default String getName() {
        return getClass().getSimpleName();
    }

    default Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin(getTargetPluginName());
    }
    boolean isEnabled();
    void onEnable();
    void onDisable();

    @Override
    default int compareTo(Hook o) {
        return getName().compareTo(Objects.requireNonNull(o).getName());
    }
}
