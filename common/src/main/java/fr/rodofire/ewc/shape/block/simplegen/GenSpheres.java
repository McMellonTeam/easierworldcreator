package fr.rodofire.ewc.shape.block.simplegen;


import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.maths.FastMaths;
import fr.rodofire.ewc.shape.block.gen.SphereGen;
import fr.rodofire.ewc.util.BlockPlaceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
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
 * switch to new generation
 * @see SphereGen
 * this class will not be updated anymore and won't receive any support
 */
@Deprecated
@SuppressWarnings("unused")
public class GenSpheres {
    public static void generateHalfFullSphere(WorldGenLevel world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullEllipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfFullSphere(WorldGenLevel world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfFullEllipsoid(world, radius, radius, radius, pos, direction, state);
    }


    public static void generateHalfFullEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, Direction direction, BlockState state) {
        generateHalfFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, direction, List.of(state));
    }

    public static void generateHalfFullEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, radiusX, 0, radiusY, -radiusZ, radiusZ, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, radiusX, -radiusY, 0, -radiusZ, radiusZ, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, 0, radiusX, -radiusY, radiusY, -radiusZ, radiusZ, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, 0, -radiusY, radiusY, -radiusZ, radiusZ, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, radiusX, -radiusY, radiusY, -radiusZ, 0, null, state);
            return;
        }
        generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, radiusX, -radiusY, radiusY, 0, radiusZ, null, state);
    }


    //Using cartesian coordinates, because, it has better performance than using trigonometry

    /**
     * allow you to generate a full Ellipsoid
     *
     * @param world         the world it will spawn in
     * @param radiusX        the x-axis radius
     * @param radiusY        the y-aris radius
     * @param radiusZ        the z-axis radius
     * @param pos           the center of the ellipsoid
     * @param force         force the putting of the blocks
     * @param minx          the start of the circle on the x-axis
     * @param maxX          the end of the circle on the x-axis
     * @param miny          the start of the circle on the y-axis
     * @param maxy          the end of the circle on the y-axis
     * @param minZ          the start of the circle on the z axis
     * @param maxZ          the end of the circle on the z axis
     * @param blocksToForce list of blocks that the structure can still force if force = true
     * @param blocksToPlace list of blockState that will be placed randomly (if only one blockState is given, the block will be the only block put
     */
    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, boolean force, int minx, int maxX, int miny, int maxy, int minZ, int maxZ, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int length = blocksToPlace.size() - 1;
        int radiusXSquared = radiusX * radiusX;
        int radiusYSquared = radiusY * radiusY;
        int radiusZSquared = radiusZ * radiusZ;
        if (radiusX > 32 || radiusY > 32 || radiusZ > 32) {
            EwcConstants.LOGGER.warn("generating huge sphere (diameter > 64)");
        }
        for (float x = minx; x <= maxX; x++) {
            float xs = x*x/radiusXSquared;
            for (float y = miny; y <= maxy; y++) {
                float ys = y*y/radiusYSquared + xs;
                for (float z = minZ; z <= maxZ; z++) {
                    if (ys + (z * z) / (radiusZSquared) <= 1) {
                        mutable.setWithOffset(pos, (int) x, (int) y, (int) z);
                        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
                    }
                }
            }
        }
    }

    public static void generateSphere(WorldGenLevel world, int radius, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, radius, radius, radius, pos, false, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateSphere(WorldGenLevel world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, radius, radius, radius, pos, force, -radius, radius, -radius, radius, -radius, radius, null, List.of(state));
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, BlockState state) {
        generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -radiusX, radiusX, -radiusY, radiusY, -radiusZ, radiusZ, null, List.of(state));
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, BlockState state, boolean force) {
        generateFullEllipsoid(world, radiusX, radiusY, radiusZ, pos, force, -radiusX, radiusX, -radiusY, radiusY, -radiusZ, radiusZ, null, List.of(state));
    }


    //better performance when generating an empty sphere

    /**
     * generate an empty Ellipsoid
     *
     * @param world         the world the structure will spawn in
     * @param radiusX        the x radius
     * @param radiusY        the y radius
     * @param radiusZ        the z radius
     * @param pos           the center of the Ellipsoid
     * @param force         force the spawn of the structure by replacing already existing blocks
     * @param minLarge      the start of the horizontal radius (for a full Ellipsoid put 0-360 or -180 - 180)
     * @param maxLarge      the end of the horizontal radius
     * @param minHeight     the start of the vertical radius (for a full Ellipsoid put -90 to 90
     * @param maxHeight     the end of the vertical radius
     * @param blocksToForce the list of blocks that the structure can still force if force = true
     * @param blocksToPlace the list of blockState that the structure will pose in a random order
     */
    public static void generateEmptyEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, boolean force, int minLarge, int maxLarge, int minHeight, int maxHeight, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
        int maxLarge1 = Math.max(radiusZ, Math.max(radiusX, radiusY));
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int length = blocksToPlace.size() - 1;
        for (float theta = minLarge; theta <= maxLarge; theta += (float) 45 / maxLarge1) {
            float xCosTheta = radiusX * FastMaths.getFastCos(theta);
            float zSinTheta = radiusZ * FastMaths.getFastSin(theta);
            for (float phi = minHeight; phi <= maxHeight; phi += (float) 45 / maxLarge1) {
                float cosPhi = FastMaths.getFastCos(phi);
                int x = (int) (xCosTheta * cosPhi);
                int y = (int) (radiusY * FastMaths.getFastSin(phi));
                int z = (int) (zSinTheta * cosPhi);
                mutable.setWithOffset(pos, x, y, z);

                BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
            }
        }
    }

    public static void generateHalfEmptySphere(WorldGenLevel world, int radius, BlockPos pos, Direction direction, List<BlockState> state) {
        generateHalfEmptyEllipsoid(world, radius, radius, radius, pos, direction, state);
    }

    public static void generateHalfEmptySphere(WorldGenLevel world, int radius, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyEllipsoid(world, radius, radius, radius, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, Direction direction, BlockState state) {
        generateHalfEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, direction, List.of(state));
    }

    public static void generateHalfEmptyEllipsoid(WorldGenLevel world, int radiusX, int radiusY, int radiusZ, BlockPos pos, Direction direction, List<BlockState> state) {
        if (direction == Direction.UP) {
            generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, 180, 180, 0, 90, null, state);
            return;
        }
        if (direction == Direction.DOWN) {
            generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, 180, 180, -90, 0, null, state);
            return;
        }
        if (direction == Direction.WEST) {
            generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, 0, 180, -90, 90, null, state);
            return;
        }
        if (direction == Direction.EAST) {
            generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -180, 0, -90, 90, null, state);
            return;
        }
        if (direction == Direction.NORTH) {
            generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, -90, 90, -90, 90, null, state);
            return;
        }
        generateEmptyEllipsoid(world, radiusX, radiusY, radiusZ, pos, false, 90, 270, -90, 90, null, state);
    }

    public static void generateEmptySphere(WorldGenLevel world, int radius, BlockPos pos, BlockState state) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, false, -180, 180, -90, 90, null, List.of(state));
    }

    public static void generateEmptySphere(WorldGenLevel world, int radius, BlockPos pos, BlockState state, boolean force) {
        generateEmptyEllipsoid(world, radius, radius, radius, pos, force, -180, 180, -90, 90, null, List.of(state));
    }


}
