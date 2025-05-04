package fr.rodofire.ewc.blockdata.layer;


import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.blockdata.StructurePlacementRuleManager;
import fr.rodofire.ewc.shape.block.placer.LayerPlacer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a layer in a structure.
 * Each layer consists of a list of {@link BlockState} objects and an integer.
 * The list contains all the {@link BlockState} objects present in the layer,
 * while the integer represents the depth of the layer.
 * <p>
 * Be cautious with the depth parameter:
 * The depth should never be less than 0.
 * There is no benefit to having a depth equal to 0.
 */
@SuppressWarnings("unused")
public class BlockLayer {
    private List<BlockState> blockStates;
    List<Short> chances;
    private int depth = 1;

    private StructurePlacementRuleManager ruler;
    private LayerPlacer placer;

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states, int depth, StructurePlacementRuleManager ruler) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.depth = depth;
        this.ruler = ruler;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param states  list of BlockStates
     * @param depth   depth of the BlockStates
     * @param chances the chance of the related blockStates being chosen
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states, List<Short> chances, int depth, StructurePlacementRuleManager ruler) {
        this.blockStates = new ArrayList<>(states);
        this.chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            this.chances.add(chances.get(i));
        }
        this.depth = depth;
        this.ruler = ruler;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states, StructurePlacementRuleManager ruler) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.ruler = ruler;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayer(LayerPlacer placer, BlockState state, int depth, StructurePlacementRuleManager ruler) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        chances = new ArrayList<>();
        this.chances.add((short) 1);
        this.depth = depth;
        this.ruler = ruler;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayer(LayerPlacer placer, BlockState state, StructurePlacementRuleManager ruler) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        chances = new ArrayList<>();
        this.chances.add((short) 1);
        this.ruler = ruler;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states, int depth) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.depth = depth;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states) {
        this.blockStates = new ArrayList<>(states);
        chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            chances.add((short) 1);
        }
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param states  list of BlockStates
     * @param chances the chance of the state being chosen
     */
    public BlockLayer(LayerPlacer placer, List<BlockState> states, List<Short> chances) {
        this.blockStates = new ArrayList<>(states);
        this.chances = new ArrayList<>();
        for (int i = 0; i < states.size(); i++) {
            this.chances.add((chances.get(i)));
        }
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayer(LayerPlacer placer, BlockState state, int depth) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        chances = new ArrayList<>();
        this.chances.add((short) 1);
        this.depth = depth;
        this.placer = placer;
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayer(LayerPlacer placer, BlockState state) {
        this.blockStates = new ArrayList<>();
        this.blockStates.add(state);
        chances = new ArrayList<>();
        this.chances.add((short) 1);
        this.placer = placer;
    }

    public StructurePlacementRuleManager getRuler() {
        return ruler;
    }

    public void setRuler(StructurePlacementRuleManager ruler) {
        this.ruler = ruler;
    }


    public LayerPlacer getPlacer() {
        return placer;
    }

    public void setPlacer(LayerPlacer placer) {
        this.placer = placer;
    }

    /**
     * method used to get all the depth in a {@link BlockLayer}
     *
     * @return the depth of the layer
     */
    public int getDepth() {
        return depth;
    }

    /**
     * set a depth to the {@link BlockLayer}
     *
     * @param depth int to change the layer depth
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * add a depth to the {@link BlockLayer}
     *
     * @param depth int added to the layer depth
     */
    public void addDepth(int depth) {
        this.depth += depth;
    }

    /**
     * Method used to get all the {@link BlockState} in a {@link BlockLayer}.
     * The list returned will contain the BlockStates of the list,
     * where each will be present n times, n being the chance related to that BlockState
     *
     * @return the blockStates list of the layer
     */
    public List<BlockState> getBlockStates() {
        List<BlockState> state = new ArrayList<>();
        for (int i = 0; i < this.blockStates.size(); i++) {
            for (int j = 0; j < this.chances.get(i); j++) {
                state.add(this.blockStates.get(i));
            }
        }
        return state;
    }

    /**
     * method to get the blockState as well as his chance
     *
     * @return a list of pair corresponding to blockState as well as his chance
     */
    public List<Pair<BlockState, Short>> get() {
        List<Pair<BlockState, Short>> list = new ArrayList<>();
        for (int i = 0; i < this.blockStates.size(); i++) {
            list.add(new Pair<>(this.blockStates.get(i), this.chances.get(i)));
        }
        return list;
    }

    /**
     * method used to set all the {@link BlockState} in a {@link BlockLayer}
     *
     * @param blocks change the BlockStates of a layer
     */
    public void setBlockStates(List<BlockState> blocks) {
        this.blockStates = new ArrayList<>(blocks);
        chances = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            chances.add((short) 1);
        }
    }

    /**
     * add a BlockState to the layer
     *
     * @param state BlockState to be added
     */
    public void addBlockState(BlockState state) {
        this.blockStates.add(state);
        this.chances.add((short) 1);
    }

    /**
     * add multiple BlockStates to the layer
     *
     * @param states List of BlockState to be added
     */
    public void addBlockStates(List<BlockState> states) {
        this.blockStates.addAll(states);
        for (int i = 0; i < states.size(); i++) {
            this.chances.add((short) 1);
        }
    }

    /**
     * add a BlockState to the layer
     *
     * @param state  BlockState to be added
     * @param chance the chance of a blockState being chosen
     */
    public void addBlockState(BlockState state, short chance) {
        this.blockStates.add(state);
        this.chances.add(chance);
    }

    /**
     * add multiple BlockStates to the layer
     *
     * @param states  List of BlockState to be added
     * @param chances the chance list related to the states of a blockState being chosen
     * @throws IndexOutOfBoundsException in the case, the chance list has a size inferior to the BlockStates chance.
     */
    public void addBlockStates(List<BlockState> states, List<Short> chances) {
        this.blockStates.addAll(states);
        for (int i = 0; i < states.size(); i++) {
            this.chances.add(chances.get(i));
        }
    }

    /**
     * removes some BlockStates of the Layer
     *
     * @param state list of BlockStates that will be removed
     */
    public void removeBlockState(List<BlockState> state) {
        for (BlockState blockState : state) {
            this.chances.remove(this.blockStates.indexOf(blockState));
        }
        this.blockStates.removeAll(state);
    }

    /**
     * removes a BlockState of the Layer
     *
     * @param state BlockState that will be removed
     */
    public void removeBlockState(BlockState state) {
        this.chances.remove(this.blockStates.indexOf(state));
        this.blockStates.remove(state);
    }

    /**
     * method used to remove a {@link BlockState} related to the index
     *
     * @param index remove the BlockState at the index
     */
    public void removeBlockState(int index) {
        this.chances.remove(index);
        this.blockStates.remove(index);
    }

    /**
     * method used to get the size of the {@link BlockLayer}
     *
     * @return the size of the BlockStates
     */
    public int size() {
        return blockStates.size();
    }

    @Override
    public String toString() {
        return "BlockLayer{" +
                "blocks=" + blockStates +
                ", depth=" + depth +
                '}';
    }
}
