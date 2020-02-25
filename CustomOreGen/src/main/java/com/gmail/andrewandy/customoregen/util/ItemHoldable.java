package com.gmail.andrewandy.customoregen.util;

import org.bukkit.inventory.ItemStack;

/**
 * Classes which extend this interface should have a constructor which takes in an {@link org.bukkit.inventory.ItemStack}.
 */
public interface ItemHoldable {

    ItemStack toItemStack();

}
