package com.gmail.andrewandy.customoregen.addon.listener;

import com.gmail.andrewandy.corelib.util.DeregisterableListener;
import com.gmail.andrewandy.customoregen.CustomOreGen;
import com.gmail.andrewandy.customoregen.addon.CustomOreGenAddon;
import com.gmail.andrewandy.customoregen.addon.generators.IslandOreGenerator;
import com.gmail.andrewandy.customoregen.addon.levels.IslandTemplateMapper;
import com.gmail.andrewandy.customoregen.events.CustomOreGenReloadEvent;
import com.gmail.andrewandy.customoregen.generator.BlockGenerator;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import world.bentobox.bentobox.api.events.island.IslandEvent;
import world.bentobox.bentobox.database.objects.Island;

public class IslandDataHandler implements DeregisterableListener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandCreate(IslandEvent.IslandCreateEvent event) {
        Island island = event.getIsland();
        IslandTemplateMapper.getInstance().registerIslandTemplate(island.getUniqueId(), event.getBlueprintBundle().getUniqueId());
        IslandOreGenerator generator = CustomOreGenAddon.getDefaultIslandGenerator(island.getUniqueId());

        CustomOreGenAddon.getInstance().getIslandTracker(island.getUniqueId()).setGenerator(generator);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onIslandDelete(IslandEvent.IslandDeletedEvent event) {
        Island island = event.getIsland();
        IslandTemplateMapper.getInstance().unregisterIslandTemplate(island.getUniqueId());
        BlockGenerator generator = CustomOreGenAddon.getInstance().getIslandTracker(island.getUniqueId()).getGenerator();
        CustomOreGen.getGeneratorManager().unregisterGenerator(generator);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onReload(CustomOreGenReloadEvent event) {
        CustomOreGenAddon.getInstance().onReload();
    }
}
