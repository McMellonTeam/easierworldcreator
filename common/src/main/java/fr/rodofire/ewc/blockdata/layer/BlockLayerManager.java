package fr.rodofire.ewc.blockdata.layer;


import fr.rodofire.ewc.blockdata.StructurePlacementRuleManager;
import fr.rodofire.ewc.shape.block.placer.LayerPlacer;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code BlockLayerComparator} class provides utility methods to manage a collection of {@code BlockLayer} objects.
 * This class allows manipulation of individual layers and their properties, including adding, removing, and modifying
 * layers, as well as checking for specific conditions such as forced parameters.
 */
@SuppressWarnings("unused")
public class BlockLayerManager {

    /**
     * The list of {@code BlockLayer} objects managed by this comparator.
     */
    private List<BlockLayer> layers = new ArrayList<>();

    boolean repeatLayers = false;

    /**
     * Constructs a {@code BlockLayerComparator} with the specified list of layers.
     *
     * @param layers the initial list of {@code BlockLayer} objects to manage
     */
    public BlockLayerManager(List<BlockLayer> layers) {
        this.layers = new ArrayList<>(layers);
    }

    public BlockLayerManager(BlockLayer... layers) {
        this.layers.addAll(Arrays.stream(layers).toList());
    }

    /**
     * Constructs a {@code BlockLayerComparator} with the specified layers.
     *
     * @param layer the initial {@code BlockLayer} objects to manage
     */
    public BlockLayerManager(BlockLayer layer) {
        this.layers = new ArrayList<>();
        this.layers.add(layer);
    }

    /**
     * Constructs a {@code BlockLayerComparator} with the specified layers.
     */
    public BlockLayerManager(LayerPlacer placer, BlockState state, short depth) {
        this.layers = new ArrayList<>();
        this.layers.add(new BlockLayer(placer, state, depth));
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states, int depth, StructurePlacementRuleManager ruler) {
        this.layers.add(new BlockLayer(placer, states, depth, ruler));
    }

    /**
     * init the BlockLayer
     *
     * @param states  list of BlockStates
     * @param depth   depth of the BlockStates
     * @param chances the chance of the related blockStates being chosen
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states, List<Short> chances, int depth, StructurePlacementRuleManager ruler) {
        this.layers.add(new BlockLayer(placer, states, chances, depth, ruler));
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states, StructurePlacementRuleManager ruler) {
        this.layers.add(new BlockLayer(placer, states, ruler));
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, BlockState state, int depth, StructurePlacementRuleManager ruler) {
        this.layers.add(new BlockLayer(placer, state, depth, ruler));
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayerManager(LayerPlacer placer, BlockState state, StructurePlacementRuleManager ruler) {
        this.layers.add(new BlockLayer(placer, state, ruler));
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     * @param depth  depth of the BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states, int depth) {
        this.layers.add(new BlockLayer(placer, states, depth));
    }

    /**
     * init the BlockLayer
     *
     * @param states list of BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states) {
        this.layers.add(new BlockLayer(placer, states));
    }

    /**
     * init the BlockLayer
     *
     * @param states  list of BlockStates
     * @param chances the chance of the state being chosen
     */
    public BlockLayerManager(LayerPlacer placer, List<BlockState> states, List<Short> chances) {
        this.layers.add(new BlockLayer(placer, states, chances));
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     * @param depth list of BlockStates
     */
    public BlockLayerManager(LayerPlacer placer, BlockState state, int depth) {
        this.layers.add(new BlockLayer(placer, state, depth));
    }

    /**
     * init the BlockLayer
     *
     * @param state if the layer is only composed of one BlockState, you don't necessary need to create a list (created automatically)
     */
    public BlockLayerManager(LayerPlacer placer, BlockState state) {
        this.layers.add(new BlockLayer(placer, state));
    }

    /**
     * Returns the list of {@code BlockLayer} objects.
     *
     * @return the list of layers
     */
    public List<BlockLayer> getLayers() {
        return layers;
    }

    /**
     * Replaces the current list of {@code BlockLayer} objects with the specified list.
     *
     * @param layers the new list of {@code BlockLayer} objects
     */
    public void setLayers(List<BlockLayer> layers) {
        this.layers = new ArrayList<>(layers);
    }

    /**
     * Adds a single {@code BlockLayer} to the list of layers.
     *
     * @param layer the {@code BlockLayer} to add
     */
    public void addLayer(BlockLayer layer) {
        this.layers.add(layer);
    }

    /**
     * Adds a collection of {@code BlockLayer} objects to the list of layers.
     *
     * @param layers the list of {@code BlockLayer} objects to add
     */
    public void addBlockLayers(List<BlockLayer> layers) {
        this.layers.addAll(layers);
    }

    /**
     * Retrieves the {@code BlockLayer} at the specified index.
     *
     * @param index the index of the {@code BlockLayer} to retrieve
     * @return the {@code BlockLayer} at the specified index
     */
    public BlockLayer get(int index) {
        if (index >= layers.size()) {
            if (repeatLayers) {
                index = index % layers.size();
            } else {
                index = layers.size() - 1;
            }
        }
        return this.layers.get(index);
    }

    /**
     * Removes and returns the {@code BlockLayer} at the specified index.
     *
     * @param index the index of the {@code BlockLayer} to remove
     * @return the removed {@code BlockLayer}
     */
    public BlockLayer remove(int index) {
        return this.layers.remove(index);
    }

    /**
     * Retrieves the first {@code BlockLayer} in the list.
     *
     * @return the first {@code BlockLayer}
     */
    public BlockLayer getFirstLayer() {
        return this.layers.getFirst();
    }

    /**
     * Removes and returns the first {@code BlockLayer} in the list.
     *
     * @return the removed first {@code BlockLayer}
     */
    public BlockLayer removeFirstLayer() {
        return this.layers.removeFirst();
    }

    /**
     * Retrieves the last {@code BlockLayer} in the list.
     *
     * @return the last {@code BlockLayer}
     */
    public BlockLayer getLastLayer() {
        return this.layers.get(size() - 1);
    }

    /**
     * Removes and returns the last {@code BlockLayer} in the list.
     *
     * @return the removed last {@code BlockLayer}
     */
    public BlockLayer removeLastLayer() {
        return this.layers.remove(size() - 1);
    }

    /**
     * Modifies the block states of the {@code BlockLayer} at the specified index.
     *
     * @param index  the index of the {@code BlockLayer} to modify
     * @param states the new list of {@code BlockState} objects
     */
    public void modifyBlocks(int index, List<BlockState> states) {
        this.layers.get(index).setBlockStates(states);
    }

    /**
     * Modifies the depth of the {@code BlockLayer} at the specified index.
     *
     * @param index the index of the {@code BlockLayer} to modify
     * @param depth the new depth value
     */
    public void modifyDepth(int index, int depth) {
        this.layers.get(index).setDepth(depth);
    }

    /**
     * Replaces the {@code BlockLayer} at the specified index with a new one.
     *
     * @param index the index of the {@code BlockLayer} to replace
     * @param layer the new {@code BlockLayer}
     */
    public void modifyLayer(int index, BlockLayer layer) {
        this.layers.set(index, layer);
    }

    /**
     * Returns the number of {@code BlockLayer} objects in the list.
     *
     * @return the size of the list
     */
    public int size() {
        return this.layers.size();
    }

    /**
     * Method to know if the BlockLayer List is empty
     *
     * @return true if it is empty, false if not
     */
    public boolean isEmpty() {
        return this.layers.isEmpty();
    }

    /**
     * method to know if the layers should repeat
     *
     * @return the boolean value
     */
    public boolean isRepeatLayers() {
        return repeatLayers;
    }

    /**
     * method to repeat the layer in the case the distance is bigger than the sum of all depth of all the {@link BlockLayer}
     *
     * @param repeatLayers boolean that manage if the layers have to be repeated
     */
    public void setRepeatLayers(boolean repeatLayers) {
        this.repeatLayers = repeatLayers;
    }
}