package fr.rodofire.ewc.maths;


import net.minecraft.util.RandomSource;

/**
 * util math class
 */
@SuppressWarnings("unused")
public class MathUtil {
    /**
     * method to get a random opposite with a 50% chance
     *
     * @return a random opposite
     */
    public static int getRandomOpposite() {
        return (RandomSource.create().nextIntBetweenInclusive(0, 1) == 0) ? 1 : -1;
    }

    /**
     * method to get a random opposite with the wanted chance
     *
     * @param chance the chance of -1 being selected
     * @return a random opposite
     */
    public static int getRandomOpposite(float chance) {
        return (RandomSource.create().nextFloat() <= chance) ? 1 : -1;
    }

    /**
     * method to get a random boolean
     *
     * @param chance the chance of true being selected
     * @return the random boolean
     */
    public static boolean getRandomBoolean(float chance) {
        return RandomSource.create().nextFloat() < chance;
    }

    /**
     * method to get a random opposite with a 50% chance
     *
     * @param random the random that will be used to get the value
     * @return a random opposite
     */
    public static int getRandomOpposite(RandomSource random) {
        return (random.nextIntBetweenInclusive(0, 1) == 0) ? 1 : -1;
    }

    /**
     * method to get a random opposite with the wanted chance
     *
     * @param chance the chance of -1 being selected
     * @param random the random that will be used to get the value
     * @return a random opposite
     */
    public static int getRandomOpposite(RandomSource random, float chance) {
        return (random.nextFloat() <= chance) ? 1 : -1;
    }

    /**
     * method to get a random boolean
     *
     * @param chance the chance of true being selected
     * @param random the random that will be used to get the value
     * @return the random boolean
     */
    public static boolean getRandomBoolean(RandomSource random, float chance) {
        return random.nextFloat() < chance;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the int to compare
     * @return the sign of the int
     */
    public static int getSign(int a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the double to compare
     * @return the sign of the double
     */
    public static int getSign(double a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * method to get the sign of a number
     *
     * @param a the float to compare
     * @return the sign of the float
     */
    public static int getSign(float a) {
        return (a < 0) ? -1 : 1;
    }

    /**
     * Calculates the absolute distance between two float values.
     *
     * @param a the first float value
     * @param b the second float value
     * @return the sum of the absolute values of {@code a} and {@code b}
     */
    public static float absDistance(float a, float b) {
        return Math.abs(a) + Math.abs(b);
    }

    /**
     * Calculates the absolute distance between two integer values.
     *
     * @param a the first integer value
     * @param b the second integer value
     * @return the sum of the absolute values of {@code a} and {@code b}
     */
    public static int absDistance(int a, int b) {
        return Math.abs(a) + Math.abs(b);
    }

    /**
     * Calculates the total absolute distance for an array of float values.
     *
     * @param floats an array of float values
     * @return the sum of the absolute values of the elements in {@code floats}
     */
    public static float absDistance(float... floats) {
        float distance = 0;
        for (float aFloat : floats) {
            distance += Math.abs(aFloat);
        }
        return distance;
    }

    /**
     * Calculates the total absolute distance for an array of integer values.
     *
     * @param ints an array of integer values
     * @return the sum of the absolute values of the elements in {@code ints}
     */
    public static int absDistance(int... ints) {
        int distance = 0;
        for (int anInt : ints) {
            distance += Math.abs(anInt);
        }
        return distance;
    }

    /**
     * Calculates the total absolute distance for an array of integer values.
     *
     * @param ints an array of integer values
     * @return the sum of the absolute values of the elements in {@code ints}
     */
    public static double absDistance(double... ints) {
        double distance = 0;
        for (double anInt : ints) {
            distance += Math.abs(anInt);
        }
        return distance;
    }

    public static double getExactDistance(double... coordinates) {
        double a = squared(coordinates);
        return Math.sqrt(a);
    }

    public static double squared(double... coordinates) {
        double a = 0;
        for (double pos : coordinates) {
            a += pos * pos;
        }
        return a;
    }
}
