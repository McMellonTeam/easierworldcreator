package fr.rodofire.ewc.maths;


import fr.rodofire.ewc.EwcConstants;

/**
 * Own implementation of maths focused on better performance
 * since that precision is not needed that much in the case of minecraft
 */
@SuppressWarnings("unused")
public class FastMaths {
    //fast but not precise, it is useful to use this when using high radius values

    private static final int FAST_TRIGO_TABLE_SIZE = 360;
    private static final int FAST_TABLE_SIZE = 1000;
    private static final float[] cosFastTable = new float[FAST_TRIGO_TABLE_SIZE];
    private static final float[] sinFastTable = new float[FAST_TRIGO_TABLE_SIZE];
    private static final float[] expFastTable = new float[FAST_TABLE_SIZE];

    //took more time but more precise, you should use this when you need to generate big things
    private static final int PRECISE_TRIGO_TABLE_SIZE = 3600;
    private static final int PRECISE_TABLE_SIZE = 10000;
    private static final float[] cosPreciseTable = new float[PRECISE_TRIGO_TABLE_SIZE];
    private static final float[] sinPreciseTable = new float[PRECISE_TRIGO_TABLE_SIZE];
    private static final float[] expPreciseTable = new float[PRECISE_TABLE_SIZE];

    static {
        for (int i = 0; i < FAST_TRIGO_TABLE_SIZE; i++) {
            cosFastTable[i] = (float) Math.cos(Math.toRadians(i));
            sinFastTable[i] = (float) Math.sin(Math.toRadians(i));
        }
        for (int i = 0; i < PRECISE_TRIGO_TABLE_SIZE; i++) {
            double a = (double) i / 10;
            cosPreciseTable[i] = (float) Math.cos(Math.toRadians(a));
            sinPreciseTable[i] = (float) Math.sin(Math.toRadians(a));
        }
        for (int i = 0; i < FAST_TABLE_SIZE; i++) {
            expFastTable[i] = (float) Math.exp((double) i / 100);
        }
        for (int i = 0; i < PRECISE_TABLE_SIZE; i++) {
            expPreciseTable[i] = (float) Math.exp(-Math.exp((double) i / 1000));
        }

    }

    /**
     * method to initialize class when launching the mod
     */
    public static void registerMaths() {
        EwcConstants.LOGGER.info("|\t- Registering FastMaths");
    }

    /**
     * method to get a fast sinus: less precise but faster (0.5° precision)
     *
     * @param angle the angle in degrees
     * @return the sinus of the angle
     */
    public static float getFastSin(float angle) {
        angle = ((angle % 360) + 360) % 360;
        return sinFastTable[((int) angle + 360) % FAST_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a fast cosine: less precise but faster (0.5° precision)
     *
     * @param angle the angle in degrees
     * @return the cosine of the angle
     */
    public static float getFastCos(float angle) {
        angle = angle % 360;
        return cosFastTable[((int) angle + 360) % FAST_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a fast tan: less precise but faster (0.5° precision), based of fast cosines and sinus
     *
     * @param x the angle in degrees
     * @return the tan of the angle
     */
    public static float getFastTan(float x) {
        if ((int) x % 180 == 90) {
            EwcConstants.LOGGER.error("thrown error, tan can't accept values equal to 90° +- 180° (div by 0 error)");
            return 0;
        }
        return sinFastTable[((int) x) % 360] / cosFastTable[((int) x) % 360];
    }

    /**
     * method to get a fast exponential: less precise but faster (0.05 of precision)
     *
     * @param x the value
     * @return the exponential of the angle
     */
    public static float getFastExp(float x) {
        if (x > 10.05) {
            EwcConstants.LOGGER.warn("getFastExp() : too big exponential, returning real exp value");
            return (float) Math.exp(x);
        }
        return expFastTable[((int) (x * 100))];
    }

    /**
     * method to get a precise sinus: more precise but slower (0.05° of precision)
     *
     * @param angle the angle in degrees
     * @return the sinus of the angle
     */
    public static float getPreciseSin(float angle) {
        int angle1 = ((int) (angle * 10)) % PRECISE_TRIGO_TABLE_SIZE;
        return sinPreciseTable[(angle1 + PRECISE_TRIGO_TABLE_SIZE) % PRECISE_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a precise sinus: more precise but slower (0.05° of precision)
     *
     * @param angle the angle in degrees
     * @return the cosines of the angle
     */
    public static float getPreciseCos(float angle) {
        int angle1 = ((int) (angle * 10)) % PRECISE_TRIGO_TABLE_SIZE;
        return cosPreciseTable[(angle1 + PRECISE_TRIGO_TABLE_SIZE) % PRECISE_TRIGO_TABLE_SIZE];
    }

    /**
     * method to get a precise tan: more precise but slower (0.05° of precision), based of fast cosines and sinus
     *
     * @param x the angle in degrees
     * @return the tan of the angle
     */
    public static float getPreciseTan(float x) {
        if ((int) (x * 10) % 180 == 90) {
            EwcConstants.LOGGER.error("getPreciseExp() :thrown error, tan can't accept values equal to 90° +- 180° (div by 0 error)");
            return 0;
        }
        return sinPreciseTable[((int) (x * 10)) % PRECISE_TRIGO_TABLE_SIZE] / cosPreciseTable[((int) x * 10) % PRECISE_TRIGO_TABLE_SIZE];


    }

    /**
     * method to get a fast exponential: less precise but faster (0.005 of precision)
     *
     * @param x the value
     * @return the exp of the angle
     */
    public static float getPreciseExp(float x) {
        if (x > 10.05) {
            EwcConstants.LOGGER.warn("too big exponential, returning real exp value");
            return (float) Math.exp(x);
        }
        return expPreciseTable[((int) (x * 1000))];

    }


    /**
     * method to get a length of a 3d vector (x,y,z) with the center point being (0,0,0) with precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param y the y coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static float getLength(int x, int y, int z) {
        return getFastSqrt(x * x + y * y + z * z);
    }

    /**
     * method to get a length of a 3d vector (x,y,z) with the center point being (0,0,0) with the precision wanted
     *
     * @param x         the x coordinate of the vector
     * @param y         the y coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static float getLengthWPrecision(int x, int y, int z, float precision) {
        return getFastSqrt(x * x + y * y + z * z, precision);
    }

    /**
     * method to get a length in the case of a 3d vector with floats coordinates:
     * (x,y,z) with the center point being (0,0,0) with precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param y the y coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static float getLength(float x, float y, float z) {
        return getFastSqrt(x * x + y * y + z * z);
    }

    /**
     * method to get a length in the case of a 3d vector with floats coordinates:
     * (x,y,z) with the center point being (0,0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param y         the y coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static float getLengthWPrecision(float x, float y, float z, float precision) {
        return getFastSqrt(x * x + y * y + z * z, precision);
    }

    /**
     * method to get a length in the case of a 2d vector:
     * (x,y) with the center point being (0,0) with precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static float getLength(int x, int z) {
        return getFastSqrt(x * x + z * z);
    }

    /**
     * method to get a length in the case of a 2d vector:
     * (x,y) with the center point being (0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static float getLengthWPrecision(int x, int z, float precision) {
        return getFastSqrt(x * x + z * z, precision);
    }

    /**
     * method to get a length in the case of a 2d vector with floats coordinates:
     * (x,y) with the center point being (0,0) with precision of 0.2f
     *
     * @param x the x coordinate of the vector
     * @param z the z coordinate of the vector
     * @return the length of the vector
     */
    public static float getLength(float x, float z) {
        return getFastSqrt(x * x + z * z);
    }

    /**
     * method to get a length in the case of a 2d vector with floats coordinates:
     * (x,y) with the center point being (0,0) with the wanted precision
     *
     * @param x         the x coordinate of the vector
     * @param z         the z coordinate of the vector
     * @param precision the wanted precision of the result
     * @return the length of the vector
     */
    public static float getLengthWPrecision(float x, float z, float precision) {
        return getFastSqrt(x * x + z * z, precision);
    }

    /**
     * method to get a precise sqrt (0.2 of precision)
     *
     * @param number the angle in degrees
     * @return the sqrt of the number
     */
    public static float getFastSqrt(float number) {
        return getFastSqrt(number, 0.2f);
    }

    public static double getFastSqrt(double number) {
        return getFastSqrt(number, 0.2f);
    }

    /**
     * method to get a precise sqrt with the wanted precision
     *
     * @param number    the angle in degrees
     * @param precision the wanted precision of the result
     * @return the sqrt of the number
     */
    public static float getFastSqrt(float number, float precision) {

        if (number < 0) {
            throw new IllegalArgumentException("Bound must be positive inside of sqrt");
        }

        if (number == 0 || number == 1) {
            return number;
        }

        float x = number;
        float prev;

        do {
            prev = x;
            x = (x + number / x) / 2;
        } while (Math.abs(x - prev) > precision);
        return x;
    }

    public static double getFastSqrt(double number, float precision) {

        if (number < 0) {
            throw new IllegalArgumentException("Bound must be positive inside of sqrt");
        }

        if (number == 0 || number == 1) {
            return number;
        }

        double x = number;
        double prev;

        do {
            prev = x;
            x = (x + number / x) / 2;
        } while (Math.abs(x - prev) > precision);
        return x;
    }
}
