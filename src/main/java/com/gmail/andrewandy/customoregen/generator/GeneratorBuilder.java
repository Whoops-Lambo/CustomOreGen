package com.gmail.andrewandy.customoregen.generator;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;
import java.util.function.Function;

public class GeneratorBuilder {

    private Function<Location, Block> blockFunction;
    private Function<Location, Boolean> activeChecker;
    private int level;
    private int maxLevel;

    public GeneratorBuilder() {

    }

    public GeneratorBuilder setGeneratorFunction(Function<Location, Block> function) {
        this.blockFunction = function;
        return this;
    }

    public GeneratorBuilder setActiveLocationChecker(Function<Location, Boolean> function) {
        this.activeChecker = function;
        return this;
    }

    public GeneratorBuilder setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level must be greater than 0!");
        }
        this.level = level;
        return this;
    }

    public GeneratorBuilder setMaxLevel(int maxLevel) {
        if (maxLevel < 1) {
            throw new IllegalArgumentException("Level must be greater than 0!");
        }
        this.maxLevel = maxLevel;
        return this;
    }

    public GeneratorBuilder clear() {
        this.blockFunction = null;
        this.maxLevel = 1;
        this.level = 1;
        return this;
    }

    public BlockGenerator build() {
        BlockGenerator generator = new GeneratorImpl(this.blockFunction, this.activeChecker, maxLevel);
        generator.setLevel(Math.min(level, maxLevel));
        return generator;
    }

    public BlockGenerator buildAndClear() {
        BlockGenerator generator = build();
        clear();
        return generator;
    }


    private static class GeneratorImpl implements BlockGenerator {

        private final Function<Location, Block> blockFunction;
        private final Function<Location, Boolean> locationCheckFunction;
        private final int maxLevel;
        private int level = 1;

        private GeneratorImpl(Function<Location, Block> blockFunction, Function<Location, Boolean> locationChecker, int maxLevel) {
            this.blockFunction = Objects.requireNonNull(blockFunction);
            this.locationCheckFunction = Objects.requireNonNull(locationChecker);
            this.maxLevel = maxLevel;
        }

        @Override
        public Block generateBlockAt(Location location) {
            if (isActiveAtLocation(location)) {
                return blockFunction.apply(location);
            }
            return null;
        }

        @Override
        public boolean isActiveAtLocation(Location location) {
            return locationCheckFunction.apply(location);
        }

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public int maxLevel() {
            return maxLevel;
        }

        @Override
        public void setLevel(int newLevel) {
            if (newLevel > maxLevel) {
                throw new IllegalArgumentException("New level cannot be greater than max level!");
            }
            if (newLevel < 1) {
                throw new IllegalArgumentException("New level must be greater than 0.");
            }
            this.level = newLevel;
        }

        @Override
        public ItemStack toItemStack() {
            return null;
        }

        @Override
        public void writeToMeta(ItemMeta target) {
        }
    }

}
