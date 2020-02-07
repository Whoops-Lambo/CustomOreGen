package com.gmail.andrewandy.customoregen.listener;

import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.GeneratorManager;
import com.gmail.andrewandy.customoregen.events.OreGenerationEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;

public class LavaWaterListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockInteract(BlockFromToEvent event) {
        if (event.getBlock() == null && event.getToBlock().getType().equals(Material.COBBLESTONE)) {
            GeneratorManager manager = CustomOreGen.getGeneratorManager();
            Location location = event.getBlock().getLocation();
            manager.getGeneratorsAt(event.getBlock().getLocation()).stream().filter(gen -> gen.isActiveAtLocation(location)).forEach(gen -> {
                OreGenerationEvent oreEvent = new OreGenerationEvent(event, gen.generateBlockAt(location).getBlockData());
                CustomOreGen.getInstance().getServer().getPluginManager().callEvent(oreEvent);
                if (oreEvent.isCancelled()) {
                    event.setCancelled(true);
                } else {
                    event.getToBlock().setBlockData(oreEvent.getToBlock());
                }
            });
        }
    }

}
