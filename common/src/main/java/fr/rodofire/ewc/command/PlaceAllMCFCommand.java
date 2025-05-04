package fr.rodofire.ewc.command;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import fr.rodofire.ewc.blockdata.blocklist.BlockListHelper;
import fr.rodofire.ewc.blockdata.blocklist.BlockListManager;
import fr.rodofire.ewc.util.ChunkUtil;
import fr.rodofire.ewc.util.file.EwcFolderData;
import fr.rodofire.ewc.util.file.FileUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class PlaceAllMCFCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext commandRegistryAccess,
                                Commands.CommandSelection registrationEnvironmen) {

        dispatcher.register(Commands.literal("placeallmultichunkfeatures")
                .executes(context -> run(context, false))
                .then(Commands.argument("legacy", BoolArgumentType.bool())
                        .executes(context -> run(context, BoolArgumentType.getBool(context, "legacy")))
                )
        );
    }

    public static int run(CommandContext<CommandSourceStack> context, boolean legacy) {
        Path path;

        if (legacy) path = EwcFolderData.getStructuresDirectory(context.getSource().getLevel());
        else path = EwcFolderData.Legacy.getLegacyStructureDir(context.getSource().getLevel());

        if (context.getSource().hasPermission(2)) {
            try (Stream<Path> paths = Files.list(path)) {
                paths.forEach(filePath -> {
                    String fileName = filePath.getFileName().toString();
                    Pattern pattern = Pattern.compile("chunk_(-?\\d+)_(-?\\d+)$");
                    Matcher matcher = pattern.matcher(fileName);
                    int chunkX;
                    int chunkZ;

                    if (matcher.matches()) {
                        chunkX = Integer.parseInt(matcher.group(1));
                        chunkZ = Integer.parseInt(matcher.group(2));
                    }
                    //initialize in the else because if not, intellij cries
                    else {
                        chunkZ = 0;
                        chunkX = 0;
                    }

                    if (ChunkUtil.isFeaturesGenerated(context.getSource().getLevel(), new ChunkPos(chunkX, chunkZ))) {
                        try (Stream<Path> files = Files.list(filePath)) {
                            files.forEach((jsonFiles) -> {
                                if (jsonFiles.toString().endsWith(".json")) {
                                    JsonArray jsonArray = new Gson().fromJson(FileUtil.loadJson(jsonFiles), JsonArray.class);
                                    BlockListManager manager = BlockListHelper.fromJson(context.getSource().getLevel(), jsonArray, new ChunkPos(chunkX, chunkZ));
                                    manager.placeAllNDelete(context.getSource().getLevel());
                                }
                                try {
                                    Files.delete(jsonFiles);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } catch (Exception e) {
                            e.fillInStackTrace();
                        }
                        try {
                            Files.delete(filePath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            } catch (Exception e) {
                e.fillInStackTrace();
            }
        }
        context.getSource().sendSuccess(() -> Component.translatable("multi_chunk_place"), true);

        return 1;
    }
}
