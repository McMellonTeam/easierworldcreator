package fr.rodofire.ewc.shape.block.placer.animator;


import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.blocklist.BlockList;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.OrderedBlockListManager;
import fr.rodofire.ewc.blockdata.sorter.BlockSorter;
import fr.rodofire.ewc.maths.equation.CubicEquation;
import fr.rodofire.ewc.maths.equation.QuadraticEquation;
import fr.rodofire.ewc.platform.Services;
import fr.rodofire.ewc.util.LongPosHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Class to put blocks with an animation.
 * The class will place a number of blocks per seconds every tick depending on the parameters given.
 * <br>
 * The class provide some parameters like:
 * <ul>
 * <li>the fact that you can sort the BlockPos thanks to the {@code BlockSorter}.
 * <li>the possibility to change the number of blocks per tick
 * <li>the possibility to play block place sound every x ticks
 * </ul>
 * You can use the animator like this:
 * <pre>
 *     {@code StructurePlaceAnimator animator = new StructurePlaceAnimator(world, blockSorter, AnimatorTime.TICKS);
 * //the structure will be placed in 50 ticks no matter the size
 * animator.setTicks(50);
 * animator.place(blockListManager);
 *     }
 * </pre>
 *
 * <p>
 * How to use the animator?
 * <ul>
 * <li> Specify if you want to play block Place Sounds
 * <li> Specify how you want your animation to be played.
 * </ul>
 */
@SuppressWarnings({"unused", "Duplicates"})
public class StructurePlaceAnimator {
    private WorldGenLevel world;
    BlockSorter blockSorter;

    private AnimatorTime animatorTime;
    private AnimatorSound animatorSound = AnimatorSound.DEFAULT;

    /**
     * you don't need to manually define each of theses since that they each belong to one animatorTime
     */
    private Pair<Integer, Integer> bounds = new Pair<>(1, 100);
    private int blocksPerTick = 100;
    private int ticks = 500;

    int ticksPassed;

    private float soundPerTicks = 10f;


    // parameters for equation:
    // ax² + bx + c
    private float ax2 = 0;
    private float bx = 0;
    private int c = 0;

    /**
     * init a {@code StructurePlaceAnimator} object
     *
     * @param world        the world the animation will take place
     * @param blockSorter  the type of the animation
     * @param animatorTime the time of the animation
     */
    public StructurePlaceAnimator(WorldGenLevel world, BlockSorter blockSorter, AnimatorTime animatorTime) {
        this.world = world;
        this.blockSorter = blockSorter;
        this.animatorTime = animatorTime;
    }

    /**
     * Returns the random bounds for blocks placed per tick.
     *
     * @return a Pair representing the minimum and maximum number of blocks to be placed per tick.
     */
    public Pair<Integer, Integer> getBounds() {
        return bounds;
    }

    /**
     * Sets the bounds for {@link AnimatorTime}
     *
     * @param bounds the pair. See more details on {@link AnimatorTime}
     */
    public void setBounds(Pair<Integer, Integer> bounds) {
        this.bounds = bounds;
    }

    /**
     * Returns the number of blocks to place per tick.
     *
     * @return the number of blocks placed each tick.
     */
    public int getBlocksPerTick() {
        return blocksPerTick;
    }

    /**
     * Sets the number of blocks to place per tick.
     *
     * @param blocksPerTick the number of blocks placed each tick.
     */
    public void setBlocksPerTick(int blocksPerTick) {
        this.blocksPerTick = blocksPerTick;
    }

    /**
     * Returns the current tick count.
     *
     * @return the number of ticks.
     */
    public int getTicks() {
        return ticks;
    }

    /**
     * Sets the current tick count.
     *
     * @param ticks the number of ticks.
     */
    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    /**
     * Returns the AnimatorTime object, which manages timing for the animation.
     *
     * @return the AnimatorTime object.
     */
    public AnimatorTime getAnimatorTime() {
        return animatorTime;
    }

    /**
     * Sets the AnimatorTime object, which manages timing for the animation.
     *
     * @param animatorTime the AnimatorTime object.
     */
    public void setAnimatorTime(AnimatorTime animatorTime) {
        this.animatorTime = animatorTime;
    }

    /**
     * Returns the AnimatorType, representing the type of animation.
     *
     * @return the AnimatorType.
     */
    public BlockSorter getAnimatorType() {
        return blockSorter;
    }

    /**
     * Sets the AnimatorType, representing the type of animation.
     *
     * @param blockSorter the AnimatorType.
     */
    public void setAnimatorType(BlockSorter blockSorter) {
        this.blockSorter = blockSorter;
    }

    /**
     * Returns the WorldGenLevel for interacting with the world.
     *
     * @return the WorldGenLevel object.
     */
    public WorldGenLevel getWorld() {
        return world;
    }

    /**
     * Sets the WorldGenLevel for interacting with the world.
     *
     * @param world the WorldGenLevel object.
     */
    public void setWorld(WorldGenLevel world) {
        this.world = world;
    }

    /* ------ Sound Related ----- */

    /**
     * Returns the AnimatorSound object, which manages sounds for the animation.
     *
     * @return the AnimatorSound object.
     */
    public AnimatorSound getAnimatorSound() {
        return animatorSound;
    }

    /**
     * Sets the AnimatorSound object, which manages sounds for the animation.
     *
     * @param animatorSound the AnimatorSound object.
     */
    public void setAnimatorSound(AnimatorSound animatorSound) {
        this.animatorSound = animatorSound;
    }

    /**
     * Returns the interval of ticks between sounds during the animation.
     *
     * @return the interval as a float.
     */
    public float getSoundPerTicks() {
        return soundPerTicks;
    }

    /**
     * Sets the interval of ticks between sounds during the animation.
     *
     * @param soundPerTicks the interval as a float.
     */
    public void setSoundPerTicks(float soundPerTicks) {
        this.soundPerTicks = soundPerTicks;
    }

    /**
     * method to sort the list depending on the {@code animatorType} and handling the divided List of BlockList
     *
     * @param blockList the list of BlockList that will be sorted
     * @return a list of pair of BlockStates and BlockPos
     */
    List<BlockList> convertFromDividedToUnified(List<Set<BlockList>> blockList) {
        List<BlockList> fusedList = new ArrayList<>();
        for (Set<BlockList> set : blockList) {
            fusedList.addAll(set);
        }
        return fusedList;
    }


    /**
     * method to place the structure by merging and then sorting the BlockList depending on the {@code animatorType}
     *
     * @param blockList the list of BlockList that will be placed
     */
    @Deprecated(forRemoval = true)
    public void placeFromDividedBlockList(List<Set<BlockList>> blockList) {
        if (blockListVerification(blockList)) return;
        Instant start = Instant.now();
        List<BlockList> fusedList = convertFromDividedToUnified(blockList);
        //placeFromBlockList(fusedList);
    }

    /**
     * method to place the structure by sorting the BlockList depending on the {@code animatorType}
     *
     * @param manager the manager that will be placed
     */
    public void place(BlockListManager manager) {
        if (blockListVerification(manager.getAllBlockList())) return;
        Instant start = Instant.now();
        OrderedBlockListManager sortedBlockList = manager.getOrdered(this.blockSorter);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        EwcConstants.LOGGER.info("Shape sorted list calculations took : {}ms", timeElapsed.toMillis());
        this.place(sortedBlockList);
    }

    /**
     * method to verify that the provided list is not Empty
     *
     * @param blockList the list to verify
     * @param <T>       allow to be used in the divided and unified context
     * @return true if it's empty or null or false in the other case
     */
    private static <T> boolean blockListVerification(List<T> blockList) {
        if (blockList == null || blockList.isEmpty()) {
            EwcConstants.LOGGER.warn("StructureBlockAnimator: blockList is null or empty");
            return true;
        }
        return false;
    }

    /**
     * <p>Method to handle animated block placement. This has some advantages : </p>
     * <p> - Since that the placement take many ticks instead of one, it will improve user experience when placing huge structures
     * <p> - It may look better than just a structure spawning
     * <p>The method need a {@link OrderedBlockListManager} that will be used to place the blocks.
     * <p>The method calculates the number of ticks it will take to place the structure and will then place a part of the structure depending on how much blocks per ticks should be placed.
     * <p>To place them, it registers an event happening at the end of each world tick.
     *
     * @param manager the {@code OrderedBlockListManager} that will be placed.
     */
    public void place(OrderedBlockListManager manager) {
        List<Integer> randomBlocks = new ArrayList<>();
        int totalBlocks = manager.posSize();
        AtomicReference<Float> soundPlayed = new AtomicReference<>((float) 0);


        calculateBlockPerTicks(totalBlocks, randomBlocks);

        switch (animatorSound) {
            case NO_SOUND -> this.soundPerTicks = 0;
            case DEFAULT -> this.soundPerTicks = 0.5f;
            case NUMBER_PER_TICK -> this.soundPerTicks = Math.min(blocksPerTick, this.soundPerTicks);
            default -> throw new IllegalStateException("Unexpected value: " + animatorSound);
        }

        this.ticksPassed = 0;
        //calling on end server tick because end world tick wouldn't place the blocks 2 times on 3.
        Services.PlatformEvents.WORLD.onEndServerTick(server -> {
            if (ticksPassed == this.ticks || manager.isPosEmpty()) {
                return;
            }
            soundPlayed.set(soundPlayed.get() + this.soundPerTicks);

            int blocksThisTick;
            if (animatorTime == AnimatorTime.RANDOM_BLOCKS_PER_TICK && !randomBlocks.isEmpty()) {
                blocksThisTick = randomBlocks.removeLast();
            } else {
                int block = (int) (ax2 * this.ticksPassed * this.ticksPassed + bx * this.ticksPassed + c);
                blocksThisTick = Math.min(block, manager.posSize());
            }

            for (int i = 0; i < blocksThisTick && !manager.isPosEmpty(); i++) {

                if (soundPlayed.get() >= 1) {
                    BlockState state = manager.getLastBlockState();
                    BlockPos pos = LongPosHelper.decodeBlockPos(manager.getLastBlockPos());
                    world.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, (float) RandomSource.create().nextIntBetweenInclusive(20, 100) / 10, (float) RandomSource.create().nextIntBetweenInclusive(5, 20) / 10);
                    soundPlayed.set(soundPlayed.get() - 1);
                }

                manager.placeLastNDelete(world);
            }
            ticksPassed++;

            if (ticksPassed == this.ticks && !manager.isPosEmpty()) {
                int left = manager.posSize();
                EwcConstants.LOGGER.info("All ticks completed, but {} blocks are still unplaced. Placing remaining blocks in final tick.", left);
                manager.placeAllNDelete(world);
                manager.clear();
            }
        });
    }

    /**
     * Method used to calculate the factors of the animator
     *
     * @param totalBlocks  the number of blocks that have to be placed
     * @param randomBlocks the random blocks list in the case of random placeTime
     */
    private void calculateBlockPerTicks(int totalBlocks, List<Integer> randomBlocks) {
        switch (animatorTime) {
            case CONSTANT_TICKS -> {
                if (ticks == 0) {
                    this.ticks = 1;
                }
                if (this.ticks > totalBlocks)
                    this.ticks = totalBlocks;

                this.c = Math.max(1, totalBlocks / ticks);
            }
            case LINEAR_TICKS -> {
                int start = bounds.getFirst();
                int end = bounds.getSecond();

                if (start < 1)
                    start = 1;
                if (end < 1)
                    end = 1;

                this.ticks = (int) Math.ceil((double) (2 * totalBlocks) / (start + end));

                this.bx = (float) (end - start) / (this.ticks - 1);
                this.c = start;
            }
            case QUADRATIC_TICKS -> {
                int start = bounds.getFirst();
                int end = bounds.getSecond();

                if (start < 1)
                    start = 1;
                if (end < 1)
                    end = 1;

                float a = (float) -(4 * start - end) / 3;
                float b = totalBlocks - (float) (3 * start - end) / 2;
                float c = (float) (-start + end) / 6;

                QuadraticEquation quadraticEquation = new QuadraticEquation(a, b, c);
                Pair<Float, Float> result = quadraticEquation.solve();
                if (result == null || (result.getSecond() < 0 && result.getFirst() < 0)) {
                    this.ax2 = 1;
                    return;
                }
                if (result.getFirst() < 0) {
                    this.ticks = (int) Math.ceil(result.getSecond());
                } else {
                    this.ticks = (int) Math.ceil(result.getFirst());
                }

                if (this.ticks == 1) {
                    this.c = totalBlocks;
                    return;
                }

                this.ax2 = (float) (end - start) / ((this.ticks - 1) * (this.ticks - 1));
                this.c = start;
            }
            case RANDOM_BLOCKS_PER_TICK -> {
                int blocksSelected = 0;
                while (blocksSelected < totalBlocks) {
                    int randomInt = RandomSource.create().nextIntBetweenInclusive(
                            Math.min(totalBlocks - blocksSelected, bounds.getFirst()),
                            Math.min(totalBlocks - blocksSelected, bounds.getSecond())
                    );
                    blocksSelected += randomInt;
                    randomBlocks.add(randomInt);
                }
                this.ticks = randomBlocks.size();
            }
            case CONSTANT_BLOCKS_PER_TICK -> {
                if (blocksPerTick <= 0) {
                    EwcConstants.LOGGER.error("StructureBlockAnimator: blocksPerTick is zero or negative");
                    throw new IllegalStateException();
                }
                this.c = blocksPerTick;
                this.ticks = (int) Math.ceil((double) totalBlocks / blocksPerTick);
            }
            case LINEAR_BLOCK_PER_TICK -> {
                if (blocksPerTick <= 0) {
                    EwcConstants.LOGGER.error("StructureBlockAnimator: blocksPerTick is zero or negative");
                    throw new IllegalStateException();
                }

                float a = (float) blocksPerTick / 2;
                float b = (float) blocksPerTick / 2;
                float c = (float) -totalBlocks;

                QuadraticEquation equation = new QuadraticEquation(a, b, c);
                Pair<Float, Float> result = equation.solve();

                if (result == null || (result.getSecond() < 0 && result.getFirst() < 0)) {
                    this.ax2 = 1;
                    return;
                }
                if (result.getFirst() < 0) {
                    this.ticks = (int) Math.ceil(result.getSecond());
                } else {
                    this.ticks = (int) Math.ceil(result.getFirst());
                }

                this.bx = blocksPerTick;
            }
            case QUADRATIC_BLOCK_PER_TICK -> {
                if (blocksPerTick <= 0) {
                    EwcConstants.LOGGER.error("StructureBlockAnimator: blocksPerTick is zero or negative");
                    throw new IllegalStateException();
                }


                float a = (float) blocksPerTick / 3;
                float b = (float) blocksPerTick / 2;
                float c = (float) blocksPerTick / 6;
                float d = -totalBlocks;

                CubicEquation equation = new CubicEquation();
                equation.solve(a, b, c, d);

                if (equation.x1 > 1) {
                    this.ticks = (int) Math.ceil(equation.x1);
                } else if (equation.x2 > 1) {
                    this.ticks = (int) Math.ceil(equation.x2);
                } else if (equation.x3 > 1) {
                    this.ticks = (int) Math.ceil(equation.x3);
                } else {
                    this.ticks = 2000;
                }

                this.ax2 = blocksPerTick;
            }
            default -> throw new IllegalStateException("Unexpected value: " + animatorTime);
        }

    }

    /**
     * enum to determine how much time the structure will be placed.
     */
    public enum AnimatorTime {
        /**
         * Determines a defined number of blocks per tick.
         * Each tick, a number of {@code blockPerTick} will be placed.
         * You then have to specify {@code blockPerTick}
         */
        CONSTANT_BLOCKS_PER_TICK,
        /**
         * Will place an increasing (or decreasing) number of blocks per ticks linearly.
         * You will have to specify the number of blockPerTicks that will be used based on the following principle:
         * <ul>
         * <li> tick0 -> zero blocks placed
         * <li> tick1 -> blockPerTick blocks placed
         * <li> tick2 -> 2 * blockPerTick blocks placed
         * <p> ...
         * <li> tickN-1 -> (N-1) * blockPerTick blocks placed
         * <li> tickN -> min(N * blockPerTick blocks placed, remaining blocks)
         * </ul>
         * to init the animator, You simply have to specify the number of {@code BlockPerTicks}
         */
        LINEAR_BLOCK_PER_TICK,
        /**
         * Will place an increasing (or decreasing) number of blocks per ticks that will grow on x².
         * You will have to specify the number of blockPerTicks that will be used based on the following principle:
         * <ul>
         * <li> tick0 -> zero blocks placed
         * <li> tick1 -> blockPerTick blocks placed
         * <li> tick2 -> 4 * blockPerTick blocks placed (2*2)
         * <p> ...
         * <li> tickN-1 -> (N-1) * (N-1) * blockPerTick blocks placed
         * <li> tickN -> min(N * N * blockPerTick blocks placed, remaining blocks)
         * </ul>
         * <br>
         * to init the animator, You simply have to specify the number of {@code BlockPerTicks}
         */
        QUADRATIC_BLOCK_PER_TICK,
        /**
         * Will place a random number blocks every tick.
         * For that you need to use the {@code bounds}:
         * <pre>
         *     {@code
         *     animator.setBounds(min_value, max_value);
         *     }
         * </pre>
         * <p> The animator will then choose a random number of blocks to place each tick.
         * <p> The random number chose will be contained between the min value and the max value.
         * For example:
         * <pre>
         *     {@code
         *     //possible values : {5, 6, 7, 8, 9, 10}
         *     animator.setBounds(5,10);
         *     }
         * </pre>
         */
        RANDOM_BLOCKS_PER_TICK,
        /**
         * <p> Determines a fixed number of ticks to place the structure.
         * <p> For that, you need to specify the number of ticks: {@code setTicks()}.
         * <p> The structure will then place a number of {@code blockSize / ticks} block each tick.
         */
        CONSTANT_TICKS,
        /**
         * Will place an increasing (or decreasing) number of blocks per ticks linearly.
         * You have to set a bound of blocks {@link StructurePlaceAnimator}.
         * <ul>
         * <li> The number of blocks that will be placed on the first tick is the first integer of the pair.
         * <li> The last int of the pair represents the number of blocks that will be placed at the last tick.
         * </ul>
         * The related method will then calculate the coefficients
         * that will determine how much blocks will be placed each tick.
         */
        LINEAR_TICKS,
        /**
         * Will place an increasing (or decreasing) number of blocks per ticks following a quadratic curve.
         * You have to set a bound of blocks {@link StructurePlaceAnimator}.
         * <ul>
         * <li> The number of blocks that will be placed on the first tick is the first integer of the pair.
         * <li> The last int of the pair represents the number of blocks that will be placed at the last tick.
         * </ul>
         * The related method will then calculate the coefficients
         * that will determine how much blocks will be placed each tick.
         */
        QUADRATIC_TICKS
    }

    /**
     * enum to decide how to play sounds
     */
    public enum AnimatorSound {
        /**
         * no sounds will be played
         */
        NO_SOUND,
        /**
         * will play one sound every tick
         */
        DEFAULT,
        /**
         * set a number of sounds per tick
         */
        NUMBER_PER_TICK
    }
}
