package com.gmail.andrewandy.customoregen.addon.generators;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import com.gmail.andrewandy.customoregen.hooks.economy.VaultHook;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import world.bentobox.bentobox.database.objects.Island;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.gmail.andrewandy.customoregen.generator.ChanceGenerator.GEN_CHANCE_HELPER_KEY;

/**
 * Represents an ore generator which only works on a specific island.
 */
public class IslandOreGenerator extends IslandRegionGenerator {

    private final double[] levelUpCost;
    private List<GenerationChanceHelper> spawnChances;
    private int manualMaxLevel;

    public IslandOreGenerator(UUID generatorID) {
        super(generatorID);
        validateHook();
        manualMaxLevel = super.maxLevel();
        spawnChances = new ArrayList<>(getMaxLevel() + 1);
        spawnChances.add(0, null);
        for (int level = 1; level < maxLevel(); level++) {
            String jsonMapped = getDataSection().getString(GEN_CHANCE_HELPER_KEY + ":" + level);
            setSpawnChances(jsonMapped, level);
        }
        levelUpCost = new double[maxLevel() + 1];
        manualMaxLevel = super.maxLevel();
    }

    public IslandOreGenerator(ItemStack itemStack) {
        this(Objects.requireNonNull(itemStack).getItemMeta());
    }

    public IslandOreGenerator(ItemMeta meta) {
        super(meta);
        validateHook();
        manualMaxLevel = super.maxLevel();
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        for (int level = 0; level < maxLevel(); level++) {
            String jsonMapped = wrapper.getString(GEN_CHANCE_HELPER_KEY + ":" + level);
            setSpawnChances(jsonMapped, level);
        }
        levelUpCost = new double[maxLevel() + 1];
        fillSpawnChances(false);
    }

    public IslandOreGenerator(Island island, int maxLevel, int level) {
        super(island, maxLevel, level);
        validateHook();
        manualMaxLevel = super.maxLevel();
        levelUpCost = new double[maxLevel() + 1];
        fillSpawnChances(false);
    }

    public IslandOreGenerator(Island island, int maxLevel, int level, Priority priority) {
        super(island, maxLevel, level, priority);
        validateHook();
        manualMaxLevel = super.maxLevel();
        levelUpCost = new double[maxLevel() + 1];
        fillSpawnChances(false);

    }

    public IslandOreGenerator(String islandID, int maxLevel, int level) {
        super(islandID, maxLevel, level);
        validateHook();
        manualMaxLevel = super.maxLevel();
        levelUpCost = new double[maxLevel() + 1];
        fillSpawnChances(false);
    }

    public IslandOreGenerator(String islandID, int maxLevel, int level, Priority priority) {
        super(islandID, maxLevel, level, priority);
        validateHook();
        manualMaxLevel = super.maxLevel();
        levelUpCost = new double[maxLevel() + 1];
        fillSpawnChances(false);
    }

    private void fillSpawnChances(boolean overwrite) {
        if (spawnChances == null) {
            spawnChances = new ArrayList<>(maxLevel() + 1);
            spawnChances.add(0, null);
        }
        for (int index = 1; index < maxLevel() + 1; index++) {
            if (spawnChances.size() == index) {
                spawnChances.add(index, new GenerationChanceHelper());
                continue;
            }
            if (spawnChances.get(index) != null) {
                if (!overwrite) {
                    continue;
                }
                spawnChances.add(index, new GenerationChanceHelper());
            } else {
                spawnChances.set(index, new GenerationChanceHelper());
            }
        }
    }

    public void setLevelUpCost(int level, double cost) {
        if (cost < 0) {
            throw new IllegalArgumentException("Cost must be greater than 1");
        }
        if (level < 1 || level > maxLevel()) {
            throw new IllegalArgumentException("Invalid level!");
        }
        this.levelUpCost[level] = cost;
    }

    public void upgrade(UUID upgrader, String succeedMessage, String failMessage) {
        boolean success = true;
        if (upgrader != null) {
            Economy economy = VaultHook.getEconomy();
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(upgrader);
            CommandSender player = offlinePlayer.getPlayer();
            double balance = economy.getBalance(offlinePlayer);
            success = balance > levelUpCost[getLevel()] && economy.withdrawPlayer(offlinePlayer, levelUpCost[getLevel()]).transactionSuccess();
            String message = success ? succeedMessage : failMessage;
            if (player != null && message != null) {
                Common.tell(player, message);
            }
        }
        if (success) {
            incrementLevel();
        }
    }

    private void setSpawnChances(String serial, int level) {
        if (level < 1 || level > maxLevel() + 1) {
            throw new IllegalArgumentException("Invalid level!");
        }
        if (spawnChances.size() - 1 < level) {
            spawnChances.add(level, new GenerationChanceHelper(serial));
        } else {
            spawnChances.set(level, new GenerationChanceHelper(serial));
        }
    }

    public List<GenerationChanceHelper> getAllSpawnChances() {
        return spawnChances;
    }

    public GenerationChanceHelper getSpawnChances(int level) {
        if (level > maxLevel() || level < 1) {
            throw new IllegalArgumentException("Invalid Level!");
        }
        if (spawnChances.size() < level) {
            spawnChances.add(level, new GenerationChanceHelper());
        }
        return spawnChances.get(level);
    }

    public GenerationChanceHelper getSpawnChances() {
        return getSpawnChances(getLevel());
    }

    @Override
    public BlockData generateBlockAt(Location location) {
        if (!isActiveAtLocation(location) || spawnChances == null) {
            return null;
        }
        return getSpawnChances().getRandomBlock();
    }

    @Override
    public boolean isActiveAtLocation(Location location) {
        return withinRegion(location);
    }

    /**
     * This generator cannot be picked up!
     */
    @Override
    public ItemStack toItemStack() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This generator cannot be picked up!");
    }

    @Override
    public void save() {
        super.save();
        ConfigurationSection section = getDataSection().createSection(GEN_CHANCE_HELPER_KEY);
        for (int level = 1; level < maxLevel(); level++) {
            section.set(Integer.toString(level), getSpawnChances(level).serialise());
        }
    }

    @Override
    public void writeToMeta(ItemMeta original) {
        //Mutates the original meta.
        super.writeToMeta(original);
        ItemWrapper wrapper = ItemWrapper.wrap(original);
        for (int level = 1; level < maxLevel(); level++) {
            wrapper.setString(GEN_CHANCE_HELPER_KEY + ":" + level, getSpawnChances(level).serialise());
        }
    }

    @Override
    public int maxLevel() {
        return manualMaxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        if (maxLevel < 1) {
            throw new IllegalArgumentException("Invalid max level!");
        }
        manualMaxLevel = maxLevel;
        trimToMaxLevel();
    }

    protected void trimToMaxLevel() {
        if (maxLevel() < getLevel()) {
            setLevel(maxLevel());
        }
    }

    @Override
    public boolean isGlobal() {
        return false;
    }
}
