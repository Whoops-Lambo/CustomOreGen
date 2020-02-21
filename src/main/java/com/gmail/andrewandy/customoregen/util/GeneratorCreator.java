package com.gmail.andrewandy.customoregen.util;

import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.generator.HoldableGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceWrapper;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

//TODO --> Incomplete idea & class
public class GeneratorCreator implements ConfigurationSerializable {

    private static final Map<UUID, GeneratorCreator> playerMap = new HashMap<>();

    //private Collection<GeneratorFlag> flags = new ArrayList<>();

    private int level, maxLevel;
    private boolean isGlobal;
    private boolean isStackable;
    private GenerationChanceWrapper chances;

    private GeneratorCreator() {
    }

    public static GeneratorCreator getCreator(UUID player) {
        return playerMap.computeIfAbsent(Objects.requireNonNull(player), uuid -> new GeneratorCreator());
    }

    public static GeneratorCreator nullCreator() {
        return new GeneratorCreator();
    }

    public static GeneratorCreator deserialise(Map<String, Object> serial) {
        int level, maxLevel;
        boolean global, stackable;
        GenerationChanceWrapper chanceWrapper;
        //Setup the variables
        level = (int) serial.get("level");
        maxLevel = (int) serial.get("maxLevel");
        global = (boolean) serial.get("global");
        stackable = (boolean) serial.get("stackable");
        chanceWrapper = new GenerationChanceWrapper((String) serial.get("generationChances"));
        //Set the state from a null creator.
        GeneratorCreator creator = nullCreator();
        creator.chances = chanceWrapper;
        creator.isGlobal = global;
        creator.isStackable = stackable;
        creator.level = level;
        creator.maxLevel = maxLevel;
        return creator;
    }

    public BlockGenerator build() throws IllegalStateException {
        if (!isStackable) {
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("level", level);
        map.put("maxLevel", maxLevel);
        map.put("generationChances", chances.serialise());
        map.put("global", isGlobal);
        map.put("stackable", isStackable);
        return map;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public boolean isStackable() {
        return isStackable;
    }

    private static class NonStackableImpl extends HoldableGenerator {


        public NonStackableImpl(int maxLevel, int level) {
            super(maxLevel, level);
        }

        public NonStackableImpl(int maxLevel, int level, Priority priority) {
            super(maxLevel, level, priority);
        }

        public NonStackableImpl(ItemStack itemStack) {
            super(itemStack);
        }

        public NonStackableImpl(ItemMeta meta) {
            super(meta);
        }

        public NonStackableImpl(UUID fromID) throws IllegalArgumentException {
            super(fromID);
        }

        @Override
        public BlockData generateBlockAt(Location location) {
            return null;
        }

        @Override
        public boolean isActiveAtLocation(Location location) {
            return false;
        }

        @Override
        public boolean isGlobal() {
            return false;
        }

        @Override
        public ItemStack toItemStack() {
            return null;
        }

        @Override
        public void placeAt(Location location) {

        }
    }

    /*
    public Collection<GeneratorFlag> getFlags() {
        return flags;
    }

    public GeneratorCreator addFlag(GeneratorFlag flag) {
        this.flags.remove(flag);
        this.flags.add(Objects.requireNonNull(flag));
        return this;
    }

    public GeneratorCreator removeFlag(GeneratorFlag flag) {
        this.flags.remove(flag);
        return this;
    }
    */


}
