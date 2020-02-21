package com.gmail.andrewandy.customoregen.listener;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.events.OreGenerationEvent;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFormEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Represents the listener which handles overriding the vanilla lava-water cobblestone generator.
 */
public class CobbleGeneratorHandler implements Listener {

    private static Collection<Consumer<BlockFormEvent>> addons = new ArrayList<>();
    private BlockFormEvent recursive = null;

    public static void registerEventAddon(Consumer<BlockFormEvent> addon) {
        if (addon != null) {
            addons.add(addon);
        }
    }

    public static void removeEventAddon(Consumer<BlockFormEvent> addon) {
        addons.remove(addon);
    }

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
        if (originalState.getType().isSolid()) {
            return;
        }
        //Check if the new type is going to be a solid block.
        Material targetType = targetState.getType();
        Material originalType = originalState.getType();
        if (originalType.equals(Material.WATER) || originalType.equals(Material.LAVA) && (targetType.equals(Material.COBBLESTONE) || targetType.equals(Material.STONE))) {
            //Execute the generators, sequentially based on priority.
            Collection<BlockGenerator> generators = CustomOreGen.getGeneratorManager().getGeneratorsAt(original.getLocation());
            event.setCancelled(generators.size() > 0);
            boolean[] globalExecuted = new boolean[]{false};
            generators.forEach(gen -> {
                if (gen.isGlobal()) {
                    //Check if a global generated was already executed
                    if (globalExecuted[0]) {
                        //If so, skip.
                        return;
                    }
                    globalExecuted[0] = true;
                }
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
                    if (!recursive.isCancelled()) {
                        original.setBlockData(toBlock);
                    }
                }
            });
        }
    }

}
