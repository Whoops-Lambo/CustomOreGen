package com.gmail.andrewandy.customoregen.addon.util;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.generator.AbstractGenerator;
import com.gmail.andrewandy.customoregen.util.DataContainer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;

public class IslandTracker implements ConfigurationSerializable, Cloneable {

    private static final String IDENTIFIER_KEY = "ISLAND_TRACKER_IDENTIFY";

    /*
    static {
        if (CustomOreGenAddon.getInstance() == null) {
            Common.log(Level.WARNING, "&eLoaded IslandTracker without Skyblock enabled!");
        }
    }

     */

    private final String islandID;
    private DataContainer dataContainer;
    private IslandOreGenerator generator;

    public IslandTracker(String islandID) {
        this(islandID, new DataContainer());
    }

    public IslandTracker(String islandID, DataContainer data) {
        this.islandID = Objects.requireNonNull(islandID);
        this.dataContainer = new DataContainer(data);
        String className = IslandTracker.class.getName();
        this.dataContainer.set(IDENTIFIER_KEY, className);
        this.dataContainer.set("Island", islandID);
    }

    /**
     * Reconstruct a tracker instance from its data container, see {@link #getDataContainer()}
     *
     * @param dataContainer The data container to reconstruct from.
     * @return Returns a new instance of this class, from the data serialised.
     * @see #deserialise(Map)
     */
    public static IslandTracker fromDataContainer(DataContainer dataContainer) {
        if (!Objects.requireNonNull(dataContainer).containsKey(IDENTIFIER_KEY)) {
            throw new IllegalArgumentException("Identifier key missing!");
        }
        String island = dataContainer.getString("Island", true);
        if (island == null) {
            throw new IllegalArgumentException("Null island!");
        }
        return new IslandTracker(island, dataContainer);
    }

    /**
     * Method as per {@link ConfigurationSerializable} for deserialisation.
     *
     * @return Returns a new instance of this class, from the data serialised.
     * @see #fromDataContainer(DataContainer)
     */
    public static IslandTracker deserialise(Map<String, Object> serial) {
        Object rawIsland = Objects.requireNonNull(serial).get("Island");
        if (!(rawIsland instanceof String)) {
            throw new IllegalArgumentException("Invalid serial");
        }
        DataContainer container = new DataContainer(serial);
        //Check the identifier
        Optional<String> identifierValue = container.getString(IDENTIFIER_KEY);
        if (!identifierValue.isPresent() || !identifierValue.get().equalsIgnoreCase(IslandTracker.class.getName())) {
            throw new IllegalArgumentException("Invalid data container! Missing or invalid identifier key!");
        }
        IslandTracker tracker = new IslandTracker((String) rawIsland);
        tracker.dataContainer = new DataContainer(container);
        IslandOreGenerator oreGenerator;
        Optional<String> rawGenerator = tracker.getDataContainer().getString("Generator");
        if (rawGenerator.isPresent()) {
            UUID generatorID = UUID.fromString(rawGenerator.get());
            Optional<AbstractGenerator> optional = AbstractGenerator.fromID(generatorID);
            AbstractGenerator unknown = optional.orElse(null);
            if (unknown instanceof IslandOreGenerator) {
                oreGenerator = (IslandOreGenerator) unknown;
            } else {
                Common.log(Level.WARNING, "&e[Serial Debug] Generator ID parsed was not of type IslandOreGenerator!");
                oreGenerator = null;
            }
        } else {
            oreGenerator = null;
        }
        tracker.generator = oreGenerator;
        return tracker;
    }

    @Override
    public Map<String, Object> serialize() {
        return dataContainer.serialize();
    }

    public String getIslandID() {
        return islandID;
    }

    public IslandOreGenerator getGenerator() {
        return generator;
    }

    public IslandTracker setGenerator(IslandOreGenerator generator) {
        this.generator = generator;
        return updateGeneratorData();
    }

    private IslandTracker updateGeneratorData() {
        if (generator != null) {
            generator.save();
            dataContainer.set("Generator", generator.getGeneratorID().toString());
        } else {
            dataContainer.set("Generator", (UUID) null);
        }
        return this;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IslandTracker that = (IslandTracker) o;
        return Objects.equals(islandID, that.islandID) &&
                Objects.equals(dataContainer, that.dataContainer) &&
                Objects.equals(generator, that.generator);
    }

    @Override
    public int hashCode() {
        return Objects.hash(islandID, dataContainer, generator);
    }
}
