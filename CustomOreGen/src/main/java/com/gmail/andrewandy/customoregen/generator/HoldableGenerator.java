package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.customoregen.util.ItemHoldable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public abstract class HoldableGenerator extends ChanceGenerator implements PlaceableGenerator, ItemHoldable {

    protected HoldableGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    protected HoldableGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    protected HoldableGenerator(ItemStack itemStack) {
        super(itemStack);
    }

    protected HoldableGenerator(ItemMeta meta) {
        super(meta);
    }

    public HoldableGenerator(UUID fromID) throws IllegalArgumentException {
        super(fromID);
    }

}
