package fr.rodofire.ewc;

import fr.rodofire.ewc.command.ModCommands;
import fr.rodofire.ewc.config.ewc.EwcClientConfig;
import fr.rodofire.ewc.config.ewc.EwcConfig;
import fr.rodofire.ewc.maths.FastMaths;
import fr.rodofire.ewc.util.file.EwcFolderData;

import static com.mojang.text2speech.Narrator.LOGGER;

public class EWCCommon {
    static boolean serverInit = false;

    public static void initServer() {
        if (serverInit)
            return;

        serverInit = true;
        LOGGER.info("[EWC] Initializing :");
        EwcConfig.setConfig();
        FastMaths.registerMaths();
        EwcFolderData.initFiles();
        ModCommands.registerCommands();

        LOGGER.info("[EWC] Started!");
    }

    public static void initClient() {
        LOGGER.info("[EWC] Initializing Client:");
        initServer();
        EwcClientConfig.init();
        LOGGER.info("[EWC] Started Client!");
    }
}
