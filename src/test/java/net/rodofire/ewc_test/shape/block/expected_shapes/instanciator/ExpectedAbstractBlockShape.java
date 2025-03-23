package net.rodofire.ewc_test.shape.block.expected_shapes.instanciator;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Class to create custom shapes
 * <br>
 * The Main purpose of this class is to generate the coordinates based on a shape.
 * The coordinates are organized depending on a {@code Map<ChunkPos, LongOpenHashSet>}.
 * <p>It emply some things:
 * <ul>
 *     <li>The coordinates are divided in chunk</li>
 *     <li>It uses {@link LongOpenHashSet} for several reasons.
 *     <ul>
 *     <li>First, We use a set to avoid doing unnecessary calculations on the shape. It ensures that no duplicate is present.
 *     <li>Second, it compresses the BlockPos: The {@link BlockPos} are saved under long using {@link LongPosHelper}.
 *     It saves some memory since that we save four bytes of data for each {@link BlockPos},
 *     and there should not have overhead since that we use primitive data type.
 *     <li>Third, since that we use primitive data types and that they take less memory,
 *     coordinate generation, accession or deletion is much faster than using a {@code Set<BlockPos>}.
 *     Encoding and decoding blockPos and then adding it into {@link LongOpenHashSet}is extremely faster
 *     compared to only adding a {@link BlockPos}.
 *     ~60- 70% facter.
 *     </ul>
 *     </li>
 * </ul>
 * <p>Dividing Coordinates into Chunk has some advantages :
 * <ul>
 *     <li> allow a multithreaded block assignement when using {@link LayerManager}
 *     <li> allow to be used during WG, when using {@link DividedBlockListManager} or when placing using {@link ShapePlacer}
 * </ul>
 */
@SuppressWarnings("unused")
public abstract class ExpectedAbstractBlockShape {
    protected long centerPos;

    /**
     * precomputed ints for slightly higher performance
     */
    protected int centerX;
    protected int centerY;
    protected int centerZ;

    protected Rotator rotator;

    protected Map<ChunkPos, LongOpenHashSet> chunkMap = new HashMap<>();

    /**
     * instead of using always get on {@code chunkMap} which is pretty expensive in terms of performance,
     * we use a mutable object which will avoid the call of {@link Map#get(Object)} if the {@link ChunkPos} is the same.
     */
    MutableObject<ChunkPos> lastChunkPos = new MutableObject<>(null);
    MutableObject<LongOpenHashSet> lastSet = new MutableObject<>(null);

    /**
     * init the Shape
     *
     * @param centerPos the center of the spiral
     */
    public ExpectedAbstractBlockShape(@NotNull BlockPos centerPos) {
        this.centerPos = LongPosHelper.encodeBlockPos(centerPos);
        setCenterPos();
    }

    /**
     * init the shape
     *
     * @param centerPos the center BlockPos
     * @param rotator   the rotator uses to rotate the shape
     */
    public ExpectedAbstractBlockShape(BlockPos centerPos, Rotator rotator) {
        this.centerPos = LongPosHelper.encodeBlockPos(centerPos);
        this.rotator = rotator;
        setCenterPos();
    }

    public void setRotator(Rotator rotator) {
        this.rotator = rotator;
    }

    /**
     * method to get the coordinates that will be placed later
     *
     * @return a map of ChunkPos of blockPos for every shape
     */
    public abstract Map<ChunkPos, LongOpenHashSet> getShapeCoordinates();

    private void setCenterPos() {
        centerX = LongPosHelper.decodeX(centerPos);
        centerY = LongPosHelper.decodeY(centerPos);
        centerZ = LongPosHelper.decodeZ(centerPos);
    }

    /**
     * Method to add a pos to the map.
     * We use {@link MutableObject} to avoid using too much {@link Map#get(Object)}, enhancing performance
     *
     * @param pos the compressed {@link BlockPos} that will be added
     */
    protected void modifyChunkMap(long pos) {
        ChunkPos chunkPos = LongPosHelper.getChunkPos(pos);

        if (!chunkPos.equals(lastChunkPos.getValue())) {
            lastChunkPos.setValue(chunkPos);
            lastSet.setValue(chunkMap.computeIfAbsent(chunkPos, k -> new LongOpenHashSet()));
        }

        lastSet.getValue().add(pos);
    }

    public abstract LongOpenHashSet getCoveredChunks();
}