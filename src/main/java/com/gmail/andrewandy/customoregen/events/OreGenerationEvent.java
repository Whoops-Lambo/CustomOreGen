package com.gmail.andrewandy.customoregen.events;

import org.bukkit.block.data.BlockData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockFromToEvent;

import java.util.Objects;

/**
 * Called when a generator generated an block.
 */
public class OreGenerationEvent extends Event implements Cancellable {

    protected static HandlerList handlers = new HandlerList();
    private BlockFromToEvent original;
    private BlockData toBlock;

    public OreGenerationEvent(BlockFromToEvent original, BlockData newBlock) {
        this.original = Objects.requireNonNull(original);
        this.toBlock = Objects.requireNonNull(newBlock);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return original.isCancelled();
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.original.setCancelled(cancel);
    }

    public BlockData getToBlock() {
        return toBlock;
    }

    public void setToBlock(BlockData toBlock) {
        this.toBlock = toBlock;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
