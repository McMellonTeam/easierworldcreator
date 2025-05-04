package fr.rodofire.ewc.shape.block.layer;

import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.util.LongPosHelper;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractOuterLayer extends AbstractRadialLikeLayer {
    protected float maxDistance;

    AbstractOuterLayer(BlockLayerManager blockLayer, Vec3 center, Vec3i direction) {
        super(blockLayer, center, direction);
    }

    AbstractOuterLayer(BlockLayerManager blockLayer, Vec3 center) {
        super(blockLayer, center);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.get(posList);
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.get(posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(WorldGenLevel world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerified(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(WorldGenLevel world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        super.place(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(WorldGenLevel world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerified(world, posList);
    }

    @Override
    public BlockListManager getVerified(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getVerified(world, posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getDivided(posList);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getDivided(posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, T posList) {
        for (BlockPos pos : posList) {
            float distance = maxDistance - this.getDistance(pos.getX(), pos.getY(), pos.getZ());
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerifiedDivided(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        return super.getVerifiedDivided(world, posList);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        return super.getVerifiedDivided(world, posMap);
    }

    @Override
    public <U extends AbstractLongCollection> void place(WorldGenLevel world, U posList) {
        for (long pos : posList) {
            float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
            maxDistance = Math.max(distance, maxDistance);
        }
        super.place(world, posList);
    }

    @Override
    public void place(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        for (LongOpenHashSet set : posMap.values()) {
            for (long pos : set) {
                float distance = maxDistance - this.getDistance(LongPosHelper.decodeBlockPos2Array(pos));
                maxDistance = Math.max(distance, maxDistance);
            }
        }
        super.place(world, posMap);
    }
}
