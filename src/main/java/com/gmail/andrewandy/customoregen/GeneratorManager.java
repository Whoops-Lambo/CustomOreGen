package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import org.bukkit.Location;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GeneratorManager {

    public Collection<BlockGenerator> generators = new ConcurrentHashMap<BlockGenerator, Object>().keySet();

    public Collection<BlockGenerator> getGeneratorsAt(Location location) {
        return generators.stream().filter(generator -> generator.isActiveAtLocation(location)).collect(Collectors.toSet());
    }

    public void registerGenerator(BlockGenerator generator) {
        generators.add(generator);
    }

    public void unregisterGenerator(BlockGenerator generator) {
        generators.remove(generator);
    }

    public Collection<BlockGenerator> unregisterAllActiveAt(Location location) {
        Collection<BlockGenerator> toRemove = generators.stream().filter(generator -> generator.isActiveAtLocation(location)).collect(Collectors.toList());
        generators.removeAll(toRemove);
        return toRemove;
    }

    public Collection<BlockGenerator> retainAllActiveAt(Location location) {
        Collection<BlockGenerator> toRemove = generators.stream().filter(generator -> !generator.isActiveAtLocation(location)).collect(Collectors.toList());
        generators.removeAll(toRemove);
        return toRemove;
    }

}
