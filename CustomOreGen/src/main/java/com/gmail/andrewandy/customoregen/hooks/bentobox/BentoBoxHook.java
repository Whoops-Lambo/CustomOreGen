package com.gmail.andrewandy.customoregen.hooks.bentobox;

import com.gmail.andrewandy.customoregen.hooks.Hook;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.Addon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public abstract class BentoBoxHook implements Hook {

    private Addon[] addon;

    protected BentoBoxHook(String... targetAddonNames) {
        this(false, targetAddonNames);
    }

    protected BentoBoxHook(boolean allRequired, String... targetAddonNames) {
        if (getPlugin() == null) {
            throw new IllegalStateException("Unable to Hook into BentoBox");
        }
        Collection<Addon> addons = new ArrayList<>(targetAddonNames.length);
        for (String s : targetAddonNames) {
            Optional<Addon> optionalAddon = BentoBox.getInstance().getAddonsManager().getAddonByName(s);
            if (!allRequired) {
                optionalAddon.ifPresent(addons::add);
            } else {
                addons.add(optionalAddon.orElse(null));
            }
        }
        this.addon = addons.toArray(new Addon[0]);
    }

    @Override
    public String getTargetPluginName() {
        return "BentoBox";
    }

    public Addon[] getAddons() {
        return addon;
    }
}
