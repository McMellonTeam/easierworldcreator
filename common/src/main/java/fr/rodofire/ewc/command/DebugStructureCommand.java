package fr.rodofire.ewc.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fr.rodofire.ewc.mixin.world.structure.StructureTemplateManagerInvoker;
import fr.rodofire.ewc.platform.Services;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class DebugStructureCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext commandRegistryAccess,
                                Commands.CommandSelection registrationEnvironmen) {

        dispatcher.register(Commands.literal("debugstructures")
                .executes(commandContext -> {
                    Entity entity = commandContext.getSource().getEntity();
                    if (entity == null) return 0;
                    BlockPos pos = entity.getOnPos();
                    return DebugStructureCommand.runAll(commandContext, Services.PLATFORM.getModList(), pos);
                })
                .then(Commands.argument("mod", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            Set<String> mods = Services.PLATFORM.getModList();

                            for (String mod : mods) {
                                builder.suggest(mod);
                            }
                            return builder.buildFuture();
                        })
                        .executes(commandContext -> {
                            Entity entity = commandContext.getSource().getEntity();
                            if (entity == null) return 0;
                            BlockPos pos = entity.getOnPos();
                            return DebugStructureCommand.runAll(commandContext, Set.of(StringArgumentType.getString(commandContext, "mod")), pos);
                        })
                        .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                                .executes(commandContext -> DebugStructureCommand.runAll(commandContext, Set.of(StringArgumentType.getString(commandContext, "mod")), BlockPosArgument.getLoadedBlockPos(commandContext, "pos")))
                        )
                )
        );
    }

    public static int runAll(CommandContext<CommandSourceStack> context, Set<String> mod, BlockPos pos) throws CommandSyntaxException {
        StructureTemplateManager structureTemplateManager = context.getSource().getServer().getStructureManager();

        Map<ResourceLocation, StructureTemplate> templates = new LinkedHashMap<>();
        for (StructureTemplateManager.Source provider : ((StructureTemplateManagerInvoker) structureTemplateManager).getSources()) {
            try {
                Stream<ResourceLocation> structureIds = provider.lister().get();

                structureIds.forEach(id -> {
                    Optional<StructureTemplate> optionalTemplate = provider.loader().apply(id);
                    optionalTemplate.ifPresent(template -> templates.put(id, template));
                });
            } catch (Exception ignored) {
            }
        }

        int offsetZ = 0;
        int offsetX = 0;
        int maxX = 0;
        String lastFolder = Path.of(templates.keySet().iterator().next().getPath()).getParent().toString();
        for (Map.Entry<ResourceLocation, StructureTemplate> entry : templates.entrySet()) {
            StructureTemplate template = entry.getValue();


            BlockPos offset = new BlockPos(offsetX, 0, offsetZ);

            if (mod.contains(entry.getKey().getNamespace())) {
                template.placeInWorld(context.getSource().getLevel(), pos.offset(offset), new BlockPos(0, 0, 0), new StructurePlaceSettings(), context.getSource().getLevel().random, Block.UPDATE_CLIENTS);
                if (!Path.of(entry.getKey().getPath()).getParent().toString().equals(lastFolder)) {
                    offsetX += maxX + 3;
                    offsetZ = 0;
                    maxX = 0;
                    lastFolder = Path.of(entry.getKey().getPath()).getParent().toString();
                } else {
                    offsetZ += template.getSize().getZ() + 3;
                    maxX = Math.max(maxX, template.getSize().getX());
                }
            }
        }

        return 1;
    }
}
