package fr.rodofire.ewc.shape.block.simplegen;


import fr.rodofire.ewc.shape.block.gen.CircleGen;
import fr.rodofire.ewc.util.BlockPlaceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Set;

/*



                                             .:::::::..
                                         .::............::.
                                       ::..................::
                                     .:......................:.
                                    :..........................:
                                   -............................:
                                   ..............................
                                  -..............................:
                                  =..............................-
                                  =..............................-
                                  -..............................:
                                   .............................:
                                   -............................:
                                    :..........................:
                                     .:......................:.
                                       ::..................::
                                          ::............::
                                              .::::::.
 */
/**
 * switch to new generation :
 * @see CircleGen
 * this class will not be updated anymore and won't receive any support
 */
@Deprecated
@SuppressWarnings("unused")
public class GenCircles {
    //Validate
    public static void generateCircle(WorldGenLevel world, int radius, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, false, null, List.of(state));
    }

    public static void generateFullCircle(WorldGenLevel world, int radius, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, true, null, List.of(state));
    }

    public static void generateFullCircle(WorldGenLevel world, int radius, BlockState state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, null, List.of(state));
    }

    public static void generateFullCircle(WorldGenLevel world, int radius, BlockPos pos, boolean force, Set<Block> stateToForce, List<BlockState> stateToPlace) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, stateToForce, stateToPlace);
    }

    public static void generateCircle(WorldGenLevel world, int radius, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, false, null, state);
    }

    public static void generateFullCircle(WorldGenLevel world, int radius, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, false, true, null, state);
    }

    public static void generateFullCircle(WorldGenLevel world, int radius, List<BlockState> state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radius, radius, x, z, y, force, true, null, state);
    }


    public static void generateEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, false, false, null, List.of(state));
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, BlockState state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, false, true, null, List.of(state));
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, BlockState state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, force, true, null, List.of(state));
    }

    public static void generateEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, false, false, null, state);
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, List<BlockState> state, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, false, true, null, state);
    }

    public static void generateFullEllipsoid(WorldGenLevel world, int radiusX, int radiusZ, List<BlockState> state, BlockPos pos, boolean force) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        generateOval(world, radiusX, radiusZ, x, z, y, force, true, null, state);
    }


    /*---------- Algorithm based on Bressen Algorithms for circle ----------*/
    public static void generateOval(WorldGenLevel world, int radiusX, int radiusZ, int centerX, int centerZ, int y, boolean force, boolean full, Set<Block> blockToForce, List<BlockState> blockToPlace) {
        int x = 0;
        int z = radiusZ;
        int twoASquare = 2 * radiusX * radiusX;
        int twoBSquare = 2 * radiusZ * radiusZ;
        int decision1 = (int) (radiusZ * radiusZ - radiusX * radiusX * radiusZ + 0.25 * radiusX * radiusX);
        int dx = twoBSquare * x;
        int dz = twoASquare * z;

        // Region 1
        while (dx < dz) {
            if (!full) {
                placeOvalBlocks(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            } else {
                placeFullOval(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            }
            x++;
            if (decision1 < 0) {
                dx = dx + twoBSquare;
                decision1 = decision1 + dx + radiusZ * radiusZ;
            } else {
                z--;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision1 = decision1 + dx - dz + radiusZ * radiusZ;
            }
        }

        // Region 2
        int decision2 = (int) (radiusZ * radiusZ * (x + 0.5) * (x + 0.5) + radiusX * radiusX * (z - 1) * (z - 1) - radiusX * radiusX * radiusZ * radiusZ);
        while (z >= 0) {
            if (!full) {
                placeOvalBlocks(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            } else {
                placeFullOval(world, centerX, centerZ, x, y, z, force, blockToForce, blockToPlace);
            }
            z--;
            if (decision2 > 0) {
                dz = dz - twoASquare;
                decision2 = decision2 + radiusX * radiusX - dz;
            } else {
                x++;
                dx = dx + twoBSquare;
                dz = dz - twoASquare;
                decision2 = decision2 + dx - dz + radiusX * radiusX;
            }
        }
    }

    public static void placeOvalBlocks(WorldGenLevel world, int centerX, int centerZ, int x, int y, int z, boolean force, Set<Block> blocksToForce, List<BlockState> blockToPlace) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int length = blockToPlace.size() - 1;
        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blockToPlace, pos.set(centerX + x, 0, centerZ + z));
        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blockToPlace, pos.set(centerX + x, 0, centerZ - z));
        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blockToPlace, pos.set(centerX - x, 0, centerZ + z));
        BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blockToPlace, pos.set(centerX - x, 0, centerZ - z));

    }

    //place lines between the blocks
    public static void placeFullOval(WorldGenLevel world, int centerX, int centerZ, int x, int y, int z, boolean force, Set<Block> blocksToForce, List<BlockState> blocksToPlace) {
        BlockPos start1 = new BlockPos(centerX + x, y, centerZ + z);
        BlockPos start2 = new BlockPos(centerX - x, y, centerZ + z);

        BlockPos end1 = new BlockPos(centerX + x, y, centerZ - z);
        BlockPos end2 = new BlockPos(centerX - x, y, centerZ - z);

        int length = blocksToPlace.size() - 1;

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (int i = 0; i <= 2 * z; i++) {
            mutable.setWithOffset(start1, 0, 0, -i);
            BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);
            mutable.setWithOffset(start2, 0, 0, -i);
            BlockPlaceUtil.setRandomBlockWithVerification(world, force, blocksToForce, blocksToPlace, mutable);

        }
    }
}
