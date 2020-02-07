package com.gmail.andrewandy.customoregen.hooks;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.stream.Collectors;

public class Hooks {

    private Collection<Hook> hooks = new HashSet<>();

    public Collection<Hook> getHooks() {
        return hooks.stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList());
    }

    public void registerHook(Hook hook) {
        this.hooks.add(hook);
    }


}
