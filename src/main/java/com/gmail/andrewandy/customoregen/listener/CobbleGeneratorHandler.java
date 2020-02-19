package com.gmail.andrewandy.customoregen.listener;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.events.OreGenerationEvent;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

/**
 * Represents the listener which handles overriding the vanilla lava-water cobblestone generator.
 */
public class CobbleGeneratorHandler implements Listener {

    private BlockFormEvent recursive = null;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCobbleGeneration(BlockFormEvent event) {
        //Check if the event was just called, return to prevent an infinite recursion.
        if (recursive == event) {
            return;
        }
        Block original = event.getBlock();
        BlockState originalState = original.getState();
        BlockState targetState = event.getNewState();
        //Check if it was originally a liquid (Water)
        if (!originalState.getType().isSolid()) {
            return;
        }
        //Check if the new type is going to be a solid block.
        Material targetType = targetState.getType();
        if (originalState.getType() == Material.WATER && (targetType == Material.COBBLESTONE || targetType == Material.STONE)) {
            //Execute the generators, sequentially based on priority.
            CustomOreGen.getGeneratorManager().getGeneratorsAt(original.getLocation()).forEach(gen -> {
                //Should be active because this is checked in #getGeneratorsAt.
                assert gen.isActiveAtLocation(original.getLocation());
                BlockData toBlock = gen.generateBlockAt(original.getLocation());
                //Call a new ore gen event.
                OreGenerationEvent genEvent = new OreGenerationEvent(toBlock);
                if (!genEvent.isCancelled()) {
                    //If the generation event isn't cancelled call a new block form event.
                    targetState.setBlockData(toBlock);
                    //Assign to the recursive variable so this listener doesn't loop forever.
                    recursive = new BlockFormEvent(original, targetState);
                    recursive.callEvent();
                }
            });
        }
    }

}
