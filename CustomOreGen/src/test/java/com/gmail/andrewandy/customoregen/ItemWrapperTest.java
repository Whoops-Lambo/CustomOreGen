package com.gmail.andrewandy.customoregen;

import be.seeseemelk.mockbukkit.inventory.ItemFactoryMock;
import com.gmail.andrewandy.customoregen.util.ItemWrapper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;

public class ItemWrapperTest {

    @Test()
    @Order(2)
    public void checkDataValidity() {
        ItemFactoryMock factory = new ItemFactoryMock();
        ItemMeta meta = factory.getItemMeta(Material.COBBLESTONE);
        ItemWrapper wrapper = ItemWrapper.wrap(meta);
        String customString = "&b&lCustomString";
        wrapper.setString("OriginalString", customString);
        wrapper.setByte("OriginalStringByte", customString.getBytes());
        //test clone persistence.
        meta = wrapper.getMeta().clone();
        ItemWrapper other = ItemWrapper.wrap(meta);
        String str = other.getString("OriginalString");
        //Check data integrity
        Assert.assertEquals(str, customString);
        byte[] bytes = other.getByteArray("OriginalStringByte");
        Assert.assertEquals(new String(bytes), customString);
        wrapper.setString("OriginalString", "OverWrite");
        //Check if the string was overwritten - it should have.
        Assert.assertNotEquals(wrapper.getString("OriginalString"), customString);
        //Check if the arrays are equal when retrieved by a generic (array) class
        Assert.assertArrayEquals(wrapper.get("OriginalStringByte", byte[].class), bytes);
    }

    @Test
    @Order(1)
    public void checkInvalidClass() {
        ItemWrapper wrapper = ItemWrapper.wrap(new ItemFactoryMock().getItemMeta(Material.CHICKEN_SPAWN_EGG));
        wrapper.setLong("Number", 10L);
        try {
            int num = wrapper.getInt("Number");
            Assert.assertEquals(num, 0);
        } catch (IllegalArgumentException ignored) {
        }
        try {
            wrapper.get("Number", Runtime.class);
            Assert.fail("Invalid Class allowed!");
            return;
        } catch (IllegalArgumentException ignored) {
        }
        Assert.assertTrue(wrapper.hasKeyOfType("Number", Long.class, false));
        Assert.assertFalse(wrapper.hasKeyOfType("Number", Integer.class, false));
        Assert.assertTrue(wrapper.hasKeyOfType("Number", long.class, false));
        try {
            wrapper.hasKeyOfType("Number", Runtime.class, true);
            Assert.fail("Invalid Class allowed!");
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Test
    @Order(0)
    public void checkInvalidConstructorParams() {
        ItemWrapper wrapper;
        try {
            wrapper = ItemWrapper.wrap((ItemMeta) null);
            Assert.fail("Null meta allowed!");
            return;
        } catch (NullPointerException ignored) {
        }

        try {
            wrapper = ItemWrapper.wrap((ItemStack) null);
            Assert.fail("Null ItemStack allowed!");
            return;
        } catch (NullPointerException ignored) {
        }
        wrapper = ItemWrapper.wrap(new ItemFactoryMock().getItemMeta(Material.ENDER_CHEST));
        byte[] arr = new byte[]{0, 1, 5, 25};
        wrapper.setByte("Bytes", arr);
        try {
            byte invalidKey = wrapper.getByte("Byte");
            //Check that the invalid key will return 0.
            Assert.assertEquals(invalidKey, 0);
            byte b = wrapper.getByte("Bytes");
            //If the type-check internally at the PersistentDataContainer doesn't return null, it should
            //throw an exception or return null which would then be returned as the default value of 0.
            Assert.assertEquals(b, 0);
        } catch (IllegalArgumentException ignored) {
        }
    }
}
