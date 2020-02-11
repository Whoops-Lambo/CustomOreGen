package com.gmail.andrewandy.customoregen.generator.builtins;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

/**
 * Represents the listener which handles overriding the vanilla lava-water cobblestone generator.
 */
public class CobbleGeneratorHandler implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onCobbleGeneration(BlockFromToEvent event) {
        Block original = event.getBlock();
        Block newBlock = event.getToBlock();
    }

}
