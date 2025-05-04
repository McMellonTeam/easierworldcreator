package fr.rodofire.ewc.world.chunk;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public interface ChunkRegionUtil {
    /**
     * Method to get a chunk. If the chunk doesn't exists yet, it will return null instead of crashing the game
     * @param chunkX the x coordinate of the chunk
     * @param chunkZ the z coordinate of the chunk
     * @param leastStatus the status wanted
     */
    ChunkAccess ewc_main$getNullableChunk(int chunkX, int chunkZ, ChunkStatus leastStatus, boolean create);
}
