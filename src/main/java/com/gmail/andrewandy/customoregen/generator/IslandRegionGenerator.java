package com.gmail.andrewandy.customoregen.generator;

import com.gmail.andrewandy.customoregen.Region;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.database.objects.Island;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public abstract class IslandRegionGenerator extends AbstractGenerator {

    private final String islandID;

    public IslandRegionGenerator(UUID generatorID) {
        super(generatorID);
        ConfigurationSection section = getDataSection();
        islandID = section.getString("IslandID");
    }

    public IslandRegionGenerator(Island island, int maxLevel, int level) {
        this(Objects.requireNonNull(island).getUniqueId(), maxLevel, level);
    }

    public IslandRegionGenerator(String islandID, int maxLevel, int level) {
        super(maxLevel, level);
        this.islandID = Objects.requireNonNull(islandID);
        if (!BentoBox.getInstance().getIslands().getIslandById(islandID).isPresent()) {
            throw new IllegalArgumentException("Invalid IslandID Specified!");
        }
    }

    public boolean withinRegion(Location location) {
        Optional<Island> optionalIsland = BentoBox.getInstance().getIslands().getIslandById(islandID);
        if (!optionalIsland.isPresent()) {
            throw new IllegalStateException("IslandID is invalid, no island was found!");
        }
        Island island = optionalIsland.get();
        return island.inIslandSpace(location);
    }

    public boolean allWithinRegion(Region region) {
        Optional<Island> optionalIsland = BentoBox.getInstance().getIslands().getIslandById(islandID);
        if (!optionalIsland.isPresent()) {
            throw new IllegalStateException("IslandID is invalid, no island was found!");
        }
        Island island = optionalIsland.get();
        return new Region(island.getBoundingBox(), island.getWorld()).contains(region);
    }

    @Override
    public void save() {
        super.save();
        getDataSection().set("IslandID", islandID);
    }
}
