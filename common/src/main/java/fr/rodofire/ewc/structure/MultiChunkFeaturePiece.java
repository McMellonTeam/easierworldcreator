package fr.rodofire.ewc.structure;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import fr.rodofire.ewc.blockdata.blocklist.DividedBlockListManager;
import fr.rodofire.ewc.shape.block.MultiChunkFeaturesHandler;
import fr.rodofire.ewc.shape.block.layer.LayerManager;
import fr.rodofire.ewc.shape.block.placer.ShapePlacer;
import fr.rodofire.ewc.structure.config.StructureGeneratorConfig;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public abstract class MultiChunkFeaturePiece extends StructurePiece {
    protected ResourceLocation featureId;
    protected Set<ChunkPos> chunkPosSet = new HashSet<>();

    protected MultiChunkFeaturePiece(StructurePieceType type, int length, BoundingBox boundingBox, ResourceLocation featureId, Set<ChunkPos> chunkPosSet) {
        super(type, length, boundingBox);
        this.featureId = featureId;
        this.chunkPosSet = chunkPosSet;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context, CompoundTag nbt) {
        nbt.putString("structure_id", featureId.toString());
        nbt.putLongArray("chunkPos", chunkPosSet.stream()
                .mapToLong(ChunkPos::toLong)
                .toArray()
        );
    }

    protected <T extends StructureGeneratorConfig<T>> void writeGeneratorConfigCodec(CompoundTag nbt, Codec<T> codec, T config) {
        DynamicOps<Tag> ops = NbtOps.INSTANCE;
        DataResult<Tag> encoded = codec.encode(config, ops, ops.empty());
        encoded.result().ifPresent(element -> nbt.put("config", element));
    }

    protected <T extends StructureGeneratorConfig<T>> T getGeneratorConfig(CompoundTag nbt, Codec<T> codec) {
        return codec.parse(NbtOps.INSTANCE, nbt.get("config"))
                .result()
                .orElseThrow(() -> new IllegalStateException("Failed to decode config"));
    }

    public MultiChunkFeaturePiece(StructurePieceType pieceType, CompoundTag nbtCompound) {
        super(pieceType, nbtCompound);
        this.featureId = ResourceLocation.parse(nbtCompound.getString("structure_id"));
        long[] chunkPos = nbtCompound.getLongArray("chunkPos");
        Arrays.stream(chunkPos).forEach((longPos) -> this.chunkPosSet.add(new ChunkPos(longPos)));
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel world, @NotNull StructureManager structureManager, @NotNull ChunkGenerator generator, @NotNull RandomSource random, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        if (MultiChunkFeaturesHandler.isMultiChunkFeaturesGenerated(world, featureId)) return;
        MultiChunkFeaturesHandler.add(world, this.chunkPosSet, this.featureId);

        ShapePlacer shapePlacer = new ShapePlacer(world, ShapePlacer.PlaceMoment.WORLD_GEN, chunkPos.getMiddleBlockPosition(0));
        shapePlacer.setFeatureName(this.featureId);

        DividedBlockListManager dividedBlockListManager = getDividedStructure(world, structureManager, generator, random, box, chunkPos, pos);
        if (dividedBlockListManager != null) {
            shapePlacer.place(dividedBlockListManager);
        } else {
            Pair<Map<ChunkPos, LongOpenHashSet>, LayerManager> pair = getStructurePair(world, structureManager, generator, random, box, chunkPos, pos);
            if (pair != null && pair.getSecond() != null && pair.getFirst() != null) {
                shapePlacer.place(pair.getFirst(), pair.getSecond());
            }
        }
    }

    /**
     * Method to get the divided blockList manager. If you don't need to provide a DividedBlockListManager, you can use {@link MultiChunkFeaturePiece#getStructurePair(WorldGenLevel, StructureManager, ChunkGenerator, RandomSource, BoundingBox, ChunkPos, BlockPos)}
     */
    @Nullable
    public abstract DividedBlockListManager getDividedStructure(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot);

    @Nullable
    public abstract Pair<Map<ChunkPos, LongOpenHashSet>, LayerManager> getStructurePair(WorldGenLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot);

}
