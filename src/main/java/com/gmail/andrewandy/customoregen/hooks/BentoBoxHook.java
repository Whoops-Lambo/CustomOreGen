package com.gmail.andrewandy.customoregen.hooks;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;

import java.util.Optional;

public abstract class BentoBoxHook implements Hook {

    private Addon addon;

    protected BentoBoxHook(String targetAddonName) {
        if (getPlugin() == null) {
            throw new IllegalStateException("Unable to Hook into BentoBox");
        }
        Optional<Addon> optionalAddon = BentoBox.getInstance().getAddonsManager().getAddonByName(targetAddonName);
        this.addon = optionalAddon.orElse(null);
    }

    @Override
    public String getTargetPluginName() {
        return "BentoBox";
    }

    public Addon getAddon() {
        return addon;
    }
}
