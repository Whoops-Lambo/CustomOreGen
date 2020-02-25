package com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.levels;

import com.gmail.andrewandy.corelib.util.DeregisterableListener;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.hooks.bentobox.CustomOreGenAddon;
import com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.generators.IslandOreGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.level.Level;
import world.bentobox.level.event.IslandPreLevelEvent;

import java.util.*;

public class IslandLevelOreGenerator extends IslandOreGenerator {

    public static DeregisterableListener listener = new DeregisterableListener() {
        @EventHandler
        public void onIslandCommandExecute(IslandPreLevelEvent event) {
            Collection<BlockGenerator> generators = CustomOreGen.getGeneratorManager().getGeneratorsAt(event.getLocation());
            Collection<IslandLevelOreGenerator> oreGenerators = new HashSet<>();
            generators.forEach(gen -> {
                if (gen instanceof IslandLevelOreGenerator) {
                    oreGenerators.add((IslandLevelOreGenerator) gen);
                }
            });
            oreGenerators.forEach(gen -> {
                if (!event.getIsland().getUniqueId().contentEquals(gen.getIslandID())) {
                    return;
                }
                int level = gen.getGeneratorLevelFromIslandLevel();
                gen.setLevel(level);
            });
        }

        @Override
        public void disable() {
            IslandPreLevelEvent.getHandlerList().unregister(this);
        }
    };
    private Map<Integer, long[]> levelMap = new HashMap<>();

    public IslandLevelOreGenerator(UUID generatorID) {
        super(generatorID);
        validateHook();
    }

    public IslandLevelOreGenerator(ItemStack itemStack) {
        super(itemStack);
        validateHook();
    }

    public IslandLevelOreGenerator(ItemMeta meta) {
        super(meta);
        validateHook();
    }

    public IslandLevelOreGenerator(Island island, int maxLevel, int level) {
        super(island, maxLevel, level);
        validateHook();
    }

    public IslandLevelOreGenerator(Island island, int maxLevel, int level, Priority priority) {
        super(island, maxLevel, level, priority);
        validateHook();
    }

    public IslandLevelOreGenerator(String islandID, int maxLevel, int level) {
        super(islandID, maxLevel, level);
        validateHook();
    }

    public IslandLevelOreGenerator(String islandID, int maxLevel, int level, Priority priority) {
        super(islandID, maxLevel, level, priority);
        validateHook();
    }

    public static void validateHook() throws IllegalArgumentException {
        if (IslandLevelsHook.getInstance() == null) {
            throw new IllegalArgumentException("Island Levels not found!");
        }
    }

    public static void registerListener() {
        BentoBox.getInstance().getAddonsManager().registerListener(CustomOreGenAddon.getInstance(), listener);
    }

    public static void unregisterListener() {
        listener.disable();
    }

    public void setLevelBounds(long lower, long upper, int level) {
        if (boundsAreDuplicated(lower, upper)) {
            throw new IllegalArgumentException("Bounds already exist!");
        }
        if (level < 1 || level > maxLevel()) {
            throw new IllegalArgumentException("Invalid level!");
        }
        levelMap.remove(level);
        levelMap.put(level, new long[]{Math.min(lower, upper), Math.max(lower, upper)});
    }

    public long[] getLevelBounds(int level) {
        if (level < 1 || level > maxLevel()) {
            throw new IllegalArgumentException("Invalid level!");
        }
        return levelMap.get(level).clone();
    }

    public long getLowerBound(int level) {
        return getLevelBounds(level)[0];
    }

    public long getUpperBound(int level) {
        return getLevelBounds(level)[1];
    }


    public int getGeneratorLevelFromIslandLevel() {
        Level level = (Level) IslandLevelsHook.getInstance().getAddons()[0];
        Optional<Island> optional = BentoBox.getInstance().getIslands().getIslandById(getIslandID());
        if (optional.isPresent()) {
            long islandLevel = level.getIslandLevel(optional.get().getWorld(), null);
            for (Map.Entry<Integer, long[]> entry : levelMap.entrySet()) {
                long[] arr = entry.getValue();
                if (islandLevel >= arr[0] && islandLevel <= arr[1]) {
                    return entry.getKey();
                }
            }
        }
        //Return the current level if no island was found or no bounds are valid.
        return getLevel();
    }

    public boolean boundsAreDuplicated(long lower, long upper) {
        if (lower > upper) {
            long temp = lower;
            lower = upper;
            upper = temp;
        }
        if (lower == upper || lower < 0) {
            throw new IllegalArgumentException("Invalid Bounds!");
        }
        for (Map.Entry<Integer, long[]> entry : levelMap.entrySet()) {
            long[] arr = entry.getValue();
            long currentLower = arr[0];
            if (lower >= currentLower && lower <= upper) {
                return true;
            }
        }
        return false;
    }
}
