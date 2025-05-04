package fr.rodofire.ewc.util;


import fr.rodofire.ewc.config.ewc.EwcConfig;
import fr.rodofire.ewc.world.chunk.ChunkRegionUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * util class related to chunks
 */
@SuppressWarnings("unused")
public class ChunkUtil {

    /**
     * method to know if the features of a chunk were generated
     *
     * @param world    the world where the
     * @param chunkPos the chunk that will be tested
     * @return <p>- true if the features of the chunk were generated. <p>- false if not
     */
    public static boolean isFeaturesGenerated(LevelReader world, ChunkPos chunkPos) {
        ChunkAccess chunk = ((ChunkRegionUtil) world).ewc_main$getNullableChunk(chunkPos.x, chunkPos.z, ChunkStatus.EMPTY, false);
        return !(chunk instanceof ProtoChunk && chunk.getPersistedStatus().isBefore(ChunkStatus.FEATURES));
    }

    public static boolean areNearbyFeaturesUnGenerated(LevelReader world, ChunkPos chunkPos) {
        if (!isFeaturesGenerated(world, chunkPos)) {
            return true;
        }
        int distance = EwcConfig.getFeaturesChunkDistance();
        for (int i = -distance; i <= distance; i++) {
            for (int j = -distance; j <= distance; j++) {
                ChunkPos newChunkPos = new ChunkPos(chunkPos.x + i, chunkPos.z + j);
                if (!isFeaturesGenerated(world, newChunkPos)) {
                    return true;
                }
            }
        }
        return false;
    }
}
