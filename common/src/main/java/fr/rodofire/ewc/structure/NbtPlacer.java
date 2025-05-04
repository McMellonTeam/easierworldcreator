package fr.rodofire.ewc.structure;


import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.shape.block.placer.animator.StructurePlaceAnimator;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Optional;

/**
 * Class to place nbt structures in the world. To use a NbtPlacer, you can use this code:
 * <pre>{@code NbtPlacer placer = new NbtPlacer(world, new ResourceLocation("Path_of_your_structure"));
 * placer.place(pos)
 * }</pre>
 */
@SuppressWarnings("unused")
public class NbtPlacer {
    private final WorldGenLevel world;
    private final ResourceLocation templateName;
    private StructurePlaceAnimator animator;

    /**
     * init a NbtPlacer
     *
     * @param world        the world the structure will spawn in
     * @param templateName the ResourceLocation of the structure
     */
    public NbtPlacer(WorldGenLevel world, ResourceLocation templateName) {
        this.world = world;
        this.templateName = templateName;
    }

    /**
     * init a NbtPlacer
     *
     * @param world        the world the structure will spawn in
     * @param templateName the ResourceLocation of the structure
     * @param animator     the animation of the structure place
     */
    public NbtPlacer(WorldGenLevel world, ResourceLocation templateName, StructurePlaceAnimator animator) {
        this.world = world;
        this.templateName = templateName;
        this.animator = animator;
    }

    /**
     * method to get the animator of the object
     *
     * @return the animator of the object
     */
    public StructurePlaceAnimator getAnimator() {
        return animator;
    }

    /**
     * method to change the animator
     *
     * @param animator the animator that will be changed
     */
    public void setAnimator(StructurePlaceAnimator animator) {
        this.animator = animator;
    }


    /**
     * this method allows you to place a structure in the world during world gen or not
     *
     * @param pos the pos of the structure
     */
    public void place(BlockPos pos) {
        this.place(1.0f, pos, new BlockPos(0, 0, 0), Mirror.NONE, Rotation.NONE, true);
    }

    /**
     * this method allows you to place a structure in the world during world gen or not
     *
     * @param integrity      the integrity of the structure must be between 0f and 1f
     * @param pos            the pos of the structure
     * @param offset         the offset of the pos structure
     * @param mirror         the block mirror if wanted
     * @param rotation       the structure rotation if wanted
     * @param ignoreEntities ignore entities of the structure
     */
    public void place(float integrity, BlockPos pos, BlockPos offset, Mirror mirror, Rotation rotation, boolean ignoreEntities) {
        Pair<StructureTemplate, StructurePlaceSettings> data = processCommon(integrity, mirror, rotation, ignoreEntities);

        if (data != null) {
            data.getFirst().placeInWorld(world, pos, offset, data.getSecond(), world.getRandom(), 3);
        }
    }

    /**
     * method to get a {@link BlockListManager} based on a structure
     *
     * @param integrity      the integrity of the structure must be between 0f and 1f
     * @param pos            the pos of the structure
     * @param offset         the offset of the pos structure
     * @param mirror         the block mirror if wanted
     * @param rotation       the structure rotation if wanted
     * @param ignoreEntities ignore entities of the structure
     * @return the Structure converted into {@link BlockListManager}
     */
    public BlockListManager get(float integrity, BlockPos pos, BlockPos offset, Mirror mirror, Rotation rotation, boolean ignoreEntities) {
        BlockListManager manager = new BlockListManager();
        Pair<StructureTemplate, StructurePlaceSettings> data = processCommon(integrity, mirror, rotation, ignoreEntities);

        if (data != null) {
            StructureUtil.convertNbtToManager(data.getFirst(), manager, data.getSecond(), world, offset);
        }
        return manager;
    }


    private Pair<StructureTemplate, StructurePlaceSettings> processCommon(float integrity, Mirror mirror, Rotation rotation, boolean ignoreEntities) {
        MinecraftServer server = world.getServer();
        if (server == null) {
            EwcConstants.LOGGER.error("cannot get structure template, MinecraftServer is null. Structure template: {}", templateName);
            return null;
        }
        StructureTemplateManager structureTemplateManager = server.getStructureManager();
        if (structureTemplateManager == null) return null;

        Optional<StructureTemplate> optional;
        optional = structureTemplateManager.get(templateName);

        if (optional.isEmpty()) return null;

        StructurePlaceSettings structurePlacementData = new StructurePlaceSettings()
                .setMirror(mirror)
                .setRotation(rotation)
                .setIgnoreEntities(ignoreEntities);

        if (integrity < 1.0F) {
            structurePlacementData.clearProcessors()
                    .addProcessor(new BlockRotProcessor(Mth.clamp(integrity, 0.0F, 1.0F)))
                    .setRandom(StructureBlockEntity.createRandom(world.getSeed()));
        }

        return new Pair<>(optional.get(), structurePlacementData);
    }

}