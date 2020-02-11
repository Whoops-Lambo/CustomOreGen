package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.corelib.util.Common;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Level;

public abstract class AbstractStackableGenerator extends AbstractGenerator implements StackableGenerator {


    private Stack<StackedObject<ItemStack>> stack = new Stack<>();
    private int maxSize = -1;


    protected AbstractStackableGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }
    public AbstractStackableGenerator(UUID fromID) {
        super(fromID);
        ConfigurationSection section = getDataSection();
        this.maxSize = section.getInt("MaxStackSize");
        if (maxSize < -1) {
            maxSize = -1;
        }
        int indexSize = section.getInt("IndexSize");
        for (int i = 0; i < indexSize; i++) {
            if (!section.isItemStack(String.valueOf(i))) {
                Common.log(Level.WARNING, "&e[Data] Invalid StackedSpawner detected. Skipping!");
                continue;
            }
            stack.add(i, new StackedObject<>(section.getItemStack(String.valueOf(i))));
        }
    }

    public Collection<StackedObject<ItemStack>> getStacked() {
        return new LinkedList<>(stack);
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public boolean canStack(StackedObject<ItemStack> stackedObject) {
        return canStack(stackedObject.getOriginal());
    }

    @Override
    public boolean canStack(ItemStack spawner) {
        return false;
    }

    protected void saveStacked() {
        ConfigurationSection section = getDataSection();
        ConfigurationSection stackedSection = section.createSection("Stacked");
        int index = 0;
        for (StackedObject<ItemStack> itemStack : stack) {
            stackedSection.set(String.valueOf(index++), itemStack.getOriginal().serialize());
        }
        stackedSection.set("IndexSize", index);
    }

    public void save() {
        super.save();
        getDataSection().set("MaxStackSize", maxSize);
        saveStacked();
    }

    public void setMaxSize(int maxSize, boolean trim) {
        if (maxSize < 0) {
            this.maxSize = -1;
        } else {
            this.maxSize = maxSize;
        }
        if (maxSize < size() && trim) {
            stack.setSize(maxSize);
            stack.trimToSize();
        }
    }

    public int maxSize() {
        return maxSize;
    }
}
