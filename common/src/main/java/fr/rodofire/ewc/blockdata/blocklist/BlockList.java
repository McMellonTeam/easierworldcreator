package fr.rodofire.ewc.blockdata.blocklist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.rodofire.ewc.blockdata.BlockDataKey;
import fr.rodofire.ewc.blockdata.StructurePlacementRuleManager;
import fr.rodofire.ewc.util.BlockPlaceUtil;
import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.file.EwcFolderData;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;



/**
 * <p>Class used to connect BlockPos to a BlockState.</p>
 * <br>
 * <p>the class is composed of a {@link LongArrayList} that store all the BlockPos related to a {@link BlockState}. </p>
 * <p>This means that all the {@link BlockPos} of the object are connected to the BlockState</p>
 * <br>
 * <p>this is an easier version of the {@link StructureTemplate.Palette}</p>
 * This approach allows for many improvements:
 * <ul>
 * <li> BlockStates are not doubled, saving a lot of memory comparing to {@code Pair<BlockState, BlockPos>} since that no {@link BlockState} are duplicated</li>
 * <li> BlockPos are compressed into a {@link LongArrayList}, saving ~30% memory and allowing for ~70% more performance </li>
 * <li> Provides some describing on how should the Block be placed: {@code overrideBlocks} and {@code force}</li>
 * <li> provide some useful methods to simplify it's usage</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class BlockList {
    public static final Codec<BlockList> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockDataKey.CODEC.fieldOf("block_data").forGetter(blockList -> blockList.dataKey),
                    StructurePlacementRuleManager.CODEC.fieldOf("ruler").forGetter(blockList -> blockList.ruler),
                    Codec.list(Codec.LONG).fieldOf("positions").forGetter(blockList -> blockList.posList.longParallelStream().boxed().toList())
            ).apply(instance, BlockList::new)
    );

    StructurePlacementRuleManager ruler = new StructurePlacementRuleManager();

    /**
     * BlockPos are compressed into a {@link LongArrayList}, saving ~30% memory and allowing for ~70% more performance </li>
     */
    private final LongArrayList posList = new LongArrayList();
    private BlockDataKey dataKey;

    /**
     * used for placing blocks
     */
    ServerChunkCache chunkManager;
    boolean markDirty = false;
    boolean init = false;


    /**
     * init a BlockShapeManager
     *
     * @param posList pos of the state
     * @param state   the state related to the pos list
     */
    public BlockList(BlockState state, CompoundTag tag, List<BlockPos> posList) {
        addAllPos(posList);
        this.dataKey = new BlockDataKey(state, tag);
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, CompoundTag tag, BlockPos pos) {
        this(state, tag, List.of(pos));
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList pos of the state
     * @param state   the state related to the pos list
     */
    public BlockList(BlockState state, CompoundTag tag, LongArrayList posList) {
        addAllPos(posList);
        this.dataKey = new BlockDataKey(state, tag);
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, CompoundTag tag, long pos) {
        this(state, tag, LongArrayList.of(pos));
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList pos of the state
     * @param state   the state related to the pos list
     */
    public BlockList(BlockState state, List<BlockPos> posList) {
        this(state, null, posList);
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, BlockPos pos) {
        this(state, null, pos);
    }

    /**
     * init a BlockShapeManager
     *
     * @param posList pos of the state
     * @param state   the state related to the pos list
     */
    public BlockList(BlockState state, LongArrayList posList) {
        this(state, null, posList);
    }

    /**
     * init a BlockShapeManager
     *
     * @param pos   pos of the blockState
     * @param state the blockState related to the pos list
     */
    public BlockList(BlockState state, long pos) {
        this(state, null, pos);
    }

    public BlockList() {
        this(Blocks.REDSTONE_BLOCK.defaultBlockState(), null, List.of());
    }

    public BlockList(BlockDataKey blockDataKey, StructurePlacementRuleManager ruleManager, List<Long> longs) {
        this.dataKey = blockDataKey;
        this.ruler = ruleManager;
        this.posList.addAll(new LongArrayList(longs));
    }

    public int size() {
        return posList.size();
    }


    public BlockList replacePos(int index, BlockPos newPos) {
        this.posList.set(index, LongPosHelper.encodeBlockPos(newPos));
        return this;
    }

    public BlockList replacePos(int index, long newPos) {
        this.posList.set(index, newPos);
        return this;
    }

    public BlockList addAllPos(List<BlockPos> posList) {
        for (BlockPos pos : posList) {
            addPos(pos);
        }
        return this;
    }

    public BlockList addAllPos(LongArrayList posList) {
        for (long pos : posList) {
            addPos(pos);
        }
        return this;
    }

    public BlockList addPos(BlockPos pos) {
        posList.add(LongPosHelper.encodeBlockPos(pos));
        return this;
    }

    public BlockList addPos(long pos) {
        posList.add(pos);
        return this;
    }

    public BlockList setPosList(List<BlockPos> posList) {
        this.posList.clear();
        this.addAllPos(posList);
        return this;
    }

    public BlockList setPosList(LongArrayList posList) {
        this.posList.clear();
        this.addAllPos(posList);
        return this;
    }

    public BlockList set(int index, BlockPos newPos) {
        this.posList.set(index, LongPosHelper.encodeBlockPos(newPos));
        return this;
    }

    public List<BlockPos> getConvertedPosList() {
        List<BlockPos> posList = new ArrayList<>();
        for (long pos : this.posList) {
            posList.add(LongPosHelper.decodeBlockPos(pos));
        }
        return posList;
    }

    public LongArrayList getPosList() {
        return posList;
    }

    public BlockPos getPos(int index) {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(index));
    }

    public BlockPos getFirstPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getFirst());
    }

    public BlockPos getLastPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getLast());
    }

    public BlockPos getRandomPos() {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(RandomSource.create().nextIntBetweenInclusive(0, this.size() - 1)));
    }

    public BlockPos getRandomPos(RandomSource random) {
        return LongPosHelper.decodeBlockPos(this.posList.getLong(random.nextIntBetweenInclusive(0, this.size() - 1)));
    }

    public long getLongPos(int index) {
        return this.posList.getLong(index);
    }

    public long getFirstLongPos() {
        return this.posList.getFirst();
    }

    public long getLastLongPos() {
        return this.posList.getLast();
    }

    public long getRandomLongPos() {
        return this.posList.getLong(RandomSource.create().nextIntBetweenInclusive(0, this.size() - 1));
    }

    public long getRandomLongPos(RandomSource random) {
        return this.posList.getLong(random.nextIntBetweenInclusive(0, this.size() - 1));
    }

    public BlockPos removePos(int index) {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(index));
    }

    public BlockPos removeLastPos() {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(posList.size() - 1));
    }

    public BlockPos removeFirstPos() {
        return LongPosHelper.decodeBlockPos(this.posList.removeLong(posList.size() - 1));
    }

    /**
     * time complexity of O(n), avoid using this in performance crucial applications
     *
     * @return the removed pos
     */
    public BlockList removePos(BlockPos pos) {
        this.posList.removeLong(posList.indexOf(LongPosHelper.encodeBlockPos(pos)));
        return this;
    }

    public Optional<CompoundTag> getTag() {
        return Optional.ofNullable(dataKey.getTag());
    }

    public void setTag(CompoundTag tag) {
        this.dataKey.setTag(tag);
    }

    /**
     * time complexity of O(n * m), m being the length of the provided {@code List<>} avoid using it in performance crucial applications
     *
     * @return the instance of the object
     */
    public BlockList removeAll(List<BlockPos> list) {
        for (BlockPos pos : list) {
            this.posList.removeLong(posList.indexOf(LongPosHelper.encodeBlockPos(pos)));
        }
        return this;
    }


    public StructurePlacementRuleManager getRuler() {
        return ruler;
    }

    public void setRuler(StructurePlacementRuleManager ruler) {
        this.ruler = ruler;
    }

    public BlockState getState() {
        return this.dataKey.getState();
    }

    public void setState(BlockState state) {
        this.dataKey.setState(state);
    }

    public BlockDataKey getBlockData() {
        return dataKey;
    }

    public void setBlockData(BlockDataKey data) {
        this.dataKey = data;
    }

    public boolean placeLast(WorldGenLevel world) {
        return place(world, this.posList.getLast());
    }

    public boolean placeFirst(WorldGenLevel world) {
        return place(world, this.posList.getFirst());
    }

    public boolean place(WorldGenLevel world, int index) {
        return place(world, this.posList.getLong(index));
    }

    public boolean placeAll(WorldGenLevel worldAccess, int flag) {
        boolean placed = true;
        for (long pos : this.posList) {
            if (!place(worldAccess, pos, flag)) {
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeLast(WorldGenLevel world, int flag) {
        return place(world, this.posList.getLast(), flag);
    }

    public boolean placeFirst(WorldGenLevel world, int flag) {
        return place(world, this.posList.getFirst(), flag);
    }

    public boolean place(WorldGenLevel world, int index, int flag) {
        return place(world, this.posList.getLong(index), flag);
    }

    public boolean placeAll(WorldGenLevel worldAccess) {
        boolean placed = true;
        for (long pos : this.posList) {
            if (!place(worldAccess, pos) && placed) {
                placed = false;
            }
        }
        return placed;
    }

    public boolean placeLastNDelete(WorldGenLevel world) {
        return place(world, this.posList.removeLast());
    }

    /**
     * for the most performance, it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(WorldGenLevel world, int index) {
        return place(world, this.posList.removeLong(index));
    }

    /**
     * for the most performance,
     * it is recommended to not use this method where {@code placeLastNDelete()} can be applied
     */
    public boolean placeNDelete(WorldGenLevel world, int index, int flag) {
        return place(world, this.posList.removeLong(index), flag);
    }

    public boolean placeAllNDelete(WorldGenLevel worldAccess) {
        boolean placed = placeAll(worldAccess);
        this.posList.clear();
        return placed;
    }

    public boolean placeAllNDelete(WorldGenLevel worldAccess, int flag) {
        boolean placed = placeAll(worldAccess, flag);
        this.posList.clear();
        return placed;
    }

    /**
     * <p>When placing huge structures, {@link Block#UPDATE_ALL} takes up 80% of the placement.
     * <p>It not used in this method for this reason.
     * <p>You can however still modify the flags using {@link #place(WorldGenLevel, long, int)}
     */
    private boolean place(WorldGenLevel world, long pos) {
        if (!init)
            init(world);

        boolean placed;
        if ((placed = place(world, pos, Block.UPDATE_KNOWN_SHAPE)) && markDirty) {
            chunkManager.blockChanged(LongPosHelper.decodeBlockPos(pos));
        }
        return placed;
    }

    private boolean place(WorldGenLevel world, long pos, int flags) {
        BlockPos pos1 = LongPosHelper.decodeBlockPos(pos);
        BlockState state = world.getBlockState(pos1);
        if (this.ruler != null) {
            return BlockPlaceUtil.place(world, pos1, dataKey, this.ruler, flags);
        }
        return state.isAir() && BlockPlaceUtil.place(world, pos1, dataKey, null, flags);
    }

    public JsonObject toJson(ChunkPos chunkPos) {
        return toJson(new ChunkPos(0, 0), chunkPos);
    }

    public JsonObject toJson(ChunkPos offset, ChunkPos chunkPos) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        int offsetX = offset.x << 4;
        int offsetZ = offset.z << 4;

        // Calcul du coin inférieur gauche du chunk
        int chunkMinX = chunkPos.x << 4;
        int chunkMinZ = chunkPos.z << 4;

        jsonObject.addProperty("state", dataKey.getState().toString());
        if (ruler != null) {
            jsonObject.addProperty("force", ruler.isForce());
            JsonArray overriddenBlockArray = new JsonArray();
            for (Block block : ruler.getOverriddenBlocks()) {
                overriddenBlockArray.add(block.toString()); // Convertir en String
            }
            jsonObject.add("overriddenBlock", overriddenBlockArray);
        }
        if (dataKey.getTag() != null) {
            jsonObject.addProperty("tag", dataKey.getTag().toString());
        }
        addCustomProperty(jsonObject);

        //we allocate 11 bits to z and x axis, going from -1024 to 1024 blocks relative to the BlockPos. 10 bits to y.
        int size = size();
        int[] compactPositions = new int[size];

        for (int i = 0; i < size; i++) {
            BlockPos pos = LongPosHelper.decodeBlockPos(posList.getLong(i));

            // Positions relatives dans le chunk après application de l'offset
            int relX = pos.getX() - chunkMinX + offsetX;
            int relZ = pos.getZ() - chunkMinZ + offsetZ;
            int relY = pos.getY();

            if (relX < -1024 || relX > 1023 || relZ < -1024 || relZ > 1023) {
                throw new IllegalArgumentException("pos out of range: " + pos);
            }

            int compactPos = ((relX & 0x7FF) << 21) | ((relY + 512) << 11) | (relZ & 0x7FF);
            compactPositions[i] = compactPos;
        }

        // Ajout des positions compactées au JSON
        jsonObject.add("positions", gson.toJsonTree(compactPositions).getAsJsonArray());

        return jsonObject;
    }

    public void addCustomProperty(JsonObject json) {

    }

    public void placeJson(ChunkPos chunkPos) {
        toJson(new ChunkPos(0, 0), chunkPos);
    }

    public void placeJson(WorldGenLevel world, ChunkPos offset, ChunkPos chunkPos) {
        Gson gson = new Gson();
        chunkPos = new ChunkPos(chunkPos.x + offset.x, chunkPos.z + offset.z);
        Path path = EwcFolderData.getNVerifyDataDir(world, chunkPos);
        JsonObject jsonObj = toJson(offset, chunkPos);
        try {
            Files.writeString(path, gson.toJson(jsonObj));
        } catch (IOException e) {
            e.fillInStackTrace();
        }
    }

    public void init(WorldGenLevel world) {
        MinecraftServer server = world.getServer();

        if (server != null) {
            if (world instanceof ServerLevel serverWorld) {
                chunkManager = serverWorld.getChunkSource();
                markDirty = true;
            }
        }
    }
}
