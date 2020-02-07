package com.gmail.andrewandy.customoregen;

import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.block.CraftBlockState;
import org.bukkit.util.BoundingBox;

public class BlockUtils {

    public static void setBlockUsingNMS(boolean fast, BlockState toSet) {
        CraftBlockState blockState = (CraftBlockState) toSet;
        CraftChunk craftChunk = (CraftChunk) blockState.getChunk();
        Chunk chunk = craftChunk.getHandle();
        Location location = toSet.getLocation();
        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        IBlockData blockData = blockState.getHandle();
        chunk.setType(position, blockData, !fast);
    }

    public static void setBlocksUsingNMS(int x1, int y1, int z1, int x2, int y2, int z2, org.bukkit.World world, BlockState toSet) {
        WorldServer server = ((CraftWorld) world).getHandle();
        int currentX = x1;
        int currentZ = z1;
        while (currentX < x2 && currentZ < z2) {
            Chunk chunk = server.getChunkAt(currentX, currentZ);
            for (ChunkSection section : chunk.getSections()) {
                for (int currentY = y1; currentY < y2; currentY++) {
                    section.setType(currentX, currentY, currentZ, ((CraftBlockState) toSet).getHandle());
                }
            }
            currentX += 16;
            currentZ += 16;
        }
    }

    public static Block nmsGetBlock(Location location) {
        CraftWorld world = (CraftWorld) location.getBlock();
        WorldServer worldServer = world.getHandle();
        return worldServer.getChunkAt(location.getBlockX(), location.getBlockZ()).getBlockData(new BlockPosition(location.getX(), location.getY(), location.getZ())).getBlock();
    }

}
