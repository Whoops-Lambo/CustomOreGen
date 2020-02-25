package com.gmail.andrewandy.customoregen.hooks.bentobox.skyblock.levels;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

public class IslandTemplateMapper implements ConfigurationSerializable, Cloneable {

    private static final String SERIAL_KEY = "TemplateMap";
    private static final String IDENTIFIER_KEY = "ISLAND_TEMPLATE_MAPPER_ID";
    private static final IslandTemplateMapper instance = new IslandTemplateMapper();
    private Map<String, String> islandBlueprintUIDMap = new ConcurrentHashMap<>();

    private IslandTemplateMapper() {
    }

    public static IslandTemplateMapper getInstance() {
        return instance;
    }

    public static IslandTemplateMapper deserialise(Map<String, Object> serial) {
        Objects.requireNonNull(serial);
        if (!serial.containsKey(IDENTIFIER_KEY)) {
            throw new IllegalArgumentException("Invalid serial object, identifier key was not found!");
        }
        serial.remove(IDENTIFIER_KEY);
        Map<String, String> blueprintMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : serial.entrySet()) {
            String key = entry.getKey();
            Object rawValue = entry.getValue();
            if (!(rawValue instanceof String)) {
                throw new IllegalArgumentException("Invalid serial object! Does not contain all strings!");
            }
            blueprintMap.put(key, (String) rawValue);
        }
        IslandTemplateMapper mapper = new IslandTemplateMapper();
        mapper.islandBlueprintUIDMap.putAll(blueprintMap);
        return mapper;
    }

    public void registerIslandTemplate(String islandID, String blueprintID) {
        islandBlueprintUIDMap.remove(Objects.requireNonNull(islandID));
        islandBlueprintUIDMap.put(islandID, Objects.requireNonNull(blueprintID));
    }

    public void unregisterIslandTemplate(String islandID) {
        islandBlueprintUIDMap.remove(Objects.requireNonNull(islandID));
    }

    public Optional<String> getBlueprintID(String islandID) {
        if (!islandBlueprintUIDMap.containsKey(Objects.requireNonNull(islandID))) {
            return Optional.empty();
        }
        return Optional.of(islandBlueprintUIDMap.get(islandID));
    }

    public void load(File file) {
        YamlConfiguration data = YamlConfiguration.loadConfiguration(file);
        IslandTemplateMapper reconstructed = data.getSerializable(SERIAL_KEY, IslandTemplateMapper.class);
        if (reconstructed == null) {
            islandBlueprintUIDMap.clear();
        } else {
            this.islandBlueprintUIDMap = reconstructed.islandBlueprintUIDMap;
        }
    }


    public void save(File file) throws IOException {
        YamlConfiguration data = new YamlConfiguration();
        data.set(SERIAL_KEY, instance);
        data.save(file);
    }

    public Future<?> saveAsync(File file, ExecutorService executorService, Consumer<IOException> callback) {
        return Objects.requireNonNull(executorService).submit(() -> {
            try {
                save(file);
            } catch (IOException ex) {
                if (callback != null) {
                    callback.accept(ex);
                }
            }
        });
    }

    public BukkitTask save(File file, Consumer<IOException> callback) {
        return Bukkit.getScheduler().runTask(CustomOreGen.getInstance(), () -> {
            try {
                save(file);
            } catch (IOException ex) {
                if (callback != null) {
                    callback.accept(ex);
                }
            }
        });
    }

    public BukkitTask saveAsync(File file, BukkitScheduler scheduler, JavaPlugin plugin, Consumer<IOException> callback) {
        return Objects.requireNonNull(scheduler.runTaskAsynchronously(plugin, () -> {
            try {
                save(file);
            } catch (IOException ex) {
                if (callback != null) {
                    callback.accept(ex);
                }
            }
        }));
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>(islandBlueprintUIDMap);
        map.put(IDENTIFIER_KEY, "null");
        return map;
    }

    public boolean islandIsRegistered(String islandID) {
        return islandBlueprintUIDMap.containsKey(islandID);
    }

    public Collection<String> getAllRegisteredIslands() {
        return new ArrayList<>(islandBlueprintUIDMap.keySet());
    }

    public Collection<String> getUniqueMappedBlueprints() {
        Collection<String> blueprints = new HashSet<>();
        islandBlueprintUIDMap.forEach((String islandID, String blueprintID) -> {
            if (!blueprints.contains(blueprintID)) {
                blueprints.add(blueprintID);
            }
        });
        return blueprints;
    }

    @Override
    public IslandTemplateMapper clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        IslandTemplateMapper mapper = new IslandTemplateMapper();
        mapper.islandBlueprintUIDMap.clear();
        ;
        mapper.islandBlueprintUIDMap.putAll(islandBlueprintUIDMap);
        return mapper;
    }
}
