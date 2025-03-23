package net.rodofire.ewc_test.devtest;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;
import net.rodofire.easierworldcreator.blockdata.layer.BlockLayerManager;
import net.rodofire.easierworldcreator.shape.block.gen.TorusGen;
import net.rodofire.easierworldcreator.shape.block.instanciator.AbstractFillableBlockShape;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.LayerPlacer;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import net.rodofire.easierworldcreator.shape.block.rotations.Rotator;

public class TorusGenTest extends Feature<DefaultFeatureConfig> {
    public TorusGenTest(Codec<DefaultFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(FeatureContext<DefaultFeatureConfig> context) {
        BlockPos center = context.getOrigin();
        StructureWorldAccess worldAccess = context.getWorld();

        TorusGen torus = new TorusGen(center, 3, 40);
        torus.setOuterRadiusZ(20);
        torus.setFillingType(AbstractFillableBlockShape.Type.EMPTY);
        torus.setRotator(new Rotator(center, 20, 50));

        new ShapePlacer(worldAccess, ShapePlacer.PlaceMoment.OTHER, center).place(torus.getShapeCoordinates(), new LayerManager(LayerManager.Type.SURFACE, new BlockLayerManager(new LayerPlacer(LayerPlacer.PlacingType.RANDOM), Blocks.REDSTONE_BLOCK.getDefaultState())));

        LongOpenHashSet covered = torus.getCoveredChunks();
        for(long compactChunkPos : covered) {
            ChunkPos chunkPos = new ChunkPos(compactChunkPos);
            for(int i = 0; i<16; i++){
                for(int j = 0; j<16; j++){
                    worldAccess.setBlockState(chunkPos.getStartPos().add(i,-60,j ), Blocks.YELLOW_CONCRETE.getDefaultState(), 3);
                }
            }
        }

        return true;
    }
}
