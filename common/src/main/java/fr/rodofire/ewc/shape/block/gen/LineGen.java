package fr.rodofire.ewc.shape.block.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.shape.block.instanciator.AbstractBlockShape;
import fr.rodofire.ewc.shape.block.layer.LayerManager;
import fr.rodofire.ewc.shape.block.placer.ShapePlacer;
import fr.rodofire.ewc.shape.block.rotations.Rotator;
import fr.rodofire.ewc.util.LongPosHelper;
import fr.rodofire.ewc.util.WorldGenUtil;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Class to generate line related shapes
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
public class LineGen extends AbstractBlockShape {
    public static final Codec<LineGen> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockPos.CODEC.fieldOf("start").forGetter(shape -> LongPosHelper.decodeBlockPos(shape.centerPos)),
                    Rotator.CODEC.fieldOf("rotator").forGetter(shape -> shape.rotator),
                    BlockPos.CODEC.fieldOf("end").forGetter(shape -> shape.secondPos)
            ).apply(instance, LineGen::new)
    );

    private BlockPos secondPos;

    /**
     * init the Line Shape
     *
     * @param pos       the position of the start of the line
     * @param secondPos the second pos on which the line has to go
     */
    public LineGen(@NotNull BlockPos pos, Rotator rotator, BlockPos secondPos) {
        super(pos, rotator);
        this.secondPos = secondPos;
    }

    /**
     * init the Line Shape
     *
     * @param pos       the position of the start of the line
     * @param secondPos the second pos on which the line has to go
     */
    public LineGen(@NotNull BlockPos pos, BlockPos secondPos) {
        super(pos);
        this.secondPos = secondPos;
    }

    public void setSecondPos(BlockPos secondPos) {
        this.secondPos = secondPos;
    }

    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        Direction direction;
        if ((direction = WorldGenUtil.getDirection(LongPosHelper.decodeBlockPos(this.centerPos), secondPos)) != null) {
            this.generateAxisLine(direction);
        } else {
            this.drawLine();
        }
        return chunkMap;
    }

    @Override
    public LongOpenHashSet getCoveredChunks() {
        BlockPos.MutableBlockPos pos1 = LongPosHelper.decodeBlockPos(this.centerPos).mutable();
        pos1.set(pos1.getX() >> 4, 0, pos1.getZ() >> 4);

        BlockPos.MutableBlockPos pos2 = secondPos.mutable();
        pos2.set(pos2.getX() >> 4, 0, pos2.getZ() >> 4);
        int estimatedSurface = (int) WorldGenUtil.getDistance(pos1, pos2);

        covered = new LongOpenHashSet(estimatedSurface);
        getCovered();
        return covered;
    }

    @Override
    public void place(WorldGenLevel world, BlockLayerManager blockLayerManager) {

    }

    /**
     * this method generates the coordinates
     *
     * @param dir the direction of the line
     */
    private void generateAxisLine(Direction dir) {
        int length = (int) WorldGenUtil.getDistance(LongPosHelper.decodeBlockPos(centerPos), secondPos);
        for (int i = 0; i < length; i++) {
            modifyChunkMap(LongPosHelper.offset(dir, centerPos, i));
        }
    }


    private void drawLine() {
        modifyChunkMap(this.centerPos);

        int x1 = centerX;
        int y1 = centerY;
        int z1 = centerZ;
        int x2 = secondPos.getX();
        int y2 = secondPos.getY();
        int z2 = secondPos.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int xs = x1 < x2 ? 1 : -1;
        int ys = y1 < y2 ? 1 : -1;
        int zs = z1 < z2 ? 1 : -1;

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;
                modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1));
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1));
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                modifyChunkMap(LongPosHelper.encodeBlockPos(x1, y1, z1));
            }
        }
    }

    private void getCovered() {
        shouldAddChunk(centerX, centerZ);

        int x1 = centerX;
        int y1 = centerY;
        int z1 = centerZ;
        int x2 = secondPos.getX();
        int y2 = secondPos.getY();
        int z2 = secondPos.getZ();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int dz = Math.abs(z2 - z1);

        int xs = x1 < x2 ? 1 : -1;
        int ys = y1 < y2 ? 1 : -1;
        int zs = z1 < z2 ? 1 : -1;

        if (dx >= dy && dx >= dz) {
            int p1 = 2 * dy - dx;
            int p2 = 2 * dz - dx;
            while (x1 != x2) {
                x1 += xs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dx;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dx;
                }
                p1 += 2 * dy;
                p2 += 2 * dz;

                shouldAddChunk(x1, z1);
            }
        } else if (dy >= dx && dy >= dz) {
            int p1 = 2 * dx - dy;
            int p2 = 2 * dz - dy;
            while (y1 != y2) {
                y1 += ys;
                if (p1 >= 0) {
                    x1 += xs;
                    p1 -= 2 * dy;
                }
                if (p2 >= 0) {
                    z1 += zs;
                    p2 -= 2 * dy;
                }
                p1 += 2 * dx;
                p2 += 2 * dz;
                shouldAddChunk(x1, z1);
            }
        } else {
            int p1 = 2 * dy - dz;
            int p2 = 2 * dx - dz;
            while (z1 != z2) {
                z1 += zs;
                if (p1 >= 0) {
                    y1 += ys;
                    p1 -= 2 * dz;
                }
                if (p2 >= 0) {
                    x1 += xs;
                    p2 -= 2 * dz;
                }
                p1 += 2 * dy;
                p2 += 2 * dx;
                shouldAddChunk(x1, z1);
            }
        }
    }
}
