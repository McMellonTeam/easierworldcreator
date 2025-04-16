package net.rodofire.easierworldcreator.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.BlockDataKey;
import net.rodofire.easierworldcreator.blockdata.StructurePlacementRuleManager;
import net.rodofire.easierworldcreator.blockdata.sorter.BlockSorter;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.easierworldcreator.util.file.EwcFolderData;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * class to manage a list of DefaultBlockList automatically
 */
@SuppressWarnings("unused")
public class BlockListManager {
    public static final Codec<BlockListManager> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(BlockList.CODEC).fieldOf("block_lists").forGetter(manager -> manager.blockLists)
    ).apply(instance, BlockListManager::new));

    /**
     * the List of BlockList that are managed
     */
    protected List<BlockList> blockLists = new ArrayList<>();

    /**
     * Map used for better performance:
     * <ul>
     * <li>Instead of searching for a {@code BlockSate} inside the list, we use {@code indexes}.
     * <li>The map allows us to get the place of the related {@code BlockState} in the {@code posList}.
     * <li>Instead of having a time complexity of O(n), we get O(1) by using only a small amount of memory in more.
     * </ul>
     * <p> We use a Shorts to make the link between the {@code Pair<BlockState, NbtCompound>} and the {@code List<BlockList>}.
     * In the case where no {@link NbtCompound} is present, we put a {@code null} value to it.
     * <p>
     * Using short as a link allow us to save two bytes of data for each {@link BlockState}. Since that it is highly unprobable that more than 32 000 {@link BlockState} are used, shorts are enough.
     * <p>
     * </p>
     */
    protected List<BlockDataKey> stateIndexes = new ArrayList<>();

    protected Object2ShortOpenHashMap<BlockDataKey> blockDataMap = new Object2ShortOpenHashMap<>();

    /**
     * init a comparator
     *
     * @param comparator the comparator that will be fused
     */
    public BlockListManager(BlockListManager comparator) {
        this.blockLists = comparator.blockLists;
        this.stateIndexes = comparator.stateIndexes;
        this.blockDataMap = comparator.blockDataMap;
    }

    /**
     * init a comparator
     *
     * @param blockList the list of blockList that will be indexed
     */
    public BlockListManager(List<BlockList> blockList) {
        put(blockList);
    }

    /**
     * init a comparator
     *
     * @param blockList a blockList that will be indexed
     */
    public BlockListManager(BlockList blockList) {
        put(blockList);
    }

    /**
     * init an empty comparator
     */
    public BlockListManager() {
    }

    public BlockList getBlockList(int index) {
        return blockLists.get(index);
    }

    public List<BlockList> getAllBlockList() {
        return blockLists;
    }

    public BlockList getFirstBlockList() {
        return blockLists.getFirst();
    }

    public BlockList getLastBlockList() {
        return blockLists.getLast();
    }

    public BlockState getState(int index) {
        return stateIndexes.get(index).getState();
    }

    public short size() {
        return (short) blockLists.size();
    }


    /**
     * Method to know the number of all blockPos stored in every {@link BlockList}.
     * This is notabely used in {@link OrderedBlockListManager}.
     * To avoid too much rehash, we count the size?.
     */
    public int totalSize() {
        int sum = 0;
        for (BlockList blockList : blockLists) {
            sum += blockList.size();
        }
        return sum;
    }

    public int stateSize() {
        return stateIndexes.size();
    }

    public BlockListManager put(BlockState state, NbtCompound tag, LongArrayList pos) {
        return put(state, tag, pos, null);
    }

    /**
     * Method to put a list of encoded blockPos in the manager.
     * In the case where the pair of {@link BlockState} and {@link NbtCompound} is already present,
     * the method will fuse the encoded posList in the related blockList.
     * In the other case, the method will create a new BlockList linked to the pair.
     *
     * @return the modified instance of the manager
     */
    public BlockListManager put(BlockState state, NbtCompound tag, LongArrayList pos, StructurePlacementRuleManager ruler) {
        BlockDataKey blockData = new BlockDataKey(state, tag);
        if (this.blockDataMap.containsKey(blockData)) {
            short index = this.blockDataMap.getShort(blockData);
            this.blockLists.get(index).addAllPos(pos);
            if (ruler != null) {
                this.blockLists.get(index).setRuler(ruler);
            }
            return this;
        }
        short index = size();
        this.blockDataMap.put(blockData, index);
        this.blockLists.add(new BlockList(state, tag, pos));
        return this;
    }

    public BlockListManager put(BlockState state, NbtCompound tag, long pos) {
        return put(state, tag, LongArrayList.of(pos));
    }

    public BlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList) {
        return put(state, tag, LongPosHelper.encodeBlockPos(posList));
    }

    public BlockListManager put(BlockState state, NbtCompound tag, BlockPos pos) {
        return put(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public BlockListManager put(BlockState state, LongArrayList pos) {
        return put(state, null, pos);
    }

    public BlockListManager put(BlockState state, long pos) {
        return put(state, null, LongArrayList.of(pos));
    }

    public BlockListManager put(BlockState state, List<BlockPos> posList) {
        return put(state, null, LongPosHelper.encodeBlockPos(posList));
    }

    public BlockListManager put(BlockState state, BlockPos pos) {
        return put(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)));
    }

    public BlockListManager put(BlockList[] blockLists) {
        for (BlockList blockList : blockLists) {
            put(blockList);
        }
        return this;
    }

    public BlockListManager put(List<BlockList> blockLists) {
        for (BlockList blockList : blockLists) {
            put(blockList);
        }
        return this;
    }

    public BlockListManager put(BlockState state, NbtCompound tag, long pos, StructurePlacementRuleManager ruler) {
        return put(state, tag, LongArrayList.of(pos), ruler);
    }

    public BlockListManager put(BlockState state, NbtCompound tag, List<BlockPos> posList, StructurePlacementRuleManager ruler) {
        return put(state, tag, LongPosHelper.encodeBlockPos(posList), ruler);
    }

    public BlockListManager put(BlockState state, NbtCompound tag, BlockPos pos, StructurePlacementRuleManager ruler) {
        return put(state, tag, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)), ruler);
    }

    public BlockListManager put(BlockState state, LongArrayList pos, StructurePlacementRuleManager ruler) {
        return put(state, null, pos, ruler);
    }

    public BlockListManager put(BlockState state, long pos, StructurePlacementRuleManager ruler) {
        return put(state, null, LongArrayList.of(pos), ruler);
    }

    public BlockListManager put(BlockState state, List<BlockPos> posList, StructurePlacementRuleManager ruler) {
        return put(state, null, LongPosHelper.encodeBlockPos(posList), ruler);
    }

    public BlockListManager put(BlockState state, BlockPos pos, StructurePlacementRuleManager ruler) {
        return put(state, null, LongArrayList.of(LongPosHelper.encodeBlockPos(pos)), ruler);
    }

    public BlockListManager put(BlockList blockList) {
        BlockDataKey blockData = blockList.getBlockData();

        if (this.blockDataMap.containsKey(blockData)) {
            short index = this.blockDataMap.getShort(blockData);
            this.blockLists.get(index).addAllPos(blockList.getPosList());
            return this;
        }

        short index = size();
        this.blockDataMap.put(blockData, index);
        this.stateIndexes.add(blockData);
        this.blockLists.add(blockList);
        return this;
    }

    public BlockListManager put(BlockListManager manager) {
        for (BlockList blockList : manager.blockLists) {
            put(blockList);
        }
        return this;
    }

    public OrderedBlockListManager getOrdered() {
        return new OrderedBlockListManager(this);
    }

    public OrderedBlockListManager getOrdered(BlockSorter sorter) {
        return sorter.sortOrderedBlockList(new OrderedBlockListManager(this));
    }

    @Override
    public String toString() {
        return this.blockLists.toString();
    }

    public BlockListManager sort(BlockSorter sorter) {
        sorter.sortInsideBlockList(this);
        return this;
    }

    public void clear() {
        this.blockLists.clear();
        this.stateIndexes.clear();
        this.blockDataMap.clear();
    }

    public boolean placeAll(StructureWorldAccess worldAccess) {
        boolean placed = true;
        for (BlockList blockList : this.blockLists) {
            if (!blockList.placeAll(worldAccess)) {
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess) {
        boolean placed = true;
        for (BlockList blockList : this.blockLists) {
            if (!blockList.placeAllNDelete(worldAccess)) {
                placed = false;
            }
        }
        clear();
        return placed;
    }

    public boolean placeAll(StructureWorldAccess worldAccess, int flag) {
        boolean placed = true;
        for (BlockList blockList : this.blockLists) {
            if (!blockList.placeAll(worldAccess, flag)) {
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeAllNDelete(StructureWorldAccess worldAccess, int flag) {
        boolean placed = true;
        for (BlockList blockList : this.blockLists) {
            if (!blockList.placeAllNDelete(worldAccess, flag)) {
                placed = false;
            }
        }
        clear();
        return placed;
    }


    public JsonArray toJson(ChunkPos chunkPos) {
        return toJson(chunkPos, new ChunkPos(0, 0));
    }

    public JsonArray toJson(ChunkPos chunkPos, ChunkPos offset) {
        JsonArray jsonArray = new JsonArray();

        if (blockLists.isEmpty()) return new JsonArray();

        ForkJoinPool pool = new ForkJoinPool(Math.max(1, Math.min(blockLists.size(), Runtime.getRuntime().availableProcessors() / 2)));
        List<CompletableFuture<JsonObject>> futures = new ArrayList<>();

        // Création des CompletableFutures pour chaque BlockList
        for (BlockList blockList : blockLists) {
            CompletableFuture<JsonObject> future = CompletableFuture.supplyAsync(() -> blockList.toJson(offset, chunkPos), pool);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        for (CompletableFuture<JsonObject> future : futures) {
            try {
                jsonArray.add(future.get());
            } catch (InterruptedException | ExecutionException e) {
                e.fillInStackTrace();
            }
        }

        pool.shutdown();
        return jsonArray;
    }

    public void placeJson(StructureWorldAccess worldAccess, ChunkPos chunkPos) {
        placeJson(worldAccess, chunkPos, new ChunkPos(0, 0), "custom_feature_" + Random.create().nextLong());
    }

    /**
     * convert the manager into a Json file
     *
     * @param chunkPos the chunkpos of the manager. Positions will be written relative to this blockPos
     * @param offset   the offset to move the blockPos
     */
    public void placeJson(StructureWorldAccess worldAccess, ChunkPos chunkPos, ChunkPos offset, String name) {
        Gson gson = new Gson();
        chunkPos = new ChunkPos(chunkPos.x + offset.x, chunkPos.z + offset.z);
        Path path = EwcFolderData.getNVerifyDataDir(worldAccess, chunkPos);
        JsonArray jsonArray = toJson(chunkPos, offset);
        if (jsonArray.isEmpty()) return;

        try {
            Files.writeString(path.resolve(name + ".json"), gson.toJson(jsonArray));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

}
