package com.gmail.andrewandy.customoregen;

import com.gmail.andrewandy.customoregen.generator.builtins.GenerationChanceHelper;
import org.bukkit.Material;
import org.junit.Assert;
import org.junit.Test;

public class GenerationChanceTest {

    @Test
    public void testMath() {
        GenerationChanceHelper wrapper = new GenerationChanceHelper();
        int denominator = 0;
        int numerator = 20;
        Material[] materials = new Material[]{Material.COBBLESTONE, Material.DIAMOND_ORE, Material.END_STONE_BRICKS};
        for (Material material : materials) {
            wrapper.addBlockChance(material.name(), numerator);
            denominator += numerator;
            double percentage = 100 * numerator / (double) denominator;
            if (percentage != wrapper.getPercentageChance(material.name())) {
                Assert.fail("Chances were calculated incorrectly!");
            }
        }
    }

}
