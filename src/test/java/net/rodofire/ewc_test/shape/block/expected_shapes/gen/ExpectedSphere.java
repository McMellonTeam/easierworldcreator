package net.rodofire.ewc_test.shape.block.expected_shapes.gen;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.easierworldcreator.util.LongPosHelper;
import net.rodofire.ewc_test.shape.block.expected_shapes.instanciator.ExpectedAbstractFillableBlockShape;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class ExpectedSphere extends ExpectedAbstractFillableBlockShape {
    private int radiusX;
    private int radiusY;
    private int radiusZ;

    @Override
    public LongOpenHashSet getCoveredChunks() {
        return new LongOpenHashSet();
    }

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
    public ExpectedSphere(@NotNull BlockPos pos, Rotator rotator, int radiusX, int radiusY, int radiusZ, SphereType halfSphere) {
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
    public ExpectedSphere(@NotNull BlockPos pos, int radius) {
        super(pos);
        this.radiusX = radius;
        this.radiusY = radius;
        this.radiusZ = radius;
    }

    /**
     * Gets the direction of the half-sphere. * * @return The direction of the half-sphere.
     */
    public Direction getHalfSphereDirection() {
        return direction;
    }

    /**
     * Sets the direction of the half-sphere. * * @param direction The direction to set.
     */
    public void setHalfSphereDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Checks if it is a half sphere. * * @return The type of the half-sphere.
     */
    public SphereType isHalfSphere() {
        return halfSphere;
    }

    /**
     * Sets the half-sphere type. * * @param halfSphere The half-sphere type to set.
     */
    public void setHalfSphere(SphereType halfSphere) {
        this.halfSphere = halfSphere;
    } /*---------- Radius Related ----------*/

    /**
     * Gets the X radius of the sphere. * * @return The X radius.
     */
    public int getRadiusX() {
        return radiusX;
    }

    /**
     * Sets the X radius of the sphere. * * @param radiusX The X radius to set.
     */
    public void setRadiusX(int radiusX) {
        this.radiusX = radiusX;
    }

    /**
     * Gets the Y radius of the sphere. * * @return The Y radius.
     */
    public int getRadiusY() {
        return radiusY;
    }

    /**
     * Sets the Y radius of the sphere. * * @param radiusY The Y radius to set.
     */
    public void setRadiusY(int radiusY) {
        this.radiusY = radiusY;
    }

    /**
     * Gets the Z radius of the sphere. * * @return The Z radius.
     */
    public int getRadiusZ() {
        return radiusZ;
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
        if (this.getFillingType() == Type.EMPTY) {
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


    public void generateHalfEmptyEllipsoid() {
        if (direction == Direction.UP) {
            generateEmptyEllipsoid(-180, 180, 0, 90);
        } else if (direction == Direction.DOWN) {
            generateEmptyEllipsoid(-180, 180, -90, 0);
        } else if (direction == Direction.WEST) {
            generateEmptyEllipsoid(0, 180, -90, 90);
        } else if (direction == Direction.EAST) {
            generateEmptyEllipsoid(-180, 0, -90, 90);
        } else if (direction == Direction.NORTH) {
            generateEmptyEllipsoid(-90, 90, -90, 90);
        } else {
            generateEmptyEllipsoid(90, 270, -90, 90);
        }
    }

    public void generateEmptyEllipsoid() {
        this.generateEmptyEllipsoid(-180, 180, -90, 90);
    }

    public void generateEmptyEllipsoid(int minLarge, int maxLarge, int minHeight, int maxHeight) {
        int maxLarge1 = Math.max(radiusZ, Math.max(radiusX, radiusY));

        BlockPos.Mutable mutable = new BlockPos.Mutable();
        List<BlockPos> poslist = new ArrayList<>();
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


    public void generateHalfFullEllipsoid() {
        if (direction == Direction.UP) {
            this.generateFullEllipsoid(-radiusX, radiusX, 0, radiusY, -radiusZ, radiusZ);
        } else if (direction == Direction.DOWN) {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, 0, -radiusZ, radiusZ);
        } else if (direction == Direction.WEST) {
            this.generateFullEllipsoid(0, radiusX, -radiusY, radiusY, -radiusZ, radiusZ);
        } else if (direction == Direction.EAST) {
            this.generateFullEllipsoid(-radiusX, 0, -radiusY, radiusY, -radiusZ, radiusZ);
        } else if (direction == Direction.NORTH) {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, -radiusZ, 0);
        } else {
            this.generateFullEllipsoid(-radiusX, radiusX, -radiusY, radiusY, 0, radiusZ);
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

        float innerRadiusXSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeXSquared;
        float innerRadiusYSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeYSquared;
        float innerRadiusZSquared = (1 - this.getCustomFill()) * (1 - this.getCustomFill()) * largeZSquared;


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
