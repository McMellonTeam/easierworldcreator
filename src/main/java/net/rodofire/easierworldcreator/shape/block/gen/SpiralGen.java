package net.rodofire.easierworldcreator.shape.block.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractBlockShape;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

//helicoid shape
/*







                                                                           ......
                                                                       ...::::::..
                                                                   ....:::::::::::..
                                                                ...::::::::::::::::..
                                                              ..:::::::::::::::::::::.
                                                          ..::::::::::::::::::::::::::..
                                                      ...::::::::::::::::::::::::::::::..
                                                   ..:::::::::::::::::::::::::::::::::::..
                                               ...:::::::::::::::::::::::::::::::::::::::..
                                            ...:::::::::::::::::::::::::::::::::::::::::::..
                                        ....:::::::::::::::::::::::::::::::::::::::::::::::.
                                      ....::::::::::::::::::::::::::::::::::::::::::::::::::.
                                  ........::::::::::::::::::::::::::::::::::::::::::::::::::..
                               .........:::::::::::::::::::::::::::::::::::::::::::::::::::::.
                            ...........::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                          ............::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                        .............:::::::::::::::::::::::::::::::::::::::::::::::::::::::::.
                       .............::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                       ............:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                        ...........:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::..
                          ..........:::::::::::::::::::::::::--------------:::::::::::::::::::..
                            .........:::::::::::::::::::-------------------------:::::::::::::..
                                .......:::::::::::::::--------------------------------::::::::.
                                   .....:::::::::::::--------------------------------------:::.
                                     ....:::::::::::----------------------------------------:..
                                       ....:::::::::----------------------------------------:.
                                         ...::::::::----------------------------------------..
                                          ...::::::::--------------------------------------:.
                                            ..:::::::--------------------------------------..
                                             ..::::::-------------------------------------:.
                                             ...::::::-----------------------------------:.
                                              ..::::::----------------------------------:.
                                               .::::::---------------------------------:.
                                               ..::::::-------------------------------:.
                                               ..:::::::-----------------------------:.
                                               ..:::::::---------------------------:..
                                               ..::::::::-------------------------:.
                                               .:::::::::-----------------------:..
                                              ..:::::::::----------------------:.
                                             ..:::::::::::-------------------:.
                                            ...:::::::::::-----------------:.
                                           ...:::::::::::::--------------:..
                                          ...::::::::::::::------------:.
                                        ...::::::::::::::::---------:..
                                      ....::::::::::::::::::-------:.
                                    ....:::::::::::::::::::------==-.
                 .............    ....:::::::::::::::::::----======-.
          ..........::::::::::::....::::::::::::::::::::--==========:
       ..................::.......::::::::::::::::::::-=============-
     ..........................::::::::::::::::::---================-.
     .......................::::::::::::::::-----===================-.
     ..................:::::::::::::::::::--------==================-
       ..........:::::::::::::........:::::-------==================:
               ..........             ..::::-------================-.
                                        ..:::------================-.
                                          .:::------==============-.
                                           .:::-----================:.
                                            ..::------================:.
                                             ..::-----==================:.
                                              .::------==================-.
                                               .::------===================:.
                                               .::-------===================-..
                                               .:::--------===================:
                                               .:::---------===================:.
                                               .:::--------=====================:.
                                               .:::-------=======================:.
                                              ..:::-----==========================:.
                                              .:::-----===========================-.
                                             .:::::--==============================:.
                                            .:::::-================================-.
                                          ..::::-===================================:.
                                         ..::--=====================================-.
                                      ....:---=======================================:.
                                    ...::------======================================-.
                                ....:::--------======================================-.
                            .....:::::------------===================================-.
                                  ..:::-------------=================================-.
                                     ..::------------================================-.
                                       ..::--------------============================-.
                                         ..:-------------------======================:.
                                            .:---------------------------------------:
                                              .:-------------------------------------.
                                                .-----------------------------------:.
                                                  .---------------------------------.
                                                    .------------------------------:.
                                                     ..---------------------------:.
                                                       .:-------------------------.
                                                         .:----------------------.
                                                           .:-------------------.
                                                             .:----------------:
                                                               ..-------------.
                                                                 .:---------:.
                                                                   ..------.
                                                                     .:--:.
                                                                       ...

     */

/**
 * Class to generate spiral related shapes
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
public class SpiralGen extends AbstractBlockShape {
    public static final Codec<SpiralGen> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockPos.CODEC.fieldOf("center").forGetter(shape -> LongPosHelper.decodeBlockPos(shape.centerPos)),
                    Rotator.CODEC.fieldOf("rotator").forGetter(shape -> shape.rotator),
                    Codec.pair(
                            Codec.INT,
                            Codec.INT
                    ).fieldOf("radius_x").forGetter(shape -> shape.radiusX),

                    Codec.pair(
                            Codec.INT,
                            Codec.INT
                    ).fieldOf("radius_z").forGetter(shape -> shape.radiusZ),

                    Codec.INT.fieldOf("height").forGetter(shape -> shape.height),
                    Codec.INT.fieldOf("spiral_offset").forGetter(shape -> shape.spiralOffset),
                    Codec.FLOAT.fieldOf("outline_radius_x").forGetter(shape -> shape.outlineRadiusX),
                    Codec.FLOAT.fieldOf("outline_radius_z").forGetter(shape -> shape.outlineRadiusZ),
                    Codec.FLOAT.fieldOf("turn_number").forGetter(shape -> shape.turnNumber),
                    SpiralType.CODEC.fieldOf("spiral_type").forGetter(shape -> shape.spiralType)
            ).apply(instance, SpiralGen::new)
    );

    //radius on the x-axis
    private Pair<Integer, Integer> radiusX;
    //radius on the z-axis
    private Pair<Integer, Integer> radiusZ;
    //height of the spiral
    private int height;
    //offset of the spiral in degrees, set to 0 by default, but you can change it
    private int spiralOffset = 0;
    //the number of turn that the spiral do between the start and the end, by default, this value is set to 1
    private float turnNumber = 1;
    //set the type of spiral
    private SpiralType spiralType = SpiralType.DEFAULT;
    private float spiralFilling = 1f;
    //set the radius of the outline on the x-axis
    private float outlineRadiusX = 1;
    //set the radius of the outline on the z-axis
    private float outlineRadiusZ = 1;

    /**
     * The angle of the side.
     * <p>It can change during the generation.
     * <p>To change the angle you can do as following : {@code new Pair<>{startAngle, endAngle}}
     */
    private Pair<Integer, Integer> helicoidAngle = new Pair<>(0, 0);

    /**
     * init the Spiral Shape
     *
     * @param pos        the center of the spiral
     * @param radiusX    the radius on the x-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param radiusZ    the radius on the z-axis. The first value corresponding to the radius at the base of the spiral, the second, corresponding to the radius at the top of the spiral
     * @param height     the height of the spiral
     * @param turnNumber the number of turn that the spiral will do (ex: 1 -> 1 turn, 3.5 -> 3.5 turn)
     */
    public SpiralGen(@NotNull BlockPos pos, Rotator rotator, Pair<Integer, Integer> radiusX, Pair<Integer, Integer> radiusZ, int height, float turnNumber) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
        this.turnNumber = turnNumber;
    }

    /**
     * @param pos    the center of the spiral
     * @param radius the radius of the spiral
     * @param height the height of the spiral
     */
    public SpiralGen(@NotNull BlockPos pos, int radius, int height) {
        super(pos);
        this.radiusX = new Pair<>(radius, radius);
        this.radiusZ = new Pair<>(radius, radius);
        this.height = height;
    }

    public SpiralGen(BlockPos pos, Rotator rotator, Pair<Integer, Integer> radiusX, Pair<Integer, Integer> radiusZ, Integer height, Integer spiralOffset, Float outlineRadiusX, Float outlineRadiusZ, Float turnNumber, SpiralType spiralType) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusZ = radiusZ;
        this.height = height;
        this.spiralOffset = spiralOffset;
        this.outlineRadiusX = outlineRadiusX;
        this.outlineRadiusZ = outlineRadiusZ;
        this.turnNumber = turnNumber;
        this.spiralType = spiralType;
    }

    public void setOutlineRadiusZ(int outlineRadiusZ) {
        this.outlineRadiusZ = outlineRadiusZ;
    }

    public void setOutlineRadiusX(int outlineRadiusX) {
        this.outlineRadiusX = outlineRadiusX;
    }

    public void setSpiralFilling(float spiralFilling) {
        this.spiralFilling = spiralFilling;
    }

    public void setSpiralType(SpiralType spiralType) {
        this.spiralType = spiralType;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setRadiusZ(Pair<Integer, Integer> radiusZ) {
        this.radiusZ = radiusZ;
    }

    public void setRadiusX(Pair<Integer, Integer> radiusX) {
        this.radiusX = radiusX;
    }

    public void setTurnNumber(float turnNumber) {
        this.turnNumber = turnNumber;
    }

    /**
     * @param spiralOffset the offset of the start of the spiral
     */
    public void setSpiralOffset(int spiralOffset) {
        this.spiralOffset = spiralOffset;
    }

    /**
     * @param helicoidAngle the start and the end angle of the blocks on the side
     */
    public void setHelicoidAngle(Pair<Integer, Integer> helicoidAngle) {
        this.helicoidAngle = helicoidAngle;
    }


    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        this.getFilling();
        switch (this.spiralType) {
            case DEFAULT:
                this.generateEllipsoidSpiral(new BlockPos(0, 0, 0));
                break;

            case HELICOID, HALF_HELICOID, CUSTOM_HELICOID:
                this.generateHelicoid();
                break;

            case DOUBLE_HELICOID, HALF_DOUBLE_HELICOID, CUSTOM_DOUBLE_HELICOID:
                this.generateHelicoid();
                this.spiralOffset = 180;
                this.generateHelicoid();
                break;

            case LARGE_OUTLINE:
                this.generateLargeOutlineSpiral();
                break;

            case FULL_LARGE_OUTLINE:
                float intermediateOutlineX = this.outlineRadiusX;
                float intermediateOutlineZ = this.outlineRadiusZ;

                float max = Math.max(intermediateOutlineX, intermediateOutlineZ);
                for (float i = 0; i < max; i += 0.5f) {
                    this.outlineRadiusX = i * (intermediateOutlineX) / max;
                    this.outlineRadiusZ = i * (intermediateOutlineZ) / max;

                    this.generateLargeOutlineSpiral();
                }
                break;
        }
        return chunkMap;
    }

    @Override
    public LongOpenHashSet getCoveredChunks() {
        covered = new LongOpenHashSet();
        this.getFilling();

        switch (this.spiralType) {
            case DEFAULT:
                this.getEllipsoidSpiralCovered(new BlockPos(0, 0, 0));
                break;

            case HELICOID, HALF_HELICOID, CUSTOM_HELICOID:
                this.getHelicoidCovered();
                break;

            case DOUBLE_HELICOID, HALF_DOUBLE_HELICOID, CUSTOM_DOUBLE_HELICOID:
                this.getHelicoidCovered();
                this.spiralOffset = 180;
                this.getHelicoidCovered();
                break;

            case LARGE_OUTLINE:
                this.getLargeOutlineSpiralCovered();
                break;

            case FULL_LARGE_OUTLINE:
                float intermediateOutlineX = this.outlineRadiusX;
                float intermediateOutlineZ = this.outlineRadiusZ;

                float max = Math.max(intermediateOutlineX, intermediateOutlineZ);
                for (float i = 0; i < max; i += 0.5f) {
                    this.outlineRadiusX = i * (intermediateOutlineX) / max;
                    this.outlineRadiusZ = i * (intermediateOutlineZ) / max;

                    this.getLargeOutlineSpiralCovered();
                }
                break;
        }
        return covered;
    }

    @Override
    public void place(StructureWorldAccess world, BlockLayerManager blockLayerManager) {

    }

    /**
     * generates a simple spiral
     *
     * @param pos the center of the spiral. This can be changed to match certain needing like when generating a large outline
     */
    private void generateEllipsoidSpiral(BlockPos pos) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getFirst(), radiusX.getSecond()), Math.max(radiusZ.getFirst(), radiusZ.getSecond()));
        float f = (this.turnNumber * maxLarge);
        float a = (float) 360 / (height * maxLarge);
        float limit = maxLarge * this.turnNumber * height;

        if (rotator == null) {
            for (float i = 0; i < limit; i++) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                int x = (int) (radiusX * FastMaths.getFastCos(ai));
                int z = (int) (radiusZ * FastMaths.getFastSin(ai));
                int y = (int) (i / f);
                modifyChunkMap(LongPosHelper.encodeBlockPos(x + centerX + pos.getX(), y + centerY, z + centerZ + pos.getZ()));
            }
        } else {
            for (float i = 0; i < limit; i += 0.5f) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float x = radiusX * FastMaths.getFastCos(ai);
                float z = radiusZ * FastMaths.getFastSin(ai);
                float y = i / f;
                modifyChunkMap(rotator.get(x + pos.getX(), y, z + pos.getZ()));
            }
        }
    }


    /**
     * this allows the generation of a large outline spiral.
     */
    private void generateLargeOutlineSpiral() {
        float angle = (float) Math.atan(height / turnNumber);
        int degAngle = (int) Math.toDegrees(angle);
        Vec3d vec = new Vec3d(FastMaths.getFastCos(degAngle), FastMaths.getFastSin(degAngle), 0).normalize();
        double cosY = FastMaths.getFastCos(degAngle);
        double sinY = FastMaths.getFastSin(degAngle);

        float maxLarge = Math.max(outlineRadiusX, outlineRadiusZ);
        for (float i = 0; i < 360; i += 45 / maxLarge) {
            double x = outlineRadiusX * FastMaths.getFastCos(i);
            double z = outlineRadiusZ * FastMaths.getFastSin(i);
            this.generateEllipsoidSpiral(rotator.getRawBlockPos((float) x, (float) 0, (float) z));
        }
    }

    /**
     * generates a helicoid if the {@link SpiralType} is set to {@code HELICOID} or {@code DOUBLE_HELICOID} with their variants
     */
    private void generateHelicoid() {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getFirst(), radiusX.getSecond()), Math.max(radiusZ.getFirst(), radiusZ.getSecond()));
        float f = (this.turnNumber * maxLarge);
        float a = (float) 360 / (height * maxLarge);
        float limit = maxLarge * this.turnNumber * height;


        if (rotator == null && this.helicoidAngle.getFirst() < 45 && this.helicoidAngle.getFirst() > -45 && this.helicoidAngle.getSecond() < 45 && this.helicoidAngle.getSecond() > -45) {
            for (float i = 0; i < limit; i++) {
                float ai = a * i + spiralOffset;

                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);
                float xpr = gainX * FastMaths.getFastCos(ai);
                float zpr = gainZ * FastMaths.getFastSin(ai);

                for (float j = 0; j <= maxLarge; j++) {

                    int x = (int) (xpr * j);
                    int z = (int) (zpr * j);
                    double distance = FastMaths.getLength(x, z);


                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        int y = (int) ((int) (i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        modifyChunkMap(LongPosHelper.encodeBlockPos(x + centerX, y + centerY, z + centerZ));
                    }
                }
            }
        } else {
            for (float i = 0; i < limit; i += 0.25f) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                float xpr = gainX * FastMaths.getFastCos(ai);
                float zpr = gainZ * FastMaths.getFastSin(ai);

                for (float j = 0; j <= maxLarge; j++) {

                    float x = (xpr * j);
                    float z = (zpr * j);

                    float distance = FastMaths.getLength(x, z);


                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        float y = ((i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        modifyChunkMap(rotator.get(x, y, z));
                    }
                }
            }
        }
    }

    /**
     * change the filling of the spiral to avoid some possible issues if the value is badly set
     */
    private void getFilling() {
        if (spiralType == SpiralType.HELICOID || spiralType == SpiralType.DOUBLE_HELICOID) {
            this.spiralFilling = 1f;
        } else if (spiralType == SpiralType.HALF_HELICOID || spiralType == SpiralType.HALF_DOUBLE_HELICOID) {
            this.spiralFilling = 0.5f;
        }
    }


    /**
     * this method returns the {@code xRadius} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    private float getXRadius(float percentage) {
        return (int) (radiusX.getFirst() * (1 - percentage) + radiusZ.getSecond() * percentage);
    }

    /**
     * this method returns the {@code zRadius} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the x radius of the spiral
     */
    private float getZRadius(float percentage) {
        return (int) (radiusZ.getFirst() * (1 - percentage) + radiusZ.getSecond() * percentage);
    }

    /**
     * this method returns the {@code helicoïdAngle} depending on the height we are at
     *
     * @param percentage the percentage of the height we are at
     * @return the angle of the spiral
     */
    private int getAngle(float percentage) {
        return (int) (helicoidAngle.getFirst() * (1 - percentage) + helicoidAngle.getSecond() * percentage);
    }

    /**
     * generates a simple spiral
     *
     * @param pos the center of the spiral. This can be changed to match certain needing like when generating a large outline
     */
    private void getEllipsoidSpiralCovered(BlockPos pos) {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getFirst(), radiusX.getSecond()), Math.max(radiusZ.getFirst(), radiusZ.getSecond()));
        float f = (this.turnNumber * maxLarge);
        float a = (float) 360 / (height * maxLarge);
        float limit = maxLarge * this.turnNumber * height;
        int lastChunkX = Integer.MAX_VALUE, lastChunkZ = Integer.MAX_VALUE;

        if (rotator == null) {
            for (float i = 0; i < limit; i++) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                int x = (int) (radiusX * FastMaths.getFastCos(ai));
                int z = (int) (radiusZ * FastMaths.getFastSin(ai));
                shouldAddChunk(x + centerX, z + centerZ);
            }
        } else {
            for (float i = 0; i < limit; i += 0.5f) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float x = radiusX * FastMaths.getFastCos(ai);
                float z = radiusZ * FastMaths.getFastSin(ai);
                float y = i / f;
                BlockPos rotated = rotator.getBlockPos(x + pos.getX(), y, z + pos.getZ());
                shouldAddChunk(rotated.getX(), rotated.getZ());
            }
        }
    }


    /**
     * this allows the generation of a large outline spiral.
     */
    private void getLargeOutlineSpiralCovered() {
        float angle = (float) Math.atan(height / turnNumber);
        int degAngle = (int) Math.toDegrees(angle);
        Vec3d vec = new Vec3d(FastMaths.getFastCos(degAngle), FastMaths.getFastSin(degAngle), 0).normalize();
        double cosY = FastMaths.getFastCos(degAngle);
        double sinY = FastMaths.getFastSin(degAngle);

        float maxLarge = Math.max(outlineRadiusX, outlineRadiusZ);
        for (float i = 0; i < 360; i += 45 / maxLarge) {
            double x = outlineRadiusX * FastMaths.getFastCos(i);
            double z = outlineRadiusZ * FastMaths.getFastSin(i);
            this.getEllipsoidSpiralCovered(rotator.getRawBlockPos((float) x, (float) 0, (float) z));
        }
    }

    /**
     * generates a helicoid if the {@link SpiralType} is set to {@code HELICOID} or {@code DOUBLE_HELICOID} with their variants
     */
    private void getHelicoidCovered() {
        /*if (this.turnNumber <= 0) {
            Easierworldcreator.LOGGER.error("param turn can't be <= 0");
        }*/
        int maxLarge = Math.max(Math.max(radiusX.getFirst(), radiusX.getSecond()), Math.max(radiusZ.getFirst(), radiusZ.getSecond()));
        float f = (this.turnNumber * maxLarge);
        float a = (float) 360 / (height * maxLarge);
        float limit = maxLarge * this.turnNumber * height;


        if (rotator == null && this.helicoidAngle.getFirst() < 45 && this.helicoidAngle.getFirst() > -45 && this.helicoidAngle.getSecond() < 45 && this.helicoidAngle.getSecond() > -45) {
            for (float i = 0; i < limit; i++) {
                float ai = a * i + spiralOffset;

                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);
                float xpr = gainX * FastMaths.getFastCos(ai);
                float zpr = gainZ * FastMaths.getFastSin(ai);

                for (float j = 0; j <= maxLarge; j++) {

                    int x = (int) (xpr * j);
                    int z = (int) (zpr * j);
                    double distance = FastMaths.getLength(x, z);

                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        shouldAddChunk(x + centerX, z + centerZ);
                    }
                }
            }
        } else {
            for (float i = 0; i < limit; i += 0.25f) {
                float ai = a * i + spiralOffset;
                float percentage = i / (limit);
                float radiusX = this.getXRadius(percentage);
                float radiusZ = this.getZRadius(percentage);
                float gainX = radiusX / maxLarge;
                float gainZ = radiusZ / maxLarge;

                float innerRadiusX = (1 - this.spiralFilling) * radiusX;
                float innerRadiusZ = (1 - this.spiralFilling) * radiusZ;
                float innerRadiusXSquared = innerRadiusX * innerRadiusX;
                float innerRadiusZSquared = innerRadiusZ * innerRadiusZ;

                int helicoidAngle = getAngle(percentage);

                float xpr = gainX * FastMaths.getFastCos(ai);
                float zpr = gainZ * FastMaths.getFastSin(ai);

                for (float j = 0; j <= maxLarge; j++) {

                    float x = (xpr * j);
                    float z = (zpr * j);

                    float distance = FastMaths.getLength(x, z);


                    boolean bl = true;
                    if (innerRadiusXSquared != 0) {
                        float innerXSquared = x * x / innerRadiusXSquared;
                        float innerZSquared = z * z / innerRadiusZSquared;
                        if (innerXSquared + innerZSquared <= 1f) {
                            bl = false;
                        }
                    }
                    if (bl) {
                        float y = ((i / f) + distance * FastMaths.getFastSin(helicoidAngle));
                        BlockPos pos = rotator.getBlockPos(x, y, z);
                        shouldAddChunk(pos.getX(), pos.getZ());
                    }
                }
            }
        }
    }

    /**
     * set every possible spiral shape of the mod
     */
    public enum SpiralType {
        /**
         * default shape
         */
        DEFAULT,
        /**
         * helicoid shape, blocks are posed between the center axis and the outline
         */
        HELICOID,
        /**
         * helicoid shape, blocks are posed between the center of the axis and the outline
         */
        HALF_HELICOID,
        CUSTOM_HELICOID,
        /**
         * helicoid shape,this generates helicoid 2 with an opposite direction
         */
        DOUBLE_HELICOID,
        /**
         * helicoid shape,this generates helicoid 2 with a hole in the middle with an opposite direction
         */
        HALF_DOUBLE_HELICOID,
        CUSTOM_DOUBLE_HELICOID,
        LARGE_OUTLINE,
        /**
         * same as large outline except that it is full on the inside
         */
        FULL_LARGE_OUTLINE;

        public static final Codec<SpiralType> CODEC = Codec.STRING.xmap(SpiralType::valueOf, SpiralType::name);
    }

}
