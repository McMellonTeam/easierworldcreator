package net.rodofire.easierworldcreator.shape.block.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.DirectionUtil;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/*



                                ..:::::::::....
                      .-==-==-::==-:--==+=======----::-=-:.
                 .-=---=::::-=+=+-:::-=---===--=-::--:::==--::-=-:.
              =+=--==:--====-========-=---=++=+-===+=----:-==-:::-:::-+-
           :*====+*++++++++++=====--:-+*=-=+=**===*++==::==-::-+==:::-::::+*:
         =#=----=+-=====+*======*====++++===++******+**+=+++=-==-:---:::--:::-==:
       *##==+*+=+--------+::--=+#++++*+++++=++*##%@@@@@%#%++===::--:--+++==+-::::::-.
      *##*====-=++===++=-==::---=+=-====--====+=+++##*###*++=+*-::==----=::-:::..::-:::.
     *****==*+===+=-----=++=---:::-++=--:=+=--==-+-==*=+===-====-:-*-::.-+-....-:--:::..:=
    .***##=---====+++=------+====-:::==+-:::=+--=:.--=*=*==-:-:==--=::.-=-:...-=-.....:=::.:
    .*#****====++===+-:--=+==--=-:-+--:-==*-:::=:-=+--===++--=-==.::-::-=-..::---....:-::..::=
    .*++**+#+=--=-=-==*+--------=++-:::-=::::=#+-:-=::-=:.--::-:::=--==-...:=:.:-.=-.....-:...:=
     #*****+*+=====++-=-+=---==------++=-:::-=::::-::-=+==-=+===+:..:-.:.--:..---:....-::- ..::.::
     *@**+++++++++=-=====--+*::-----=------+#-::::+::::-:::::+:::::::.:=:..=+=.....-:...::::.  ..=.:
     :@@#++*++**++===========-=+=-==-------+:::-:=#==---*----==----+=:-==-:..:.-:.....-=- .. -:. :.::
      -@@%*++*+++++++==+=-------=+==++--:-=::::::-=:::::=:::::--:::::-::..:--:...::-.::...: .  ..-...:
       -%%@%*+=++++++**+=-=--=-=+-------+*=:--:::+::-::::*::::::=:::::--::...-::.::==-. ....-. ..-... .
         #@@%%*+++++*==+++++==+=--------==----=+=#===-:::=:::::::=::::::-::-=--==......-......::-   :.-
          .@%##%#++*++===++=+**+--------+--------+--------=:----=+=-:::::=:::::::=::.....=....::.:  . -
            -#####%*+====++=*+====+++=-+------------------+::::-::=-::::::-=:::::::-:..:..:--:....:  .+
              .*%######*=+=++=========*==+*+----=-----------::--:::+::::::::-:::::::-:::=-:::=......::=
                 =#########*=========+=========+#*+=--=---==::::::::+::::::::=::---====:::::::-.....-+-
                    *%######*##**====+==========+================--==+=--====-+-:::-----:::::::-::====
                      -@%#########*##+==========+============---==---=---------+--------=:::::-=*=--=:
                         .*@%#########*****+++===-=========+===-====-==--------==--------=-=++==*==:
                             :@@@%#*##**##************+====*----------*-------------=+*+++*+=++=*-
                                 :*%@@%#****#####**********#*******************++*++++++++*=+=-.
                                      .+#@%%%%##*+*********#************+++++++++*+++++++*-
                                             :+%%%%%%%%###*#************+++********#%%:
                                                     .:-=+##%%%%%%%%%%%%%%#***=:.
 */

/**
 * Class to generate sphere related shapes
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
public class SphereGen extends AbstractFillableBlockShape {
    public static final Codec<SphereGen> CODEC = RecordCodecBuilder.create((instance) ->
            instance.group(
                    BlockPos.CODEC.fieldOf("center").forGetter(shape -> LongPosHelper.decodeBlockPos(shape.centerPos)),
                    Rotator.CODEC.fieldOf("rotator").forGetter(shape -> shape.rotator),
                    Codec.INT.fieldOf("radius_x").forGetter(shape -> shape.radiusX),
                    Codec.INT.fieldOf("radius_y").forGetter(shape -> shape.radiusY),
                    Codec.INT.fieldOf("radius_z").forGetter(shape -> shape.radiusZ),
                    Codec.FLOAT.fieldOf("filling").forGetter(shape -> shape.customFill),
                    FillingType.CODEC.fieldOf("filling_type").forGetter(shape -> shape.fillingType)
            ).apply(instance, SphereGen::new)
    );

    private int radiusX;
    private int radiusY;
    private int radiusZ;


    private SphereType halfSphere = SphereType.DEFAULT;

    private Direction direction = Direction.UP;


    //Used for performance test
    private long startTime;


    /**
     * init the Sphere Shape
     *
     * @param pos        the center of the spiral
     * @param radiusX    the radius on the x-axis
     * @param radiusY    the radius on the y-axis
     * @param radiusZ    the radius on the z-axis
     * @param halfSphere determines if the sphere is half or not
     */
    public SphereGen(@NotNull BlockPos pos, Rotator rotator, int radiusX, int radiusY, int radiusZ, SphereType halfSphere) {
        super(pos, rotator);
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
        this.halfSphere = halfSphere;
    }

    /**
     * init the shape generation
     *
     * @param pos    the pos of the structure center
     * @param radius the radius of the sphere
     */
    public SphereGen(@NotNull BlockPos pos, int radius) {
        super(pos);
        this.radiusX = radius;
        this.radiusY = radius;
        this.radiusZ = radius;
    }

    public SphereGen(BlockPos pos, Rotator rotator, int radiusX, int radiusY, int radiusZ, float customFill, FillingType fillingType) {
        super(pos, rotator);
        this.customFill = customFill;
        this.fillingType = fillingType;
        this.radiusX = radiusX;
        this.radiusY = radiusY;
        this.radiusZ = radiusZ;
    }

    /**
     * Sets the direction of the half-sphere. * * @param direction The direction to set.
     */
    public void setHalfSphereDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Sets the half-sphere type. * * @param halfSphere The half-sphere type to set.
     */
    public void setHalfSphere(SphereType halfSphere) {
        this.halfSphere = halfSphere;
    }

    /*---------- Radius Related ----------*/

    /**
     * Sets the X radius of the sphere. * * @param radiusX The X radius to set.
     */
    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    /**
     * Sets the Y radius of the sphere. * * @param radiusY The Y radius to set.
     */
    public void setRadiusY(int radiusY) {
        this.radiusY = radiusY;
    }

    /**
     * Sets the Z radius of the sphere. * * @param radiusZ The Z radius to set.
     */
    public void setRadiusZ(int radiusZ) {
        this.radiusZ = radiusZ;
    }

    @Override
    public Map<ChunkPos, LongOpenHashSet> getShapeCoordinates() {
        //verify if the rotations == 0 to avoid some unnecessary calculations
        if (this.fillingType == FillingType.EMPTY) {
            if (this.halfSphere == SphereType.HALF) {
                this.generateHalfEmptyEllipsoid();
            } else {
                this.generateEmptyEllipsoid();
            }
        } else {
            if (this.halfSphere == SphereType.HALF) {
                this.generateHalfFullEllipsoid();
            } else {
                this.generateFullEllipsoid();
            }
        }
        return chunkMap;
    }

    @Override
    public LongOpenHashSet getCoveredChunks() {
        int estimatedSurface;

        if (halfSphere == SphereType.HALF && DirectionUtil.isHorizontal(direction)) {
            estimatedSurface = (int) (Math.PI * radiusZ * radiusX / 2);
        } else {
            estimatedSurface = (int) (Math.PI * radiusZ * radiusX);
        }
        covered = new LongOpenHashSet(estimatedSurface);

        int minTheta = -180, minPhi = -90;
        int maxTheta = 180, maxPhi = 90;

        switch (direction) {
            case UP:
                minPhi = 0;
                break;
            case DOWN:
                maxPhi = 0;
                break;
            case WEST:
                minTheta = 0;
                break;
            case EAST:
                maxTheta = 0;
                break;
            case NORTH:
                minTheta = -90;
                maxTheta = 90;
                break;
            case SOUTH:
                minTheta = 90;
                maxTheta = 270;
                break;
        }

        getCovered(minTheta, maxTheta, minPhi, maxPhi);
        return covered;
    }


    private void generateHalfEmptyEllipsoid() {
        int minTheta = -180, minPhi = -90;
        int maxTheta = 180, maxPhi = 90;

        switch (direction) {
            case UP:
                minPhi = 0;
                break;
            case DOWN:
                maxPhi = 0;
                break;
            case WEST:
                minTheta = 0;
                break;
            case EAST:
                maxTheta = 0;
                break;
            case NORTH:
                minTheta = -90;
                maxTheta = 90;
                break;
            case SOUTH:
                minTheta = 90;
                maxTheta = 270;
                break;
        }
        generateEmptyEllipsoid(minTheta, maxTheta, minPhi, maxPhi);
    }

    @Override
    public void place(StructureWorldAccess world, BlockLayerManager blockLayerManager) {

    }

    private void generateEmptyEllipsoid() {
        this.generateEmptyEllipsoid(-180, 180, -90, 90);
    }

    private void generateHalfFullEllipsoid() {
        int minX = -radiusX, minY = -radiusY, minZ = -radiusZ;
        int maxX = radiusX, maxY = radiusY, maxZ = radiusZ;
        switch (direction) {
            case UP:
                minY = 0;
                break;
            case DOWN:
                maxY = 0;
                break;
            case WEST:
                minX = 0;
                break;
            case EAST:
                maxX = 0;
                break;
            case NORTH:
                minZ = 0;
                break;
            case SOUTH:
                maxZ = 0;
                break;
        }
        this.generateFullEllipsoid(minX, maxX, minY, maxY, minZ, maxZ);
    }

    public void generateEmptyEllipsoid(int minLarge, int maxLarge, int minHeight, int maxHeight) {
        int maxLarge1 = Math.max(radiusZ, Math.max(radiusX, radiusY));
        if (rotator == null) {
            for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {

                double xCosTheta = radiusX * FastMaths.getFastCos(theta);
                double zSinTheta = radiusZ * FastMaths.getFastSin(theta);


                for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                    double cosPhi = FastMaths.getFastCos(phi);
                    int x = (int) (xCosTheta * cosPhi);
                    int y = (int) (radiusY * FastMaths.getFastSin(phi));
                    int z = (int) (zSinTheta * cosPhi);
                    modifyChunkMap(LongPosHelper.encodeBlockPos(x + centerX, y + centerY, z + centerZ));
                }
            }
        } else {
            for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {

                float xCosTheta = radiusX * FastMaths.getFastCos(theta);
                float zSinTheta = radiusZ * FastMaths.getFastSin(theta);

                for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                    float cosPhi = FastMaths.getFastCos(phi);

                    float x = xCosTheta * cosPhi;
                    float y = (radiusY * FastMaths.getFastSin(phi));
                    float z = zSinTheta * cosPhi;
                    modifyChunkMap(rotator.get(x, y, z));
                }
            }
        }
    }


    public void generateFullEllipsoid() {
        this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, -radiusZ, radiusZ);
    }

    //Using cartesian coordinates because it has better performance than using trigonometry

    /**
     * allow you to generate a full ellipsoid
     *
     * @param minX the start of the circle on the x-axis
     * @param maxX the end of the circle on the x-axis
     * @param minY the start of the circle on the y-axis
     * @param maxY the end of the circle on the y-axis
     * @param minZ the start of the circle on the z-axis
     * @param maxZ the end of the circle on the z-axis
     */
    public void generateFullEllipsoid(int minX, int maxX, int minY, int maxY, int minZ, int maxZ) {
        this.setFill();
        int largeXSquared = radiusX * radiusX;
        int largeYSquared = radiusY * radiusY;
        int largeZSquared = radiusZ * radiusZ;

        float innerRadiusXSquared = (1 - this.customFill) * (1 - this.customFill) * largeXSquared;
        float innerRadiusYSquared = (1 - this.customFill) * (1 - this.customFill) * largeYSquared;
        float innerRadiusZSquared = (1 - this.customFill) * (1 - this.customFill) * largeZSquared;


        if (radiusX > 32 || radiusY > 32 || radiusZ > 32) {
            Ewc.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        if (this.rotator == null) {
            for (float x = minX; x <= maxX; x++) {
                float xx = x * x;
                float xs = xx / largeXSquared;

                for (float y = minY; y <= maxY; y++) {
                    float yy = y * y;
                    float ys = yy / largeYSquared + xs;

                    for (float z = minZ; z <= maxZ; z++) {
                        float zz = z * z;
                        if (ys + (zz) / (largeZSquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) {
                                    bl = false;
                                }
                            }
                            if (bl) {
                                modifyChunkMap(LongPosHelper.encodeBlockPos((int) (this.centerX + x), (int) (this.centerY + y), (int) (this.centerZ + z)));
                            }
                        }
                    }
                }
            }
        } else {
            for (float x = minX; x <= maxX; x += 0.5f) {
                float xx = x * x;
                float xs = xx / largeXSquared;

                for (float y = minY; y <= maxY; y += 0.5f) {
                    float yy = y * y;
                    float ys = yy / largeYSquared + xs;

                    for (float z = minZ; z <= maxZ; z += 0.5f) {
                        float zz = z * z;
                        if (ys + (zz) / (largeZSquared) <= 1) {
                            boolean bl = true;
                            if (innerRadiusXSquared != 0) {
                                float innerXSquared = xx / innerRadiusXSquared;
                                float innerYSquared = yy / innerRadiusYSquared;
                                float innerZSquared = zz / innerRadiusZSquared;
                                if (innerXSquared + innerZSquared + innerYSquared <= 1f) {
                                    bl = false;
                                }
                            }
                            if (bl) {
                                modifyChunkMap(rotator.get(x, y, z));
                            }
                        }
                    }
                }
            }
        }
    }

    private void getCovered(int minLarge, int maxLarge, int minHeight, int maxHeight) {
        int maxLarge1 = Math.max(radiusZ, Math.max(radiusX, radiusY));
        if (rotator == null) {
            int largeXSquared = radiusX * radiusX;
            int largeZSquared = radiusZ * radiusZ;

            int xBound = (360 - maxLarge + minLarge) / 360 * radiusX;
            int zBound = (360 - maxLarge + minLarge) / 360 * radiusZ;

            for (int x = (180 - maxLarge + minLarge) / 180 * radiusX; x < xBound; x++) {
                int chunkX = (x + centerX) >> 4;
                boolean different = chunkX != lastChunkX;

                float x2 = (float) (x * x) / largeXSquared;

                for (int z = (180 - maxLarge + minLarge) / 180 * radiusZ; z < zBound; z++) {
                    if (x2 + (float) (z * z) / largeZSquared <= 1f) {
                        shouldAddChunkPrecomputedX(z + centerZ, different, chunkX);
                    }
                }
            }
        } else {
            for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {

                float xCosTheta = radiusX * FastMaths.getFastCos(theta);
                float zSinTheta = radiusZ * FastMaths.getFastSin(theta);

                for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                    float cosPhi = FastMaths.getFastCos(phi);

                    float x = xCosTheta * cosPhi;
                    float y = (radiusY * FastMaths.getFastSin(phi));
                    float z = zSinTheta * cosPhi;
                    BlockPos pos = rotator.getBlockPos(x, y, z);
                    shouldAddChunk(pos.getX(), pos.getZ());
                }
            }
        }
    }

    /**
     * enum to define the type of the sphere
     */
    public enum SphereType {
        /**
         * the sphere will be cut in half
         */
        HALF,
        /**
         * default sphere
         */
        DEFAULT
    }


}
