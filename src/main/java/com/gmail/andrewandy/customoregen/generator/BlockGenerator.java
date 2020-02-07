package com.gmail.andrewandy.customoregen.generator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface BlockGenerator {

    Block generateBlockAt(Location location);

    boolean isActiveAtLocation(Location location);

    int getLevel();

    int maxLevel();

    void setLevel(int newLevel);

    default boolean isMaxed() {
        if (maxLevel() == -1) {
            return false;
        }
        return getLevel() < maxLevel();
    }

    default void incrementLevel() {
        if (!isMaxed()) {
            setLevel(getLevel() + 1);
        }
    }

    default void decrementLevel() {
        if (getLevel() > 0) {
            setLevel(getLevel() - 1);
        }
    }

    ItemStack toItemStack();

    void writeToMeta(ItemMeta target);
}
