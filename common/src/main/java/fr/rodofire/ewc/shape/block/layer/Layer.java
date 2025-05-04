package fr.rodofire.ewc.shape.block.layer;

import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Map;

@SuppressWarnings("unused")
public interface Layer {
    BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap);

    void place(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap);

    BlockListManager getVerified(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap);

    DividedBlockListManager getVerifiedDivided(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap);

    <T extends Collection<BlockPos>> BlockListManager get(T posList);

    <T extends Collection<BlockPos>> void place(WorldGenLevel world, T posList);

    <T extends Collection<BlockPos>> BlockListManager getVerified(WorldGenLevel world, T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList);

    <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, T posList);

    <U extends AbstractLongCollection> BlockListManager get(U posList);

    <U extends AbstractLongCollection> void place(WorldGenLevel world, U posList);

    <U extends AbstractLongCollection> BlockListManager getVerified(WorldGenLevel world, U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList);

    <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, U posList);

    Vec3 getCenterPos();

    void setCenterPos(Vec3 centerPos);

    Vec3 getDirectionVector();

    void setDirectionVector(Vec3i directionVector);
}
