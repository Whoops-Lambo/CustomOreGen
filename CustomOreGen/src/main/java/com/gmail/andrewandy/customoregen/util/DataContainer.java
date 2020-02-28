package com.gmail.andrewandy.customoregen.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataContainer implements ConfigurationSerializable {

    public static final Type TYPE = new TypeToken<DataContainer>() {
    }.getType();

    private static final transient Collection<Class<?>> wrappers = Arrays.asList(
            Integer.class, Boolean.class, Byte.class, Short.class, Double.class, Long.class,
            Float.class, Character.class
    );
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

    private static boolean isPrimitiveWrapper(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        clazz = clazz.isArray() ? clazz.getComponentType() : clazz;
        return wrappers.contains(clazz);
    }

    public static Class<?> getPrimitiveClassFor(Class<?> wrapperClass) {
        if (wrapperClass == null) {
            throw new IllegalArgumentException();
        }
        if (wrapperClass.isPrimitive()) {
            return wrapperClass;
        }
        if (!isPrimitiveWrapper(wrapperClass)) {
            throw new IllegalArgumentException();
        }
        if (wrapperClass == Integer.class) {
            return Integer.TYPE;
        } else if (wrapperClass == Boolean.class) {
            return Boolean.TYPE;
        } else if (wrapperClass == Short.class) {
            return Short.TYPE;
        } else if (wrapperClass == Double.class) {
            return Double.TYPE;
        } else if (wrapperClass == Long.class) {
            return Long.TYPE;
        } else if (wrapperClass == Float.class) {
            return Float.TYPE;
        } else if (wrapperClass == Character.class) {
            return Character.TYPE;
        } else if (wrapperClass == Byte.class) {
            return Byte.TYPE;
        } else {
            throw new IllegalStateException("Unknown wrapper class!");
        }
    }

    public static boolean isSupported(Class<?> clazz) {
        if (clazz == null || clazz == Void.class) {
            return false;
        }
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return clazz.isPrimitive() || clazz.equals(String.class) || isPrimitiveWrapper(clazz);
    }

    public static Optional<DataContainer> fromJson(String json) {
        return Optional.of(new GsonBuilder().create().fromJson(json, TYPE));
    }

    public void set(String key, Object object) throws UnsupportedOperationException {
        if (object == null) {
            container.remove(key);
            return;
        }
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

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> targetType) {
        if (!isSupported(targetType) || !containsKey(Objects.requireNonNull(key))) {
            return Optional.empty();
        }
        Object obj = container.get(key);
        assert obj != null;
        Class<?> objectClass = obj.getClass();
        //Check if object class is something like Integer.class and target class is int.class
        if (targetType.isPrimitive() && isPrimitiveWrapper(objectClass)) {
            //If say, the target class is int.class and the object's
            //is Integer.class, then the primitive for Integer.class would be int.class
            Class<?> primitive = getPrimitiveClassFor(objectClass);
            //Thus, if the primitive of the object's class == target class then it
            //is safe to unbox the value.
            if (primitive == targetType) {
                return Optional.of((T) obj);
            }
        }
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

    public Optional<Character> getChar(String key) {
        return Optional.of((Character) container.get(key));
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

    public String toJson() {
        return new GsonBuilder().create().toJson(this, TYPE);
    }

    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(container);
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
        return container != null ? 29 * container.hashCode() : 0;
    }
}
