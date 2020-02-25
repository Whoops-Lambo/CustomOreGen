package com.gmail.andrewandy.customoregen.util;

import com.gmail.andrewandy.corelib.util.Common;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import com.gmail.andrewandy.customoregen.generator.Priority;
import com.gmail.andrewandy.customoregen.generator.SingleInstanceGenerator;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The default generator manager.
 */
public class GeneratorManager {

    private Collection<BlockGenerator> generators = ConcurrentHashMap.newKeySet();

    public Collection<BlockGenerator> getAllGenerators() {
        return new HashSet<>(generators);
    }

    public List<BlockGenerator> getGeneratorsAt(Location location) {
        List<BlockGenerator> generators = this.generators.stream()
                .filter(blockGenerator -> blockGenerator.isActiveAtLocation(location)).collect(Collectors.toList());
        List<BlockGenerator> ret = new ArrayList<>(generators.size());
        Priority[] priorities = Priority.values();
        for (int index = 0; index < priorities.length; ) {
            Priority priority = priorities[index++];
            ret.addAll(generators.stream()
                    .filter(blockGenerator -> blockGenerator.getPriority() == priority).collect(Collectors.toSet()));
        }
        return ret;
    }

    public void registerGenerator(BlockGenerator generator) {
        if (generator instanceof SingleInstanceGenerator) {
            throw new UnsupportedOperationException();
        }
        generators.remove(generator);
        generators.add(generator);
    }

    public void registerUniversalGenerator(SingleInstanceGenerator generator, boolean overwrite) {
        if (overwrite) {
            generators.removeIf(gen -> Common.classEquals(gen.getClass(), generator.getClass()));
        } else if (generators.stream().anyMatch(gen -> Common.classEquals(gen.getClass(), generator.getClass()))) {
            return;
        }
        generators.add(generator);
    }

    public void unregisterUniversalGenerator(Class<? extends SingleInstanceGenerator> clazz) {
        generators.removeIf(gen -> Common.classEquals(gen.getClass(), clazz));
    }


    public void unregisterGenerator(BlockGenerator generator) {
        generators.remove(generator);
    }

    /**
     * @param location
     * @return
     */
    public Collection<BlockGenerator> unregisterAllActiveAt(Location location) {
        Collection<BlockGenerator> toRemove = generators.stream()
                .filter(generator -> generator.isActiveAtLocation(location)).collect(Collectors.toList());
        generators.removeAll(toRemove);
        return toRemove;
    }

    public Collection<BlockGenerator> retainAllActiveAt(Location location) {
        Collection<BlockGenerator> toRemove = generators.stream()
                .filter(generator -> !generator.isActiveAtLocation(location)).collect(Collectors.toList());
        generators.removeAll(toRemove);
        return toRemove;
    }

}
