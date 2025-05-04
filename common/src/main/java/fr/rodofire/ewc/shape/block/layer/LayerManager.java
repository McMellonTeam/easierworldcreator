package fr.rodofire.ewc.shape.block.layer;

import fr.rodofire.ewc.blockdata.StructurePlacementRuleManager;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.blockdata.layer.BlockLayer;
import fr.rodofire.ewc.blockdata.layer.BlockLayerManager;
import fr.rodofire.ewc.util.LongPosHelper;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Class to assign BlockStates based on coordinates.
 * For that, we need a {@link BlockLayerManager}.
 * It allows us to define which blocks will go where.
 * This class will choose which block to assign depending on {@link Type}.
 * For example based on this shape:
 * <pre>
 *     {@code
 *     BlockLayer layer1, layer2, layer3
 *
 *     BlockLayerManager manager = new BlockLayerManager(
 *          List.of(layer1, layer2, layer3),
 *          List.of((short)1, (short)2, (short)5
 *     );
 *
 *     LayerManager layerManager = new LayerManager(Type.SURFACE, manager);
 *     }
 *     <br>
 *     The layer assignement will result on this:
 * </pre>
 * <pre>
 *       {@code
 *             * * *                  1 1 1
 *         * * * * * *            1 1 2 2 2 1
 *       * * * * * * *          1 2 2 2 2 2 2
 *       * * * * * * *      ->  2 2 2 3 3 3 2
 *       * * * * * * * *        2 3 3 3 3 3 3 1
 *       * * * * * * * *        3 3 3 3 3 3 3 2
 *       }
 *       </pre>
 * Then, after choosing the layer, we choose which block of the layer will be placed.
 * For more information, see {@link BlockLayer}
 */
public class LayerManager implements Layer {
    Type layerType;
    private final BlockLayerManager blockLayerManager;
    protected Vec3 centerPos = new Vec3(0, 0, 0);
    protected Vec3i directionVector = new Vec3i(0, 1, 0);

    public LayerManager(Type layerType, BlockLayerManager blockLayerManager) {
        this.layerType = layerType;
        this.blockLayerManager = blockLayerManager;
    }


    public LayerManager(Type layerType, BlockLayerManager blockLayerManager, Vec3 centerPos) {
        this.blockLayerManager = blockLayerManager;
        this.layerType = layerType;
        this.centerPos = centerPos;
    }

    public LayerManager(Type layerType, BlockLayerManager blockLayerManager, Vec3 centerPos, Vec3i directionVector) {
        this.layerType = layerType;
        this.blockLayerManager = blockLayerManager;
        this.centerPos = centerPos;
        this.directionVector = directionVector;
    }

    @Override
    public BlockListManager get(Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();

            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) ->
                                    manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler)
                    ))
            );
            return manager;
        }
        return getLayer().get(posMap);
    }

    @Override
    public void place(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> layer.getPlacer().place(world, states, LongPosHelper.decodeBlockPos(pos), ruler)
                    ))
            );
        }
        getLayer().place(world, posMap);
    }

    @Override
    public BlockListManager getVerified(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> {
                                if (ruler.canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                                    manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);

                            }
                    ))
            );
            return manager;
        }
        return getLayer().getVerified(world, posMap);
    }

    @Override
    public DividedBlockListManager getDivided(Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) ->
                                    manager.putWithoutVerification(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler)
                    ))
            );
            return manager;
        }
        return getLayer().getDivided(posMap);
    }

    @Override
    public DividedBlockListManager getVerifiedDivided(WorldGenLevel world, Map<ChunkPos, LongOpenHashSet> posMap) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posMap.forEach((
                    (chunkPos, longs) -> longs.forEach(
                            (pos) -> {
                                if (ruler.canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                                    manager.putWithoutVerification(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
                            }
                    ))
            );
            return manager;
        }
        return getLayer().getVerifiedDivided(world, posMap);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager get(T posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> manager.put(layer.getPlacer().get(states, pos), pos)));
            return manager;
        }
        return getLayer().get(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> void place(WorldGenLevel world, T posList) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> layer.getPlacer().place(world, states, pos, ruler)));
        }
        getLayer().place(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> BlockListManager getVerified(WorldGenLevel world, T posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> {
                if (ruler.canPlace(world.getBlockState(pos)))
                    manager.put(layer.getPlacer().get(states, pos), pos);
            }));
            return manager;
        }
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getDivided(T posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> manager.put(layer.getPlacer().get(states, pos), pos)));
            return manager;
        }
        return getLayer().getDivided(posList);
    }

    @Override
    public <T extends Collection<BlockPos>> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, T posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> {
                if (ruler.canPlace(world.getBlockState(pos)))
                    manager.put(layer.getPlacer().get(states, pos), pos);
            }));
            return manager;
        }
        return getLayer().getVerifiedDivided(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager get(U posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler)));
            return manager;
        }
        return getLayer().get(posList);
    }

    @Override
    public <U extends AbstractLongCollection> void place(WorldGenLevel world, U posList) {
        if (blockLayerManager.size() == 1) {
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> layer.getPlacer().place(world, states, LongPosHelper.decodeBlockPos(pos), ruler)));
        }
        getLayer().place(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> BlockListManager getVerified(WorldGenLevel world, U posList) {
        if (blockLayerManager.size() == 1) {
            BlockListManager manager = new BlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> {
                if (ruler.canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                    manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
            }));
            return manager;
        }
        return getLayer().getVerified(world, posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getDivided(U posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler)));
            return manager;
        }
        return getLayer().getDivided(posList);
    }

    @Override
    public <U extends AbstractLongCollection> DividedBlockListManager getVerifiedDivided(WorldGenLevel world, U posList) {
        if (blockLayerManager.size() == 1) {
            DividedBlockListManager manager = new DividedBlockListManager();
            BlockLayer layer = blockLayerManager.getFirstLayer();
            StructurePlacementRuleManager ruler = layer.getRuler();
            List<BlockState> states = layer.getBlockStates();
            posList.forEach(((pos) -> {
                if (ruler.canPlace(world.getBlockState(LongPosHelper.decodeBlockPos(pos))))
                    manager.put(layer.getPlacer().get(states, LongPosHelper.decodeBlockPos(pos)), pos, ruler);
            }));
            return manager;
        }
        return getLayer().getVerifiedDivided(world, posList);
    }

    @Override
    public Vec3 getCenterPos() {
        return centerPos;
    }

    @Override
    public void setCenterPos(Vec3 centerPos) {
        this.centerPos = centerPos;
    }

    @Override
    public Vec3 getDirectionVector() {
        return Vec3.atCenterOf(directionVector);
    }

    @Override
    public void setDirectionVector(Vec3i directionVector) {
        this.directionVector = directionVector;
    }


    private Layer getLayer() {
        return switch (layerType) {
            case SURFACE -> new SurfaceLayer(blockLayerManager);
            case INNER_RADIAL -> new InnerRadialLayer(blockLayerManager, centerPos);
            case OUTER_RADIAL -> new OuterRadialLayer(blockLayerManager, centerPos);
            case INNER_CYLINDRICAL -> new InnerCylindricalLayer(blockLayerManager, centerPos, directionVector);
            case OUTER_CYLINDRICAL -> new OuterCylindricalLayer(blockLayerManager, centerPos, directionVector);
            case ALONG_DIRECTION -> new DirectionalLayer(blockLayerManager, centerPos, directionVector);
        };
    }

    /**
     * change how the blocks are put
     */
    public enum Type {
        /**
         * for a natural aspect,
         * Put the first BlockStates on top of the structure for a coordinate x and z,
         * and until it reaches the depth of the layer
         */
        SURFACE,
        /**
         * place the blocks in a sphere shape, first layer being placed at the center
         */
        INNER_RADIAL,
        /**
         * place the blocks in a sphere shape, the last layer being placed at the center
         */
        OUTER_RADIAL,
        /**
         * place the blocks in a cylindrical shape, the first layer being placed at the center
         */
        INNER_CYLINDRICAL,
        /**
         * place the blocks in a cylindrical shape, the last layer being placed near the center
         */
        OUTER_CYLINDRICAL,
        /**
         * place the blocks on a plan
         * the plan is defined by the vector "directionalLayerDirection"
         */
        ALONG_DIRECTION
    }
}
