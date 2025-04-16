package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.LongPosHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * During world gen, you can not place all your structure if it is larger than 3x3 chunks.
 * With this call, the {@link BlockListManager} are stored in a {@code Map<ChunkPos, BlockListManager>}.
 * This allows to place the manager only when allowed.
 * Use this class if you will place a structure during world gen.
 */
public class DividedBlockListManager {
    public static final Codec<DividedBlockListManager> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(BlockListManager.CODEC).fieldOf("block_list_managers").forGetter(manager -> manager.managers.values().stream().toList())
    ).apply(instance, DividedBlockListManager::new));

    private final Map<ChunkPos, BlockListManager> managers = new HashMap<>();

    public DividedBlockListManager() {
    }

    public DividedBlockListManager(List<BlockListManager> managers) {

    }

    public DividedBlockListManager put(DividedBlockListManager manager) {
        for (Map.Entry<ChunkPos, BlockListManager> entry : manager.managers.entrySet()) {
            putWithoutVerification(entry.getKey(), entry.getValue());
        }
        return this;
    }

    public DividedBlockListManager put(BlockList blockList) {
        BlockState state = blockList.getState();
        for (long pos : blockList.getPosList()) {
            managers.computeIfAbsent(LongPosHelper.getChunkPos(pos), (k) -> new BlockListManager(new BlockList())).put(state, pos);
        }
        return this;
    }


    /**
     * In a controlled environment, you can use this method which doesn't perform any verification, improving performance.
     * ! However, in the case where some {@code BlockPos} are not in the provided {@code ChunkPos} but in another place,
     * it might result in a crash or create unwanted behavior.
     *
     * @param pos       the chunkPos where the BlockPos are.
     * @param blockList the BlockList related to the chunkPos that will be put
     * @return the resulted comparator.
     */
    public DividedBlockListManager putWithoutVerification(ChunkPos pos, BlockList blockList) {
        managers.computeIfAbsent(pos, (k) -> new BlockListManager()).put(blockList);
        return this;
    }


    public DividedBlockListManager put(BlockListManager comparator) {
        for (BlockList blockList : comparator.getAllBlockList()) {
            put(blockList);
        }
        return this;
    }

    /**
     * In a controlled environment, you can use this method which doesn't perform any verification, improving performance.
     * ! However, in the case where some {@code BlockPos} are not in the provided {@code ChunkPos} but in another place,
     * it might result in a crash or create unwanted behavior.
     *
     * @param pos     the chunkPos where the BlockPos are.
     * @param manager the manager related to the chunkPos that will be put
     * @return the resulted manager.
     */
    public DividedBlockListManager putWithoutVerification(ChunkPos pos, BlockListManager manager) {
        managers.computeIfAbsent(pos, (k) -> new BlockListManager()).put(manager);
        return this;
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, LongArrayList posList) {
        for (long pos : posList) {
            put(state, tag, pos);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, LongArrayList pos) {
        for (long po : pos) {
            put(state, null, po);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, long pos) {
        return put(state, null, pos);
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, long pos) {
        managers.computeIfAbsent(LongPosHelper.getChunkPos(pos), (k) -> new BlockListManager()).put(state, tag, pos);
        return this;
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            put(state, tag, LongPosHelper.encodeBlockPos(pos));
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            put(state, null, LongPosHelper.encodeBlockPos(pos));
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, BlockPos pos) {
        return put(state, null, LongPosHelper.encodeBlockPos(pos));
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, BlockPos pos) {
        return put(state, tag, LongPosHelper.encodeBlockPos(pos));
    }

    /**
     * Method will perform verification on only one position, improving performance but might result in a crash,
     * or behave incorrectly in the case where the BlockPos in not in the good chunk.
     * Only use this method in a controlled environment.
     */
    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, LongArrayList pos) {
        managers.computeIfAbsent(LongPosHelper.getChunkPos(pos.getFirst()), (k) -> new BlockListManager()).put(state, tag, pos);
        return this;
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, LongArrayList pos) {
        return putWithoutVerification(state, null, pos);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, long pos) {
        return putWithoutVerification(state, null, LongArrayList.of(pos));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, long pos) {
        return putWithoutVerification(state, tag, LongArrayList.of(pos));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, List<BlockPos> pos) {
        return putWithoutVerification(state, tag, LongPosHelper.encodeBlockPos(pos));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, List<BlockPos> pos) {
        return putWithoutVerification(state, null, LongPosHelper.encodeBlockPos(pos));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, BlockPos pos) {
        return putWithoutVerification(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, BlockPos pos) {
        return putWithoutVerification(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, LongArrayList posList, StructurePlacementRuleManager ruler) {
        for (long pos : posList) {
            put(state, tag, pos, ruler);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, LongArrayList pos, StructurePlacementRuleManager ruler) {
        for (long po : pos) {
            put(state, null, po, ruler);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, long pos, StructurePlacementRuleManager ruler) {
        return put(state, null, pos, ruler);
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, long pos, StructurePlacementRuleManager ruler) {
        managers.computeIfAbsent(LongPosHelper.getChunkPos(pos), (k) -> new BlockListManager()).put(state, tag, pos, ruler);
        return this;
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList, StructurePlacementRuleManager ruler) {
        for (BlockPos pos : posList) {
            put(state, tag, LongPosHelper.encodeBlockPos(pos), ruler);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, List<BlockPos> posList, StructurePlacementRuleManager ruler) {
        for (BlockPos pos : posList) {
            put(state, null, LongPosHelper.encodeBlockPos(pos), ruler);
        }
        return this;
    }

    public DividedBlockListManager put(BlockState state, BlockPos pos, StructurePlacementRuleManager ruler) {
        return put(state, null, LongPosHelper.encodeBlockPos(pos), ruler);
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, BlockPos pos, StructurePlacementRuleManager ruler) {
        return put(state, tag, LongPosHelper.encodeBlockPos(pos), ruler);
    }

    public DividedBlockListManager put(BlockState state, Map<ChunkPos, LongOpenHashSet> posMap, StructurePlacementRuleManager ruler) {
        return put(state, null, posMap, ruler);
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, Map<ChunkPos, LongOpenHashSet> posMap) {
        return put(state, tag, posMap, null);
    }

    public DividedBlockListManager put(BlockState state, Map<ChunkPos, LongOpenHashSet> posMap) {
        return put(state, null, posMap, null);
    }

    public DividedBlockListManager put(BlockState state, NbtCompound tag, Map<ChunkPos, LongOpenHashSet> posMap, StructurePlacementRuleManager ruler) {
        posMap.forEach((chunkPos, longSet) -> {
            managers.computeIfAbsent(chunkPos, (o) -> new BlockListManager()).put(state, tag, new LongArrayList(longSet), ruler);
        });
        return this;
    }

    /**
     * Method will perform verification on only one position, improving performance but might result in a crash,
     * or behave incorrectly in the case where the BlockPos in not in the good chunk.
     * Only use this method in a controlled environment.
     */
    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, LongArrayList pos, StructurePlacementRuleManager ruler) {
        managers.computeIfAbsent(LongPosHelper.getChunkPos(pos.getFirst()), (k) -> new BlockListManager()).put(state, tag, pos, ruler);
        return this;
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, LongArrayList pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, null, pos, ruler);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, long pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, null, LongArrayList.of(pos), ruler);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, long pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, tag, LongArrayList.of(pos), ruler);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, List<BlockPos> pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, tag, LongPosHelper.encodeBlockPos(pos), ruler);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, List<BlockPos> pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, null, LongPosHelper.encodeBlockPos(pos), ruler);
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, BlockPos pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public DividedBlockListManager putWithoutVerification(BlockState state, NbtCompound tag, BlockPos pos, StructurePlacementRuleManager ruler) {
        return putWithoutVerification(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)), ruler);
    }

    public BlockListManager getManager(ChunkPos pos) {
        return managers.get(pos);
    }

    public BlockListManager[] getAllManager() {
        return this.managers.values().toArray(BlockListManager[]::new);
    }

    public void clear() {
        this.managers.clear();
    }

    public boolean contains(ChunkPos pos) {
        return managers.containsKey(pos);
    }

    public Set<ChunkPos> getChunkPos() {
        return managers.keySet();
    }

    public boolean placeAll(StructureWorldAccess world) {
        boolean place = true;
        for (BlockListManager manager : managers.values()) {
            if (!manager.placeAll(world)) {
                place = false;
            }
        }
        return place;
    }

    public boolean placeAllNDelete(StructureWorldAccess world) {
        boolean place = true;
        for (BlockListManager manager : managers.values()) {
            if (!manager.placeAll(world)) {
                place = false;
            }
        }
        clear();
        return place;
    }

    public OrderedBlockListManager getOrdered() {
        OrderedBlockListManager manager = new OrderedBlockListManager();
        for (BlockListManager manager1 : this.managers.values()) {
            manager.put(manager1.getOrdered());
        }
        return manager;
    }

    public OrderedBlockListManager getOrdered(BlockSorter sorter) {
        OrderedBlockListManager manager = new OrderedBlockListManager();
        for (BlockListManager manager1 : this.managers.values()) {
            manager.put(manager1.getOrdered(sorter));
        }
        return manager;
    }

    public void placeJson(StructureWorldAccess worldAccess, String name) {
        for (Map.Entry<ChunkPos, BlockListManager> entry : managers.entrySet()) {
            entry.getValue().placeJson(worldAccess, entry.getKey(), new ChunkPos(0, 0), name);
        }
    }

    public void placeJson(StructureWorldAccess worldAccess, String name, ChunkPos offset) {
        for (Map.Entry<ChunkPos, BlockListManager> entry : managers.entrySet()) {
            entry.getValue().placeJson(worldAccess, entry.getKey(), offset, name);
        }
    }

    public void putWithoutVerification(BlockListManager manager) {
        ChunkPos pos = LongPosHelper.getChunkPos(manager.getFirstBlockList().getFirstLongPos());
        manager.blockLists.forEach(blockList -> this.putWithoutVerification(pos, blockList));
    }
}
