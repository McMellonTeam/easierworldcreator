package fr.rodofire.ewc.blockdata;

import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.map.ObjectShortLinkHashBiMap;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.Long2ShortOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * class to collect blockState from a world based on a {@link BlockPos} list
 */
@SuppressWarnings("unused")
public class WorldStateCollector {
    ObjectShortLinkHashBiMap<BlockDataKey> data = new ObjectShortLinkHashBiMap<>();
    Long2ShortOpenHashMap posLink;

    public WorldStateCollector() {
        this(30);
    }

    public WorldStateCollector(int capacity) {
        posLink = new Long2ShortOpenHashMap(capacity);
        posLink.defaultReturnValue((short) -1);
    }

    public <T extends AbstractLongCollection> WorldStateCollector collect(ServerLevel world, T posList) {
        for (long pos : posList) {
            if (posLink.containsKey(pos))
                continue;

            BlockPos decodedPos = LongPosHelper.decodeBlockPos(pos);
            BlockDataKey data = getDataKey(world, decodedPos);

            this.posLink.put(pos, this.data.put(data));
        }
        return this;
    }

    public <T extends Collection<BlockPos>> WorldStateCollector collect(ServerLevel world, T posList) {
        for (BlockPos decodedPos : posList) {

            long pos = LongPosHelper.encodeBlockPos(decodedPos);
            if (posLink.containsKey(pos))
                continue;

            BlockDataKey data = getDataKey(world, decodedPos);

            this.posLink.put(pos, this.data.put(data));
        }
        return this;
    }

    private static @NotNull BlockDataKey getDataKey(ServerLevel world, BlockPos decodedPos) {
        BlockState state = world.getBlockState(decodedPos);

        BlockDataKey data = new BlockDataKey(state);

        //get NbtTag of the block
        BlockEntity entity = world.getBlockEntity(decodedPos);
        if (entity != null) {
            RegistryAccess registry = world.registryAccess();
            CompoundTag currentNbt = entity.saveWithFullMetadata(registry);
            data.setTag(currentNbt);
        }
        return data;
    }

    public BlockState getState(long pos) {
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index).getState();
    }

    public BlockDataKey getData(long pos) {
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index);
    }

    public BlockState getState(BlockPos decodedPos) {
        long pos = LongPosHelper.encodeBlockPos(decodedPos);
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index).getState();
    }

    public BlockDataKey getData(BlockPos decodedPos) {
        long pos = LongPosHelper.encodeBlockPos(decodedPos);
        short index = posLink.get(pos);
        if (index == -1)
            return null;
        return this.data.get(index);
    }
}
