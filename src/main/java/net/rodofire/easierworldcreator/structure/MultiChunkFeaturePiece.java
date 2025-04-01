package net.rodofire.easierworldcreator.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.rodofire.easierworldcreator.blockdata.blocklist.DividedBlockListManager;
import net.rodofire.easierworldcreator.shape.block.MultiChunkFeaturesHandler;
import net.rodofire.easierworldcreator.shape.block.layer.LayerManager;
import net.rodofire.easierworldcreator.shape.block.placer.ShapePlacer;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class MultiChunkFeaturePiece extends StructurePiece {
    protected Identifier featureId;
    protected Set<ChunkPos> chunkPosSet = new HashSet<>();

    protected MultiChunkFeaturePiece(StructurePieceType type, int length, BlockBox boundingBox, Identifier featureId, Set<ChunkPos> chunkPosSet) {
        super(type, length, boundingBox);
        this.featureId = featureId;
        this.chunkPosSet = chunkPosSet;
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putString("structure_id", featureId.toString());
        nbt.putLongArray("chunkPos", chunkPosSet.stream()
                .mapToLong(ChunkPos::toLong)
                .toArray()
        );
    }

    public MultiChunkFeaturePiece(StructurePieceType pieceType, NbtCompound nbtCompound) {
        super(pieceType, nbtCompound);
        this.featureId = Identifier.of(nbtCompound.getString("structure_id"));
        long[] chunkPos = nbtCompound.getLongArray("chunkPos");
        Arrays.stream(chunkPos).forEach((longPos) -> this.chunkPosSet.add(new ChunkPos(longPos)));
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        if (MultiChunkFeaturesHandler.isMultiChunkFeaturesGenerated(world, featureId)) return;
        MultiChunkFeaturesHandler.add(world, this.chunkPosSet, this.featureId);

        ShapePlacer shapePlacer = new ShapePlacer(world, ShapePlacer.PlaceMoment.WORLD_GEN, chunkPos.getCenterAtY(0));
        shapePlacer.setFeatureName(this.featureId);

        DividedBlockListManager dividedBlockListManager = getDividedStructure(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
        if (dividedBlockListManager != null) {
            shapePlacer.place(dividedBlockListManager);
        } else {
            Pair<Map<ChunkPos, LongOpenHashSet>, LayerManager> pair = getStructurePair(world, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, pivot);
            if (pair != null && pair.getLeft() != null && pair.getRight() != null) {
                shapePlacer.place(pair.getLeft(), pair.getRight());
            }
        }
    }

    /**
     * Method to get the divided blockList manager. If you don't need to provide a DividedBlockListManager, you can use {@link MultiChunkFeaturePiece#getStructurePair(StructureWorldAccess, StructureAccessor, ChunkGenerator, Random, BlockBox, ChunkPos, BlockPos)}
     */
    @Nullable
    public abstract DividedBlockListManager getDividedStructure(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot);

    @Nullable
    public abstract Pair<Map<ChunkPos, LongOpenHashSet>, LayerManager> getStructurePair(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot);

}
