package fr.rodofire.ewc.util;


import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;

@SuppressWarnings("unused")
public class DirectionUtil {
    /**
     * method to know
     */
    public static boolean isHorizontal(Direction dir) {
        return switch (dir) {
            case NORTH, SOUTH: yield false;
            default: yield true;
        };
    }

    /**
     * method to get a random direction no matter the plane
     *
     * @return a random direction
     */
    public static Direction getRandomDirection() {
        return switch (RandomSource.create().nextIntBetweenInclusive(0, 5)) {
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
        return RandomSource.create().nextIntBetweenInclusive(0, 1) == 1 ? Direction.UP : Direction.DOWN;
    }

    /**
     * method to get a random direction on the horizontal axis
     *
     * @return a random direction on the horizontal axis
     */
    public static Direction getRandomHorizontalDirection() {
        return switch (RandomSource.create().nextIntBetweenInclusive(0, 3)) {
            case 0 -> Direction.WEST;
            case 1 -> Direction.EAST;
            case 2 -> Direction.NORTH;
            default -> Direction.SOUTH;
        };
    }
}
