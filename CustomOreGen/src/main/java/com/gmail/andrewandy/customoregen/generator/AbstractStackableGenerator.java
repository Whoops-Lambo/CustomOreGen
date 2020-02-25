package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gmail.andrewandy.customoregen.util.Stackable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Level;

public abstract class AbstractStackableGenerator extends AbstractGenerator implements StackableGenerator {

    private static Type itemStackTypeToken = new TypeToken<ItemStack>() {
    }.getType();
    private static Type stackedJsonTypeToken = new TypeToken<Stack<String>>() {
    }.getType();

    private Stack<Stackable.StackedObject<ItemStack>> stack = new Stack<>();
    private int maxSize = -1;

    protected AbstractStackableGenerator(int maxLevel, int level) {
        super(maxLevel, level);
    }

    public AbstractStackableGenerator(int maxLevel, int level, Priority priority) {
        super(maxLevel, level, priority);
    }

    public AbstractStackableGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    public AbstractStackableGenerator(ItemMeta itemMeta) {
        super(itemMeta);
        ItemWrapper wrapper = ItemWrapper.wrap(itemMeta);
        Gson gson = new GsonBuilder().create();
        Stack<String> strings = gson.fromJson(wrapper.getString("Stacked"), stackedJsonTypeToken);
        for (String str : strings) {
            ItemStack itemStack = gson.fromJson(str, itemStackTypeToken);
            stack.add(new Stackable.StackedObject<>(itemStack));
        }
        Integer rawMax = wrapper.getInt("MaxStackSize");
        if (rawMax == null) {
            throw new IllegalArgumentException("Invalid Meta! No MaxStackSize found!");
        }
        this.maxSize = rawMax;
        if (maxSize < -1) {
            maxSize = -1;
        }
        if (maxSize != -1 && stack.size() > maxSize) {
            throw new IllegalStateException("Serialised stack size was greater than the serialised max size!");
        }
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
                Common.log(Level.WARNING, "&e[Data] Invalid Stacked Generator detected. Skipping!");
                continue;
            }
            stack.add(i, new Stackable.StackedObject<>(section.getItemStack(String.valueOf(i))));
        }
        if (maxSize != -1 && stack.size() > maxSize) {
            throw new IllegalStateException("Serialised stack size was greater than the serialised max size!");
        }
    }

    public Collection<Stackable.StackedObject<ItemStack>> getStacked() {
        return new LinkedList<>(stack);
    }

    @Override
    public int size() {
        return stack.size();
    }

    @Override
    public boolean canStack(Stackable.StackedObject<ItemStack> stackedObject) {
        return canStack(stackedObject.getOriginal());
    }

    @Override
    public abstract boolean canStack(ItemStack itemStack);

    @Override
    public void stack(Stackable.StackedObject<ItemStack> stackedObject) {
        if (!canStack(stackedObject)) {
            throw new IllegalArgumentException("Object cannot be stacked!");
        }

    }

    protected void saveStacked() {
        ConfigurationSection section = getDataSection();
        ConfigurationSection stackedSection = section.createSection("Stacked");
        int index = 0;
        for (Stackable.StackedObject<ItemStack> itemStack : stack) {
            stackedSection.set(String.valueOf(index++), itemStack.getOriginal().serialize());
        }
        stackedSection.set("IndexSize", index);
    }

    public void save() {
        super.save();
        ConfigurationSection section = getDataSection();
        section.set("MaxStackSize", maxSize);
        saveStacked();
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        Stack<String> jsonStack = new Stack<>();
        Gson gson = new GsonBuilder().create();
        for (Stackable.StackedObject<ItemStack> item : stack) {
            jsonStack.add(gson.toJson(item.getOriginal(), itemStackTypeToken));
        }
        wrapper.setString("Stacked", gson.toJson(jsonStack, new TypeToken<Stack<String>>() {
        }.getType()));
    }

    public void setMaxSize(int maxSize) {
        if (maxSize < 0) {
            this.maxSize = -1;
        } else {
            this.maxSize = maxSize;
        }
        stack.setSize(maxSize);
        stack.trimToSize();
    }

    public int maxSize() {
        return maxSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        AbstractStackableGenerator generator = (AbstractStackableGenerator) o;

        if (maxSize != generator.maxSize) return false;
        return Objects.equals(stack, generator.stack);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (stack != null ? stack.hashCode() : 0);
        result = 31 * result + maxSize;
        return result;
    }
}
