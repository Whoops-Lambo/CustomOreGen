package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.customoregen.util.DataContainer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Order;

import java.util.Map;
import java.util.UUID;

public class DataContainerTest {

    private static DataContainer container = new DataContainer();

    @Test
    @Order(0)
    public void testAdd() {
        String key = "TEST";
        double value = 5D;
        container.set(key, value);
        Assert.assertTrue(container.containsKey(key));
        Assert.assertTrue(container.containsKeyWithType(key, double.class));
        Assert.assertFalse(container.containsKeyWithType(key, String.class));
        Assert.assertTrue(container.getDouble(key).isPresent());
        Assert.assertEquals(5D, container.getDouble(key).get(), 0.0);
    }

    @Test
    @Order(1)
    public void testRemove() {
        String key = "TEST";
        container.set(key, null);
        Assert.assertFalse(container.containsKey(key));
        Assert.assertFalse(container.containsKeyWithType(key, double.class));
        Assert.assertFalse(container.getDouble(key).isPresent());
    }

    @Test
    @Order(2)
    public void testSerialise() {
        String key = "ABC";
        String value = UUID.randomUUID().toString();
        container.set(key, value);
        Map<String, Object> map = container.serialize();
        Assert.assertEquals(new DataContainer(map), container);
    }
}
