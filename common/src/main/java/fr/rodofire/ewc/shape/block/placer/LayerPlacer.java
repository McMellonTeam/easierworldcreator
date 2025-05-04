package fr.rodofire.ewc.shape.block.placer;


import fr.rodofire.ewc.blockdata.StructurePlacementRuleManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayer;
import fr.rodofire.ewc.util.BlockPlaceUtil;
import fr.rodofire.ewc.util.FastNoiseLite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Class to define how the {@link BlockState} inside a {@link BlockLayer} should be placed depending on {@link PlacingType}
 */
@SuppressWarnings("unused")
public class LayerPlacer {
    private final PlacingType type;
    FastNoiseLite noise;
    int placedBlocks = 0;
    private RandomSource random = RandomSource.create();

    /**
     * use factory methods {@link LayerPlacer#ofRandom()}
     */
    @Deprecated(forRemoval = true)
    public LayerPlacer(int placedBlocks, PlacingType type) {
        this.placedBlocks = placedBlocks;
        this.type = type;
    }

    /**
     * use factory methods {@link LayerPlacer#ofRandom()}
     */
    @Deprecated(forRemoval = true)
    public LayerPlacer(PlacingType type, FastNoiseLite noise) {
        this.type = type;
        this.noise = noise;
    }

    /**
     * use factory methods {@link LayerPlacer#ofRandom()}
     */
    @Deprecated(forRemoval = true)
    public LayerPlacer(PlacingType type) {
        this.type = type;
    }

    /**
     * use factory methods {@link LayerPlacer#ofRandom()}
     */
    @Deprecated(forRemoval = true)
    public LayerPlacer(int placedBlocks, PlacingType type, RandomSource random) {
        this.placedBlocks = placedBlocks;
        this.random = random;
        this.type = type;
    }

    /**
     * use factory methods {@link LayerPlacer#ofRandom()}
     */
    @Deprecated(forRemoval = true)
    public LayerPlacer(PlacingType type, RandomSource random) {
        this.type = type;
        this.random = random;
    }

    private LayerPlacer(PlacingType type, FastNoiseLite noise, RandomSource random) {
        this.type = type;
        this.random = random;
        this.noise = noise;
    }

    public static LayerPlacer ofRandom() {
        return new LayerPlacer(PlacingType.RANDOM, null, RandomSource.create());
    }

    public static LayerPlacer ofRandom(RandomSource random) {
        return new LayerPlacer(PlacingType.RANDOM, null, random);
    }

    public static LayerPlacer of3DNoise(FastNoiseLite noise) {
        return new LayerPlacer(PlacingType.NOISE3D, noise, null);
    }

    public static LayerPlacer of3DNoise() {
        return new LayerPlacer(PlacingType.NOISE3D, new FastNoiseLite(), null);
    }

    public static LayerPlacer of2DNoise(FastNoiseLite noise) {
        return new LayerPlacer(PlacingType.NOISE2D, noise, null);
    }

    public static LayerPlacer of2DNoise() {
        return new LayerPlacer(PlacingType.NOISE2D, new FastNoiseLite(), null);
    }

    public static LayerPlacer ofOrder(){
        return new LayerPlacer(PlacingType.ORDER, null, null);
    }


    public LayerPlacer() {
        this.type = PlacingType.RANDOM;
    }


    public void setNoise(FastNoiseLite noise) {
        this.noise = noise;
    }


    public void setRandom(RandomSource random) {
        this.random = random;
    }

    public boolean place(WorldGenLevel worldAccess, List<BlockState> states, BlockPos pos) {
        return place(worldAccess, states, pos, null);
    }

    public boolean place(WorldGenLevel worldAccess, BlockState[] states, BlockPos pos) {
        return place(worldAccess, states, pos, null);
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything.
     * Used for a precomputed BlockState list
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return the BlockState related to the pos
     **/

    public boolean place(WorldGenLevel worldAccess, List<BlockState> states, BlockPos pos, StructurePlacementRuleManager ruler) {
        BlockState state;
        //avoid doing unnecessary calculations if only one blockState is present.
        if (states.size() == 1)
            state = states.getFirst();
        else {
            switch (this.type) {
                case NOISE2D -> state = BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
                case NOISE3D -> state = BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
                case ORDER -> {
                    state = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                    this.placedBlocks = (this.placedBlocks + 1) % (states.size() - 1);
                }
                default -> state = BlockPlaceUtil.getRandomBlock(states, random);
            }
        }
        if (ruler == null) {
            return worldAccess.getBlockState(pos).isAir() && worldAccess.setBlock(pos, state, 3);
        }
        return ruler.canPlace(worldAccess.getBlockState(pos)) && worldAccess.setBlock(pos, state, 3);
    }

    public boolean place(WorldGenLevel worldAccess, BlockState[] states, BlockPos pos, StructurePlacementRuleManager ruler) {
        BlockState state;
        //avoid doing unnecessary calculations if only one blockState is present.
        if (states.length == 1)
            state = states[0];
        else {
            switch (this.type) {
                case NOISE2D -> state = BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
                case NOISE3D -> state = BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
                case ORDER -> {
                    state = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                    this.placedBlocks = (this.placedBlocks + 1) % (states.length - 1);
                }
                default -> state = BlockPlaceUtil.getRandomBlock(states, random);
            }
        }
        if (ruler == null) {
            return worldAccess.getBlockState(pos).isAir() && worldAccess.setBlock(pos, state, 3);
        }
        return ruler.canPlace(worldAccess.getBlockState(pos)) && worldAccess.setBlock(pos, state, 3);
    }

    /**
     * Used to get the blocksState notably used during this.getWorld() gen, this doesn't place anything.
     * Used for a precomputed BlockState list
     *
     * @param states the states that will be chosen
     * @param pos    the pos of the block
     * @return the BlockState related to the pos
     **/
    public BlockState get(List<BlockState> states, BlockPos pos) {
        //avoid doing unnecessary calculations if only one blockState is present.
        if (states.size() == 1)
            return states.getFirst();

        return switch (this.type) {
            case RANDOM -> BlockPlaceUtil.getRandomBlock(states, random);
            case NOISE2D -> BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.size() - 1);
                yield blockState;
            }
        };
    }

    public BlockState get(BlockState[] states, BlockPos pos) {
        //avoid doing unnecessary calculations if only one blockState is present.
        if (states.length == 1)
            return states[0];
        return switch (this.type) {
            case RANDOM -> BlockPlaceUtil.getRandomBlock(states, random);
            case NOISE2D -> BlockPlaceUtil.getBlockWith2DNoise(states, pos, this.noise);
            case NOISE3D -> BlockPlaceUtil.getBlockWith3DNoise(states, pos, this.noise);
            case ORDER -> {
                BlockState blockState = BlockPlaceUtil.getBlockWithOrder(states, this.placedBlocks);
                this.placedBlocks = (this.placedBlocks + 1) % (states.length - 1);
                yield blockState;
            }
        };
    }


    /**
     * set how the blocks/particles will be chosen inside a layer
     */
    public enum PlacingType {
        /**
         * will choose random Block/Particle in the layer
         */
        RANDOM,
        /**
         * will place the first Block/particle in the layer, then the second, then the third, in the order
         */
        ORDER,
        /**
         * Will place the Block/Particle according to a 2d noise.
         * This is slower than random placing type.
         * If the placement does not matter for you, choose {@link PlacingType#RANDOM}
         */
        NOISE2D,
        /**
         * Will place the Block/Particle according to a 3d noise
         * This is slower than random or 2d noise placing type.
         * If the placement does not matter for you, choose {@link PlacingType#RANDOM}
         */
        NOISE3D
    }
}
