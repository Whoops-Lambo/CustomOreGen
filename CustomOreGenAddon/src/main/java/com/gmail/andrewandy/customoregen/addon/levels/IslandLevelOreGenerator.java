package com.gmail.andrewandy.customoregen.addon.levels;

import com.gmail.andrewandy.corelib.util.DeregisterableListener;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.level.Level;
import world.bentobox.level.event.IslandPreLevelEvent;

import java.util.*;

public class IslandLevelOreGenerator extends IslandOreGenerator {

    //Handles the level up generator level logic.
    public static DeregisterableListener listener = new DeregisterableListener() {
        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        public void onIslandCommandExecute(IslandPreLevelEvent event) {
            Collection<BlockGenerator> generators = CustomOreGen.getGeneratorManager().getGeneratorsAt(event.getLocation());

            generators.forEach(gen -> {
                if (gen instanceof IslandLevelOreGenerator) {
                    IslandLevelOreGenerator oreGenerator = (IslandLevelOreGenerator) gen;
                    if (!event.getIsland().getUniqueId().contentEquals(oreGenerator.getIslandID())) {
                        return;
                    }
                    int level = oreGenerator.getGeneratorLevelFromIslandLevel();
                    gen.setLevel(level);
                }
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
        Bukkit.getPluginManager().registerEvents(listener, CustomOreGen.getInstance());
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
        Optional<Island> optional = BentoBox.getInstance().getIslands().getIslandById(getIslandID());
        if (optional.isPresent()) {
            long islandLevel = IslandLevelsHook.getInstance().getIslandLevel(optional.get().getWorld(), optional.get().getOwner().toString());
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
