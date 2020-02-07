package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.customoregen.util.Stackable;
import org.bukkit.inventory.ItemStack;

public interface StackableGenerator extends BlockGenerator, Stackable<ItemStack> {

    boolean canStack(ItemStack spawner);
}
