package com.gmail.andrewandy.customoregen.hooks.economy;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.hooks.Hook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.logging.Level;

public class VaultHook implements Hook {

    private static VaultHook instance;
    private static Economy economy;

    private VaultHook() {
        Common.log(Level.INFO, "&a[Hooks] Attempting to hook into vault...");
        if (Bukkit.getPluginManager().getPlugin(getTargetPluginName()) == null) {
            Common.log(Level.WARNING, "&a[Hooks] &eUnable to hook into vault!");
            return;
        }
        Common.log(Level.INFO, "&a[Hooks] &eFound vault!");
        RegisteredServiceProvider<Economy> provider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (provider == null) {
            Common.log(Level.WARNING, "&a[Hooks] &eNo Economy Plugin Found!");
            return;
        }
        economy = provider.getProvider();
        instance = this;
        Common.log(Level.INFO, "&a[Hooks] &bHooked into vault!");
    }

    public static VaultHook getInstance() {
        if (instance == null) {
            new VaultHook();
        }
        return instance;
    }

    public static Economy getEconomy() {
        return economy;
    }


    @Override
    public String getTargetPluginName() {
        return "Vault";
    }

    @Override
    public boolean isEnabled() {
        return instance != null;
    }

    @Override
    public void onEnable() {
        getInstance();
    }

    @Override
    public void onDisable() {
        instance = null;
        Common.log(Level.WARNING, "&a[Hooks] &eVault hook is now disabled! Plugin may not function correctly.");
    }
}
