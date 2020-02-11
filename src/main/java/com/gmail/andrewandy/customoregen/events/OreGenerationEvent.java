package com.gmail.andrewandy.customoregen.events;

import org.bukkit.block.data.BlockData;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Objects;

/**
 * Called when a generator generated an block.
 */
public class OreGenerationEvent extends Event implements Cancellable {

    protected static HandlerList handlers = new HandlerList();
    private boolean cancel;
    private BlockData toBlock;

    public OreGenerationEvent(BlockData newBlock) {
        this.toBlock = Objects.requireNonNull(newBlock);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
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
