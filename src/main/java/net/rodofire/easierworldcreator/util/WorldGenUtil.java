package net.rodofire.easierworldcreator.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayer;
import net.rodofire.easierworldcreator.maths.FastMaths;
import net.rodofire.easierworldcreator.maths.MathUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("unused")
public class WorldGenUtil {

    /**
     * method to get a random direction no matter the plane
     *
     * @return a random direction
     */
    public static Direction getRandomDirection() {
        return switch (Random.create().nextBetween(0, 5)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    /**
     * method to get a random direction on the vertical axis
     *
     * @return a random direction on the vertical axis
     */
    public static Direction getRandomVerticalDirection() {
        return Random.create().nextBetween(0, 1) == 1 ? Direction.UP : Direction.DOWN;
    }

    /**
     * method to get a random direction on the horizontal axis
     *
     * @return a random direction on the horizontal axis
     */
    public static Direction getRandomHorizontalDirection() {
        return switch (Random.create().nextBetween(0, 3)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    /**
     * method to get a random direction no matter the plane
     *
     * @return a random direction
     */
    public static Direction getRandomDirection(Random random) {
        return switch (random.nextBetween(0, 5)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            case 3 -> Direction.SOUTH;
            case 4 -> Direction.UP;
            default -> Direction.DOWN;
        };
    }

    /**
     * method to get a random direction on the vertical axis
     *
     * @return a random direction on the vertical axis
     */
    public static Direction getRandomVerticalDirection(Random random) {
        return random.nextBetween(0, 1) == 1 ? Direction.UP : Direction.DOWN;
    }

    /**
     * method to get a random direction on the horizontal axis
     *
     * @return a random direction on the horizontal axis
     */
    public static Direction getRandomHorizontalDirection(Random random) {
        return switch (random.nextBetween(0, 3)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }

    /**
     * return a random int between min height and max height if the chance
     *
     * @param chance    the chance at which the result won't be equal to 0
     * @param maxHeight the maximum height that can be returned
     * @return a random height
     */
    public static int getSecondHeight(float chance, int maxHeight) {
        return getSecondHeight(chance, 0, maxHeight);
    }

    /**
     * return a random int between min height and max height if the chance
     *
     * @param chance    the chance at which the result won't be equal to 0
     * @param minHeight the minimum height that can be returned in the case the chance allowed a random height
     * @param maxHeight the maximum height that can be returned
     * @return a random height
     */
    public static int getSecondHeight(float chance, int minHeight, int maxHeight) {
        if (Random.create().nextFloat() < chance) {
            return Random.create().nextBetween(minHeight, maxHeight);
        }
        return 0;
    }


    //verify if a block is in a list of BlockState
    public static boolean isBlockInBlockStateList(Block block, List<BlockState> state) {
        for (BlockState blockState : state) {
            if (blockState.getBlock().equals(block)) {
                return true;
            }
        }
        return false;
    }

    //Verify if a BlockState is in a list of BlockStates
    public static boolean isBlockStateInBlockStateList(BlockState block, List<BlockState> state) {
        for (BlockState blockState : state) {
            if (blockState == block) {
                return true;
            }
        }
        return false;
    }

    public static double getAbs(Vec3d pos) {
        return MathUtil.absDistance(pos.x, pos.y, pos.z);
    }

    public static double getSquared(Vec3d pos) {
        return MathUtil.squared(pos.x, pos.y, pos.z);
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2) {
        return FastMaths.getLength(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ());
    }

    public static float getDistance(int x1, int y1, int z1, int x2, int y2, int z2) {
        return FastMaths.getLength(x1 - x2, y1 - y2, z1 - z2);
    }

    public static float getDistance(int x1, int y1, int z1, int[] pos2) {
        return FastMaths.getLength(x1 - pos2[0], y1 - pos2[1], z1 - pos2[2]);
    }

    public static float getDistance(int[] pos1, int[] pos2) {
        return FastMaths.getLength(pos1[0] - pos2[0], pos1[1] - pos2[1], pos1[2] - pos2[2]);
    }

    public static float getDistance(BlockPos pos1, BlockPos pos2, float precision) {
        return FastMaths.getLengthWPrecision(pos1.getX() - pos2.getX(), pos1.getY() - pos2.getY(), pos1.getZ() - pos2.getZ(), precision);
    }

    public static float getDistance(Vec3d pos1, Vec3d pos2) {
        return FastMaths.getLength((float) (pos1.getX() - pos2.getX()), (float) (pos1.getY() - pos2.getY()), (float) (pos1.getZ() - pos2.getZ()));
    }

    public static float getDistance(Vec3d pos1, Vec3d pos2, float precision) {
        return FastMaths.getLengthWPrecision((float) (pos1.getX() - pos2.getX()), (float) (pos1.getY() - pos2.getY()), (float) (pos1.getZ() - pos2.getZ()), precision);
    }

    public static boolean isPosAChunkFar(BlockPos pos1, BlockPos pos2) {
        if (Math.abs(pos1.getX() - pos2.getX()) > 16) return true;
        return Math.abs(pos1.getZ() - pos2.getZ()) > 16;
    }


    public static Set<Block> addBlockStateListToBlockList(Set<Block> block, List<BlockState> state) {
        List<Block> secondlist = new ArrayList<>();
        if (block == null) block = new HashSet<>();
        for (BlockState blockState : state) {
            secondlist.add(blockState.getBlock());
        }
        block.addAll(secondlist);
        return block;
    }

    public static double getDistanceFromPointToPlane(Vec3d normal, Vec3d pointOnPlane, Vec3d point) {
        double A = normal.x;
        double B = normal.y;
        double C = normal.z;

        double D = -(A * pointOnPlane.x + B * pointOnPlane.y + C * pointOnPlane.z);

        double x0 = point.x;
        double y0 = point.y;
        double z0 = point.z;

        double numerator = Math.abs(A * x0 + B * y0 + C * z0 + D);
        double denominator = Math.sqrt(A * A + B * B + C * C);

        return (numerator / denominator);
    }

    public static int getTotalBlockLayerDepth(List<BlockLayer> layers) {
        int i = 0;
        for (BlockLayer blockLayer : layers) {
            i += blockLayer.getDepth();
        }
        return i;
    }

    public static int getBlockLayerDepth(List<BlockLayer> layers, int index) {
        int i = 0;
        if (index >= layers.size()) {
            Ewc.LOGGER.error("int index >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + layers.size());
        }
        for (int a = 0; a <= index; a++) {
            i += layers.get(a).getDepth();
        }
        return i;
    }

    public static int getBlockLayerDepth(List<BlockLayer> layers, int startIndex, int endIndex) {
        int i = 0;
        if (startIndex >= layers.size()) {
            Ewc.LOGGER.error("int startIndex >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + startIndex + ", Size: " + layers.size());
        }
        if (endIndex >= layers.size()) {
            Ewc.LOGGER.error("int endIndex >= blockLayer size");
            throw new IndexOutOfBoundsException("Index: " + endIndex + ", Size: " + layers.size());
        }
        if (endIndex < startIndex) {
            Ewc.LOGGER.error("int firstIndex > endIndex");
            return 0;
        }
        for (int a = startIndex; a <= endIndex; a++) {
            i += layers.get(a).getDepth();
        }
        return i;
    }

    public static Direction getDirection(BlockPos pos1, BlockPos pos2) {
        if (pos1.getX() == pos2.getX() && pos1.getY() == pos2.getY()) {
            return pos2.getZ() - pos1.getZ() < 0 ? Direction.WEST : Direction.EAST;
        }

        if (pos1.getX() == pos2.getX() && pos1.getZ() == pos2.getZ()) {
            return pos2.getY() - pos1.getY() < 0 ? Direction.DOWN : Direction.UP;
        }

        if (pos1.getY() == pos2.getY() && pos1.getZ() == pos2.getZ()) {
            return pos2.getX() - pos1.getX() < 0 ? Direction.SOUTH : Direction.NORTH;
        }
        return null;
    }

    public static ChunkPos addChunkPos(ChunkPos pos1, ChunkPos pos2) {
        return new ChunkPos(pos1.x + pos2.x, pos1.z + pos2.z);
    }

    public static ChunkPos addChunkPos(ChunkPos pos1, int x, int z) {
        return new ChunkPos(pos1.x + x, pos1.z + z);
    }

    public static ChunkPos addChunkPos(ChunkPos pos1, BlockPos pos2) {
        ChunkPos pos = new ChunkPos(pos2);
        return new ChunkPos(pos1.x + pos.x, pos1.z + pos.z);
    }


    public static float getDistanceToAxis(Vec3d centerPos, Vec3d axisDir, Vec3d pos) {
        Vec3d v = pos.subtract(centerPos);
        Vec3d cross = v.crossProduct(axisDir);
        double crossNorm = cross.length();
        double dirNorm = axisDir.length();

        return (float) (crossNorm / dirNorm);
    }

    public static double getExactDistance(Vec3d pos) {
        return MathUtil.getExactDistance(pos.x, pos.y, pos.z);
    }
}
