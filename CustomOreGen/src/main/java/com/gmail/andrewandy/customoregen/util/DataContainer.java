package com.gmail.andrewandy.customoregen.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataContainer implements ConfigurationSerializable {

    private static final Collection<Class<?>> supportedClasses;

    static {
        supportedClasses = Arrays.asList(
                int.class,
                double.class,
                float.class,
                long.class,
                short.class,
                byte.class,
                String.class);
    }

    private Map<String, Object> container = new ConcurrentHashMap<>();

    public DataContainer(Map<String, Object> serial) {
        this.container = new HashMap<>(serial);
    }

    public DataContainer() {

    }

    public DataContainer(DataContainer other) {
        Objects.requireNonNull(other);
        this.container = new ConcurrentHashMap<>(other.container);
    }


    public static boolean isSupported(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        for (Class<?> supported : supportedClasses) {
            if (clazz.equals(supported) || clazz.getCanonicalName().equalsIgnoreCase(supported.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    public void set(String key, Object object) throws UnsupportedOperationException {
        if (!isSupported(object.getClass())) {
            throw new UnsupportedOperationException();
        }
        container.remove(key);
        container.put(key, object);
    }

    public boolean containsKey(String key) {
        return container.containsKey(key);
    }

    public <T> boolean containsKeyWithType(String key, Class<T> targetType) {
        return get(key, targetType).isPresent();
    }

    public <T> Optional<T> get(String key, Class<T> targetType) {
        Object obj = container.get(Objects.requireNonNull(key));
        if (!targetType.isInstance(obj)) {
            return Optional.empty();
        }
        return Optional.of(targetType.cast(obj));
    }

    public Optional<Double> getDouble(String key) {
        return get(key, double.class);
    }

    public Optional<double[]> getDoubleArray(String key) {
        return get(key, double[].class);
    }

    public Optional<Integer> getInt(String key) {
        return get(key, int.class);
    }


    public Optional<int[]> getIntArray(String key) {
        return get(key, int[].class);
    }

    public Optional<Float> getFloat(String key) {
        return get(key, float.class);
    }

    public Optional<float[]> getFloatArray(String key) {
        return get(key, float[].class);
    }

    public Optional<Long> getLong(String key) {
        return get(key, long.class);
    }

    public Optional<Byte> getByte(String key) {
        return get(key, byte.class);
    }

    public Optional<byte[]> getByteArray(String key) {
        return get(key, byte[].class);
    }

    public Optional<Short> getShort(String key) {
        return get(key, short.class);
    }

    public Optional<short[]> getShortArray(String key) {
        return get(key, short[].class);
    }

    public Optional<long[]> getLongArray(String key) {
        return get(key, long[].class);
    }

    public Optional<String> getString(String key) {
        return get(key, String.class);
    }

    public Optional<String[]> getStringArray(String key) {
        return get(key, String[].class);
    }

    public String[] getStringArray(String key, boolean nullable) {
        if (container.containsKey(key) && nullable) {
            return (String[]) container.get(key);
        }
        return new String[0];
    }

    public String getString(String key, boolean nullable) {
        if (container.containsKey(key) && nullable) {
            return (String) container.get(key);
        }
        return null;
    }

    public Collection<Object> getValues() {
        return new ArrayList<>(container.values());
    }

    public <T> Collection<T> getAllValuesOfType(Class<T> targetType) {
        Collection<Object> raw = getValues();
        Objects.requireNonNull(targetType);
        Collection<T> ret = new HashSet<>();
        raw.forEach(obj -> {
            if (targetType.isInstance(obj)) {
                ret.add(targetType.cast(obj));
            }
        });
        return ret;
    }


    @Override
    public Map<String, Object> serialize() {
        return container;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataContainer container1 = (DataContainer) o;

        return Objects.equals(container, container1.container);
    }

    @Override
    public int hashCode() {
        return container != null ? container.hashCode() : 0;
    }
}
