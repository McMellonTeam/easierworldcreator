package fr.rodofire.ewc.util;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Utility class for encoding and decoding {@link BlockPos} into a single long value.
 * <p>
 * This encoding scheme uses:
 * <ul>
 *     <li>26 bits for the x-coordinate</li>
 *     <li>26 bits for the z-coordinate</li>
 *     <li>12 bits for the y-coordinate</li>
 * </ul>
 * The encoding preserves the sign of the coordinates.
 *
 * <p> Using a long representation reduces memory usage and improves performance,
 * especially when working with {@link LongArrayList}.
 */
public class LongPosHelper {
    /**
     * Number of bits allocated for x and z coordinates.
     */
    public static final int XZ_BITS = 26;
    /**
     * Number of bits allocated for y coordinate.
     */
    public static final int Y_BITS = 12;
    /**
     * Bitmask for x and z coordinates (26 bits).
     */
    public static final long XZ_MASK = (1L << XZ_BITS) - 1;
    /**
     * Bitmask for y coordinate (12 bits).
     */
    public static final long Y_MASK = (1L << Y_BITS) - 1;
    /**
     * Maximum value for signed x and z before overflow.
     */
    public static final int XZ_MAX = (1 << (XZ_BITS - 1)) - 1;
    /**
     * Maximum value for signed y before overflow.
     */
    public static final int Y_MAX = (1 << (Y_BITS - 1)) - 1;

    /**
     * Encodes the given coordinates into a single long value.
     *
     * @param x The x-coordinate (-33,554,432 to 33,554,431)
     * @param y The y-coordinate (-2,048 to 2,047)
     * @param z The z-coordinate (-33,554,432 to 33,554,431)
     * @return Encoded long value representing the {@code BlockPos}
     */
    public static long encodeBlockPos(int x, int y, int z) {
        long lx = (x + (1 << (XZ_BITS - 1))) & XZ_MASK;
        long lz = (z + (1 << (XZ_BITS - 1))) & XZ_MASK;
        long ly = (y + (1 << (Y_BITS - 1))) & Y_MASK;

        return (lx << (Y_BITS + XZ_BITS)) | (lz << Y_BITS) | ly;
    }

    /**
     * Encodes a {@link BlockPos} into a single long value.
     *
     * @param pos The block position
     * @return Encoded long value
     */
    public static long encodeBlockPos(BlockPos pos) {
        return encodeBlockPos(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Encodes a {@link BlockPos} into a single long value.
     *
     * @param pos The block position
     * @return Encoded long value
     */
    public static long encodeVec3d(Vec3 pos) {
        return encodeBlockPos((int) pos.x, (int) pos.y, (int) pos.z);
    }

    /**
     * Encodes a {@link BlockPos} into a single long value.
     *
     * @param pos The block position
     * @return Encoded long value
     */
    public static LongArrayList encodeBlockPos(List<BlockPos> pos) {
        LongArrayList list = new LongArrayList(pos.size());
        for (BlockPos p : pos) {
            list.add(encodeBlockPos(p));
        }
        return list;
    }

    /**
     * Decodes a long value back into a {@link BlockPos}.
     *
     * @param encoded The encoded long value
     * @return Decoded {@code BlockPos}
     */
    public static BlockPos decodeBlockPos(long encoded) {
        int x = (int) ((encoded >> (Y_BITS + XZ_BITS)) & XZ_MASK) - (1 << (XZ_BITS - 1));
        int z = (int) ((encoded >> Y_BITS) & XZ_MASK) - (1 << (XZ_BITS - 1));
        int y = (int) (encoded & Y_MASK) - (1 << (Y_BITS - 1));

        return new BlockPos(x, y, z);
    }

    public static int[] decodeBlockPos2Array(long encoded) {
        return new int[]{
                (int) ((encoded >> (Y_BITS + XZ_BITS)) & XZ_MASK) - (1 << (XZ_BITS - 1)),
                (int) (encoded & Y_MASK) - (1 << (Y_BITS - 1)),
                (int) ((encoded >> Y_BITS) & XZ_MASK) - (1 << (XZ_BITS - 1))
        };
    }

    public static int[] convert2Array(BlockPos pos) {
        return new int[]{
                pos.getX(),
                pos.getY(),
                pos.getZ()
        };
    }

    public static int decodeX(long encoded) {
        return (int) ((encoded >> (Y_BITS + XZ_BITS)) & XZ_MASK) - (1 << (XZ_BITS - 1));
    }

    public static int decodeZ(long encoded) {
        return (int) ((encoded >> Y_BITS) & XZ_MASK) - (1 << (XZ_BITS - 1));
    }

    public static int decodeY(long encoded) {
        return (int) (encoded & Y_MASK) - (1 << (Y_BITS - 1));
    }


    public static ChunkPos getChunkPos(long encoded) {
        int x = (int) ((encoded >> (Y_BITS + XZ_BITS)) & XZ_MASK) - (1 << (XZ_BITS - 1));
        int z = (int) ((encoded >> Y_BITS) & XZ_MASK) - (1 << (XZ_BITS - 1));
        return new ChunkPos(
                SectionPos.blockToSectionCoord(x),
                SectionPos.blockToSectionCoord(z)
        );
    }

    public static long add(long pos, int dx, int dy, int dz) {
        long shiftedDx = (long) dx << (Y_BITS + XZ_BITS);
        long shiftedDz = (long) dz << Y_BITS;

        return pos + shiftedDx + shiftedDz + dy;
    }

    public static long up(long pos, int i) {
        return pos + i;
    }

    public static long offset(Direction direction, long pos, int distance) {
        return distance == 0
                ? pos
                : add(pos, direction.getStepX() * distance, direction.getStepY() * distance, direction.getStepZ() * distance);
    }
}

