package net.rodofire.ewc_test.shape.block.gen;

import net.minecraft.util.math.BlockPos;
import net.rodofire.easierworldcreator.shape.block.gen.CircleGen;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;
import net.rodofire.ewc_test.shape.block.expected_shapes.gen.ExpectedCircle;
import net.rodofire.ewc_test.shape.block.expected_shapes.instanciator.ExpectedAbstractFillableBlockShape;
import net.rodofire.ewc_test.shape.block.expected_shapes.rotations.ExpectedRotator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;

public class CircleTest {
    @Nested
    class Consistency {
        private static void testEquality(CircleGen circleGen, ExpectedCircle expectedCircle) {
            Assertions.assertEquals(circleGen.getShapeCoordinates(), expectedCircle.getShapeCoordinates());
        }

        @Test
        public void testBaseConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 8);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 8);

            testEquality(circleGen, expectedCircle);
        }

        @Test
        public void testHugeBaseConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 32);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 32);

            testEquality(circleGen, expectedCircle);
        }

        @Test
        public void testFillingTypeConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 8);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 8);

            testFillingType(circleGen, expectedCircle);
        }

        @Test
        public void testHugeFillingTypeConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 32);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 32);

            testFillingType(circleGen, expectedCircle);
        }

        @Test
        public void testRotatorBaseConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 8);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 8);

            testRotator(circleGen, expectedCircle);
        }

        @Test
        public void testRotatorHugeBaseConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 32);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 32);

            testRotator(circleGen, expectedCircle);
        }

        private void testRotator(CircleGen circleGen, ExpectedCircle expectedCircle) {
            Rotator rotator = new Rotator(new BlockPos(-5, 45, 200), 15, 50, 242);
            circleGen.setRotator(rotator);
            expectedCircle.setRotator(rotator);

            testEquality(circleGen, expectedCircle);

            rotator = new Rotator(new BlockPos(-5, 45, 200), 180, 17, 0);
            circleGen.setRotator(rotator);
            expectedCircle.setRotator(rotator);

            testEquality(circleGen, expectedCircle);
        }

        @Test
        public void testRotatorFillingTypeConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 8);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 8);

            testFilledRotator(circleGen, expectedCircle);
        }

        @Test
        public void testRotatorHugeFillingTypeConsistency() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 32);
            ExpectedCircle expectedCircle = new ExpectedCircle(new BlockPos(-5, 45, 200), 32);

            testFilledRotator(circleGen, expectedCircle);
        }

        private void testFilledRotator(CircleGen circleGen, ExpectedCircle expectedCircle) {
            Rotator rotator = new Rotator(new BlockPos(-5, 45, 200), 15, 50, 242);
            circleGen.setRotator(rotator);
            expectedCircle.setRotator(rotator);

            testFillingType(circleGen, expectedCircle);

            rotator = new Rotator(new BlockPos(-5, 45, 200), 180, 17, 0);
            circleGen.setRotator(rotator);
            expectedCircle.setRotator(rotator);

            testFillingType(circleGen, expectedCircle);
        }

        private static void testFillingType(CircleGen circleGen, ExpectedCircle expectedCircle) {
            circleGen.setFillingType(AbstractFillableBlockShape.Type.HALF);
            expectedCircle.setFillingType(ExpectedAbstractFillableBlockShape.Type.HALF);
            testEquality(circleGen, expectedCircle);

            circleGen.setFillingType(AbstractFillableBlockShape.Type.EMPTY);
            expectedCircle.setFillingType(ExpectedAbstractFillableBlockShape.Type.EMPTY);
            testEquality(circleGen, expectedCircle);

            circleGen.setFillingType(AbstractFillableBlockShape.Type.FULL);
            expectedCircle.setFillingType(ExpectedAbstractFillableBlockShape.Type.FULL);
            testEquality(circleGen, expectedCircle);

            circleGen.setFillingType(AbstractFillableBlockShape.Type.CUSTOM);
            circleGen.setCustomFill(0.3f);
            expectedCircle.setFillingType(ExpectedAbstractFillableBlockShape.Type.CUSTOM);
            expectedCircle.setCustomFill(0.3f);
            testEquality(circleGen, expectedCircle);
        }
    }


    @Nested
    class Performance {
        @Test
        public void testCircleCoverPerformance() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 30000);
            Instant instant = Instant.now();
            circleGen.getCoveredChunks();
            System.out.println("Elapsed time: " + Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS));
            Assertions.assertTrue((double) (Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS) / 1000) / 1000 < 2000);
        }

        @Test
        public void testEmptyCircleCoverPerformance() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 30000);
            circleGen.setFillingType(AbstractFillableBlockShape.Type.EMPTY);
            Instant instant = Instant.now();
            circleGen.getCoveredChunks();
            System.out.println("Elapsed time: " + Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS));
            Assertions.assertTrue((double) (Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS) / 1000) / 1000 < 2000);
        }

        @Test
        public void testRotatedCircleCoverPerformance() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 30000);
            Instant instant = Instant.now();
            circleGen.getCoveredChunks();
            Rotator rotator = new Rotator(new BlockPos(-5, 45, 200), -5, 50, 242);
            circleGen.setRotator(rotator);
            System.out.println("Elapsed time: " + Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS));
            Assertions.assertTrue((double) (Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS) / 1000) / 1000 < 2000);
        }

        @Test
        public void testRotatedEmptyCircleCoverPerformance() {
            CircleGen circleGen = new CircleGen(new BlockPos(-5, 45, 200), 30000);
            circleGen.setFillingType(AbstractFillableBlockShape.Type.EMPTY);
            Rotator rotator = new Rotator(new BlockPos(-5, 45, 200), -5, 50, 242);
            circleGen.setRotator(rotator);
            Instant instant = Instant.now();
            circleGen.getCoveredChunks();
            System.out.println("Elapsed time: " + Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS));
            Assertions.assertTrue((double) (Duration.between(instant, Instant.now()).get(ChronoUnit.NANOS) / 1000) / 1000 < 2000);
        }
    }
}
