package com.gmail.andrewandy.customoregen.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ItemWrapper {

    private final PersistentDataContainer container;
    private volatile ItemMeta meta;
    public static JavaPlugin plugin;

    private NamespacedKey getKey(String name) {
        return new NamespacedKey(plugin, name);
    }

    public static void setPlugin(JavaPlugin instance) {
        plugin = Objects.requireNonNull(instance);
    }

    /**
     * @see #wrap(ItemMeta)
     */
    private ItemWrapper(ItemMeta meta) {
        this.meta = Objects.requireNonNull(meta);
        this.container = meta.getPersistentDataContainer();
    }

    public <T> boolean hasKeyOfType(String key, Class<T> clazz) {
        return clazz.isInstance(get(key, clazz));
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        if (!isPrimitive(clazz)) {
            throw new UnsupportedOperationException("Only primitives are supported!");
        }
        NamespacedKey namespacedKey = getKey(key);
        PersistentDataType<?, ?> type = getType(clazz);
        assert type != null;
        return (T) container.get(namespacedKey, type);
    }

    public static ItemWrapper wrap(ItemStack itemStack) {
        if (!Objects.requireNonNull(itemStack).hasItemMeta()) {
            throw new IllegalArgumentException("Stack doesn't have ItemMeta!");
        }
        return wrap(itemStack.getItemMeta());
    }

    public static ItemWrapper wrap(ItemMeta meta) {
        return new ItemWrapper(Objects.requireNonNull(meta).clone());
    }

    public ItemWrapper setString(String key, String value) {
        container.set(getKey(key), PersistentDataType.STRING, value);
        return this;
    }

    public String getString(String key) {
        return container.get(getKey(key), PersistentDataType.STRING);
    }

    public ItemWrapper setInt(String key, int... value) {
        if (value.length == 1) {
            container.set(getKey(key), PersistentDataType.INTEGER, value[0]);
        } else {
            container.set(getKey(key), PersistentDataType.INTEGER_ARRAY, value);
        }
        return this;
    }


    public int getInt(String key) {
        Integer value = container.get(getKey(key), PersistentDataType.INTEGER);
        return value == null ? 0 : value;
    }

    public int[] getIntArray(String key) {
        return container.get(getKey(key), PersistentDataType.INTEGER_ARRAY);
    }

    public ItemWrapper setByte(String key, byte... value) {
        if (value.length == 1) {
            container.set(getKey(key), PersistentDataType.BYTE, value[0]);
        } else {
            container.set(getKey(key), PersistentDataType.BYTE_ARRAY, value);
        }
        return this;
    }

    public byte getByte(String key) {
        Byte value = container.get(getKey(key), PersistentDataType.BYTE);
        return value == null ? 0 : value;
    }

    public byte[] getByteArray(String key) {
        return container.get(getKey(key), PersistentDataType.BYTE_ARRAY);
    }

    public ItemWrapper setLong(String key, long... value) {
        if (value.length == 1) {
            container.set(getKey(key), PersistentDataType.LONG, value[0]);
        } else {
            container.set(getKey(key), PersistentDataType.LONG_ARRAY, value);
        }
        return this;
    }

    public long getLong(String key) {
        Long value = container.get(getKey(key), PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    public long[] getLongArray(String key) {
        return container.get(getKey(key), PersistentDataType.LONG_ARRAY);
    }

    public ItemWrapper setFloat(String key, float value) {
        container.set(getKey(key), PersistentDataType.FLOAT, value);
        return this;
    }

    public float getFloat(String key) {
        Float value = container.get(getKey(key), PersistentDataType.FLOAT);
        return value == null ? 0 : value;
    }

    public ItemWrapper setDouble(String key, double value) {
        container.set(getKey(key), PersistentDataType.DOUBLE, value);
        return this;
    }

    public double getDouble(String key) {
        Double value = container.get(getKey(key), PersistentDataType.DOUBLE);
        return value == null ? 0 : value;
    }

    public ItemWrapper setShort(String key, short value) {
        container.set(getKey(key), PersistentDataType.SHORT, value);
        return this;
    }

    public short getShort(String key) {
        Short value = container.get(getKey(key), PersistentDataType.SHORT);
        return value == null ? 0 : value;
    }

    public ItemWrapper removeKey(String key) {
        container.remove(getKey(key));
        return this;
    }

    public ItemWrapper removeKey(NamespacedKey key) {
        container.remove(key);
        return this;
    }

    public ItemMeta getMeta() {
        return meta.clone();
    }

    private static PersistentDataType<?, ?> getType(Class<?> clazz) {
        boolean array = clazz.isArray();
        if (array) {
            clazz = clazz.getComponentType();
        }
        if (clazz == byte.class) {
            return array ? PersistentDataType.BYTE_ARRAY : PersistentDataType.BYTE;
        } else if (clazz == short.class) {
            return PersistentDataType.SHORT;
        } else if (clazz == float.class) {
            return PersistentDataType.FLOAT;
        } else if (clazz == int.class) {
            return array ? PersistentDataType.INTEGER : PersistentDataType.INTEGER_ARRAY;
        } else if (clazz == double.class) {
            return PersistentDataType.DOUBLE;
        } else if (clazz == long.class) {
            return array ? PersistentDataType.LONG_ARRAY : PersistentDataType.LONG;
        } else if (clazz == String.class) {
            return PersistentDataType.STRING;
        } else if (PersistentDataContainer.class.isAssignableFrom(clazz)) {
            return PersistentDataType.TAG_CONTAINER;
        } else {
            return null;
        }
    }

    private static boolean isPrimitive(Class<?> clazz) {
        PersistentDataType<?, ?> type = getType(clazz);
        return type != null && type != PersistentDataType.TAG_CONTAINER;
    }
}
