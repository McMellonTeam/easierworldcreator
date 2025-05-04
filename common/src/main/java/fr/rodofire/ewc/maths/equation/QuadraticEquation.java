package fr.rodofire.ewc.maths.equation;


import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.maths.FastMaths;

@SuppressWarnings("unused")
public class QuadraticEquation {
    private final float a;
    private final float b;
    private final float c;
    private final float precision;

    public QuadraticEquation(float a, float b, float c) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.precision = 0.2f;
    }

    public QuadraticEquation(float a, float b, float c, float precision) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.precision = precision;
    }

    public Pair<Float, Float> solve() {
        float delta = b * b - 4 * a * c;
        if (delta < 0) {
            return null;
        }
        float r1 = (-b + FastMaths.getFastSqrt(delta, precision)) / (2 * a);
        float r2 = (-b - FastMaths.getFastSqrt(delta, precision)) / (2 * a);
        return new Pair<>(r1, r2);
    }
}
