package fr.rodofire.ewc.world.chunk;

import fr.rodofire.ewc.config.ewc.EwcConfig;
import fr.rodofire.ewc.util.ChunkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ChunkPosManager {
    private final Map<ChunkPos, ChunkState> chunkStateMap = new ConcurrentHashMap<>();
    private ChunkPos offset = new ChunkPos(0, 0);

    private final @NotNull WorldGenLevel world;

    public ChunkPosManager(@NotNull WorldGenLevel world) {
        this.world = world;
    }

    public ChunkPosManager(@NotNull WorldGenLevel world, Set<ChunkPos> toTest) {
        this.world = world;
        putAll(toTest);
    }

    public ChunkPosManager(@NotNull WorldGenLevel world, ChunkPos toTest) {
        this.world = world;
        this.put(toTest);
    }

    /**
     * Method to avoid chain call. If a chunk is not generated, it is added to the non-generated chunks. If the chunk is generated and can be placed, it will be added
     *
     * @param chunkPos the pos to put
     * @return true, it can accept world-gen blocks, false if not
     */
    public boolean put(ChunkPos chunkPos) {
        ChunkState state = chunkStateMap.get(chunkPos);
        if (state != null) {
            return state != ChunkState.UNALLOWED;
        }

        if (!ChunkUtil.isFeaturesGenerated(world, chunkPos)) {
            chunkStateMap.put(chunkPos, ChunkState.UNGENERATED);
            return true;
        }

        if (canBeAllowed(chunkPos)) {
            chunkStateMap.put(chunkPos, ChunkState.ALLOWED);
            return true;
        }

        chunkStateMap.put(chunkPos, ChunkState.UNALLOWED);
        return false;
    }

    /**
     * Method to know if a chunk  chain call.
     * If a chunk is not generated, it is added to the non-generated chunks.
     * If the chunk is generated and can be placed, it will be added to the allowed one.
     * If not, nothing is done
     *
     * @param chunkPos the pos to put
     * @return true, it can accept world-gen blocks, false if not
     */
    private boolean putSafe(ChunkPos chunkPos) {
        //The chunk is then not present. We verify if the chunk was generated
        if (!ChunkUtil.isFeaturesGenerated(world, chunkPos)) {
            chunkStateMap.put(chunkPos, ChunkState.UNGENERATED);
            return true;
        }
        return false;
    }

    private boolean canBeAllowed(ChunkPos chunkPos) {
        int distance = EwcConfig.getFeaturesChunkDistance();
        for (int x = -distance; x <= distance; x++) {
            for (int z = -distance; z <= distance; z++) {
                if (z == 0 && x == 0) continue;

                ChunkPos pos = new ChunkPos(chunkPos.x + x, chunkPos.z + z);
                ChunkState state = chunkStateMap.get(pos);

                if (state == ChunkState.UNGENERATED) {
                    return true;
                }

                if (state == null && putSafe(pos)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void putAll(Set<ChunkPos> chunks) {
        chunks.forEach(this::put);
    }

    public boolean containsChunk(ChunkPos chunkPos) {
        return chunkStateMap.containsKey(chunkPos);
    }


    /**
     * method to know if the shape should use multi-chunk feature
     *
     * @param posSetMap the map that will determine if the shape is multi-chunk
     * @return true if it is, false if not
     */
    public boolean isMultiChunk(Set<ChunkPos> posSetMap, BlockPos centerPos) {
        int distance = EwcConfig.getFeaturesChunkDistance();
        if (posSetMap.size() > Math.pow(2 * distance + 1, 2))
            return true;
        for (ChunkPos set : posSetMap) {
            if (set.x > distance + new ChunkPos(centerPos).x || set.x < -distance + new ChunkPos(centerPos).x)
                return true;
            if (set.z > distance + new ChunkPos(centerPos).z || set.z < -distance + new ChunkPos(centerPos).z)
                return true;
        }
        return false;
    }

    public boolean canPlaceMultiChunk(Set<ChunkPos> chunkPosSet, int maxDistance) {
        int minOffset = Integer.MAX_VALUE;
        for (int distance = 0; distance <= maxDistance; distance++) {
            for (int x = -distance; x <= distance; x++) {
                for (int z = -distance; z <= distance; z++) {
                    if (Math.abs(x) < distance && Math.abs(z) < distance) {
                        continue;
                    }

                    if (canGenerate(chunkPosSet, x, z)) {
                        this.offset = new ChunkPos(x, z);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canGenerate(Set<ChunkPos> chunkPosSet, int x, int z) {
        return chunkPosSet.parallelStream().noneMatch(chunkPos -> {
            chunkPos = new ChunkPos(chunkPos.x + x, chunkPos.z + z);


            ChunkState existingState = chunkStateMap.get(chunkPos);

            if (existingState != null) {
                return existingState == ChunkState.UNALLOWED;
            }

            //avoid crash with computeIfAbsent
            ChunkState newState;
            if (!ChunkUtil.isFeaturesGenerated(world, chunkPos)) {
                newState = ChunkState.UNGENERATED;
            } else if (canBeAllowed(chunkPos)) {
                newState = ChunkState.ALLOWED;
            } else {
                newState = ChunkState.UNALLOWED;
            }

            chunkStateMap.put(chunkPos, newState);
            return newState == ChunkState.UNALLOWED;
        });
    }

    public ChunkPos getOffset() {
        return offset;
    }

    private enum ChunkState {
        UNGENERATED, ALLOWED, UNALLOWED
    }

}
