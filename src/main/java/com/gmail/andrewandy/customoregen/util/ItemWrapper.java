package com.gmail.andrewandy.customoregen.util;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class ItemWrapper {

    public static JavaPlugin plugin;
    private final PersistentDataContainer container;
    private volatile ItemMeta meta;

    /**
     * @see #wrap(ItemMeta)
     */
    private ItemWrapper(ItemMeta meta) {
        this.meta = Objects.requireNonNull(meta);
        this.container = meta.getPersistentDataContainer();
    }

    public static void setPlugin(JavaPlugin instance) {
        plugin = Objects.requireNonNull(instance);
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

    private static PersistentDataType<?, ?> getType(Class<?> clazz) {
        boolean array = clazz.isArray();
        if (array) {
            clazz = clazz.getComponentType();
        }
        PersistentDataType<?, ?> ret;
        if (clazz == byte.class) {
            ret = array ? PersistentDataType.BYTE_ARRAY : PersistentDataType.BYTE;
        } else if (clazz == short.class) {
            ret = PersistentDataType.SHORT;
        } else if (clazz == float.class) {
            ret = PersistentDataType.FLOAT;
        } else if (clazz == int.class) {
            ret = array ? PersistentDataType.INTEGER : PersistentDataType.INTEGER_ARRAY;
        } else if (clazz == double.class) {
            ret = PersistentDataType.DOUBLE;
        } else if (clazz == long.class) {
            ret = array ? PersistentDataType.LONG_ARRAY : PersistentDataType.LONG;
        } else if (clazz == String.class) {
            ret = PersistentDataType.STRING;
        } else if (PersistentDataContainer.class.isAssignableFrom(clazz)) {
            ret = PersistentDataType.TAG_CONTAINER;
        } else {
            ret = null;
        }
        return ret;
    }

    private NamespacedKey getKey(String name) {
        return new NamespacedKey(plugin, name);
    }

    public <T> boolean hasKeyOfType(String key, Class<T> clazz, boolean nullValueExpected) {
        return nullValueExpected && (get(key, clazz) == null);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        if (!clazz.isPrimitive()) {
            throw new UnsupportedOperationException("Only primitives are supported!");
        }
        NamespacedKey namespacedKey = getKey(key);
        PersistentDataType<?, ?> type = getType(clazz);
        assert type != null;
        return (T) container.get(namespacedKey, type);
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

    public Integer getInt(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.INTEGER, 0);
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

    public Byte getByte(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.BYTE, (byte) 0);
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

    public Long getLong(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.LONG, 0L);
    }

    public long[] getLongArray(String key) {
        return container.get(getKey(key), PersistentDataType.LONG_ARRAY);
    }

    public ItemWrapper setFloat(String key, float value) {
        container.set(getKey(key), PersistentDataType.FLOAT, value);
        return this;
    }

    public Float getFloat(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.FLOAT, 0F);
    }

    public ItemWrapper setDouble(String key, double value) {
        container.set(getKey(key), PersistentDataType.DOUBLE, value);
        return this;
    }

    public Double getDouble(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.DOUBLE, 0D);
    }

    public ItemWrapper setShort(String key, short value) {
        container.set(getKey(key), PersistentDataType.SHORT, value);
        return this;
    }

    public short getShort(String key) {
        return container.getOrDefault(getKey(key), PersistentDataType.SHORT, (short) 0);
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
}
