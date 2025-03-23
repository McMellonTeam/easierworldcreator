package net.rodofire.ewc_test.shape.block.expected_shapes.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.ewc_test.shape.block.expected_shapes.instanciator.ExpectedAbstractFillableBlockShape;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

/*

                                        @@@@@@@@@@@@@@@@@@@@
                                    @@@@@#*++++++++++++++*#@@@@@
                                 @@@%#++++++++++++++++++++++++*%@@@
                                @@#++++++++++++++++++++++++++++++#@@@
                              @@@++++++++++++++++++++++++++++++++++%@@
                              @@++++++++++++++++++++++++++++++++++++%@@
                             @@%++++++++++++++++++++++++++++++++++++*@@
                             @@@++++++++++++++++++++++++++++++++++++%@@
                             @@@@++++++++++++++++++++++++++++++++++%@@@
                             @@@@@#++++++++++++++++++++++++++++++*@@%@@
                             @@@#%@@@*+++++++++++++++++++++++++%@@%#%@@
                             @@%####%@@@%#*++++++++++++++**%@@@%####%@@
                             @@%########%%%@@@@@@@@@@@@@@@%%########%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@%####################################%@@
                             @@@####################################@@
                              @@@##################################@@@
                               @@@%##############################%@@
                                 @@@@%########################%@@@@
                                    @@@@@%################%@@@@@
                                        @@@@@@@@@@@@@@@@@@@@
 */

/**
 * Class to generate cylinder related shapes
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
public class ExpectedCylinder extends ExpectedAbstractFillableBlockShape {
    private int radiusX;
    private int radiusZ;
    private int height;

    /**
     * init a Cylinder object
     *
     * @param pos     the center of the spiral
     * @param radiusX the radius of the cylinder on the x-axis
     * @param radiusZ the radius of the cylinder on the z-axis
     * @param height  the height of the cylinder
     */
    public ExpectedCylinder(@NotNull BlockPos pos, Rotator rotator, int radiusX, int radiusZ, int height) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
    }

    /**
     * init a cylinder object
     *
     * @param pos    the center of the spiral
     * @param radius the radius of the cylinder
     * @param height the height of the cylinder
     */
    public ExpectedCylinder(@NotNull BlockPos pos, int radius, int height) {
        super(pos);
        this.radiusX = radius;
        this.radiusZ = radius;
        this.height = height;
    }


    /**
     * Sets the height of the cylinder.
     *
     * <p>The height defines the vertical size of the cylinder.</p>
     *
     * @param height the height of the cylinder, in units.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Gets the height of the cylinder.
     *
     * <p>The height represents the vertical size of the cylinder.</p>
     *
     * @return the height of the cylinder, in units.
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the radius of the cylinder along the X-axis.
     *
     * <p>The radius along the X-axis determines the horizontal size of the cylinder in the X direction.</p>
     *
     * @param radius the radius of the cylinder along the X-axis, in units.
     */
    public void setRadiusX(int radius) {
        this.radiusX = radius;
    }

    /**
     * Gets the radius of the cylinder along the X-axis.
     *
     * <p>The radius along the X-axis determines the horizontal size of the cylinder in the X direction.</p>
     *
     * @return the radius of the cylinder along the X-axis, in units.
     */
    public int getRadiusX() {
        return radiusX;
    }

    /**
     * Sets the radius of the cylinder along the Z-axis.
     *
     * <p>The radius along the Z-axis determines the horizontal size of the cylinder in the Z direction.</p>
     *
     * @param radius the radius of the cylinder along the Z-axis, in units.
     */
    public void setRadiusZ(int radius) {
        this.radiusZ = radius;
    }

    /**
     * Gets the radius of the cylinder along the Z-axis.
     *
     * <p>The radius along the Z-axis determines the horizontal size of the cylinder in the Z direction.</p>
     *
     * @return the radius of the cylinder along the Z-axis, in units.
     */
    public int getRadiusZ() {
        return radiusZ;
    }

    /**
     * Method to get the BlockPos of the shape
     *
     * @return the blockPos divided into chunkPos
     */
    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        this.setFill();

        if (this.getFillingType() == Type.EMPTY) {
            this.generateEmptyCylinder();
        } else {
            this.generateFullCylinder();
        }

        return chunkMap;
    }

    @Override
    public LongOpenHashSet getCoveredChunks() {
        return new LongOpenHashSet();
    }

    /**
     * this generates a full cylinder
     *
     */
    public void generateFullCylinder() {
        int radiusXSquared = radiusX * radiusX;
        int radiusZSquared = radiusZ * radiusZ;
        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusX * radiusX;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * radiusZ * radiusZ;

        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {

            for (float x = -this.radiusX; x <= this.radiusX; x += 1f) {
                float x2 = x * x;
                float xSquared = x2 / radiusXSquared;

                for (float z = -this.radiusZ; z <= this.radiusZ; z += 1f) {
                    float z2 = z * z;
                    float zSquared = z2 / radiusZSquared;
                    if (xSquared + zSquared <= 1) {
                        boolean bl = true;
                        if (innerRadiusXSquared != 0) {
                            float innerXSquared = x2 / innerRadiusXSquared;
                            float innerZSquared = z2 / innerRadiusZSquared;
                            if (innerXSquared + innerZSquared <= 1f) {
                                bl = false;
                            }
                        }
                        if (bl) {
                            for (float y = 0; y <= this.height; y += 1f) {
                                modifyChunkMap(LongPosHelper.encodeBlockPos((int) x + centerX, (int) y + centerY, (int) z + centerZ));
                            }
                        }
                    }
                }
            }
        } else {
            for (float x = -this.radiusX; x <= this.radiusX; x += 0.5f) {
                float x2 = x * x;
                float xSquared = x * x / radiusXSquared;
                for (float z = -this.radiusZ; z <= this.radiusZ; z += 0.5f) {
                    float z2 = z * z;
                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x2 / innerRadiusXSquared;
                        float innerZSquared = z2 / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        for (float y = 0; y <= this.height; y += 0.5f) {
                            if (xSquared + (z * z) / radiusZSquared <= 1) {
                                modifyChunkMap(rotator.get(x, y, z));
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * this generates a full cylinder.
     */
    public void generateEmptyCylinder() {
        //Rotating a shape requires more blocks.
        //This verification is there to avoid some unnecessary calculations when the rotations don't have any impact on the number of blocks
        if (rotator == null) {
            for (float u = 0; u < 360; u += (float) 45 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                for (float y = 0; y <= this.height; y += 1f) {
                    modifyChunkMap(LongPosHelper.encodeBlockPos((int) (centerX + x), (int) (centerY + y), (int) (centerZ + z)));
                }
            }
        } else {
            for (float u = 0; u < 360; u += (float) 35 / Math.max(this.radiusZ, this.radiusX)) {
                float x = radiusX * FastMaths.getFastCos(u);
                float z = radiusZ * FastMaths.getFastSin(u);
                for (float y = 0; y <= this.height; y += 0.5f) {
                    modifyChunkMap(rotator.get(x, y, z));
                }
            }
        }
    }


}
