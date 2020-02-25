package com.gmail.andrewandy.customoregen.generator;

import org.bukkit.Location;

/**
 * Represents a generator which can be placed down.
 */
public interface PlaceableGenerator {

    void placeAt(Location location);

}
