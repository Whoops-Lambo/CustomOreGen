package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import org.bukkit.Location;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The default generator manager.
 */
public class GeneratorManager {

    public Collection<BlockGenerator> generators = ConcurrentHashMap.newKeySet();

    public Collection<BlockGenerator> getAllGenerators() {
        return new HashSet<>(generators);
    }

    public List<BlockGenerator> getGeneratorsAt(Location location) {
        List<BlockGenerator> generators = this.generators.stream().filter(blockGenerator -> blockGenerator.isActiveAtLocation(location)).collect(Collectors.toList());
        Priority[] priorities = Priority.values();
        for (int index = 0; index < priorities.length; ) {
            Priority priority = priorities[index++];
            generators.addAll(generators.stream().filter(blockGenerator -> blockGenerator.getPriority() == priority).collect(Collectors.toSet()));
        }
        return generators;
    }

    public void registerGenerator(BlockGenerator generator) {
        generators.add(generator);
    }

    public void unregisterGenerator(BlockGenerator generator) {
        generators.remove(generator);
    }

    /**
     * @param location
     * @return
     */
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
