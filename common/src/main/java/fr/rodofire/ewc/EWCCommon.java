package fr.rodofire.ewc;

import fr.rodofire.ewc.command.ModCommands;
import fr.rodofire.ewc.config.ewc.EwcClientConfig;
import fr.rodofire.ewc.config.ewc.EwcConfig;
import fr.rodofire.ewc.maths.FastMaths;
import fr.rodofire.ewc.util.file.EwcFolderData;


public class EWCCommon {
    static boolean serverInit = false;

    public static void initServer() {
        if (serverInit)
            return;

        serverInit = true;
        EwcConstants.LOGGER.info("[EWC] Initializing :");
        EwcConfig.setConfig();
        FastMaths.registerMaths();
        EwcFolderData.initFiles();
        ModCommands.registerCommands();

        EwcConstants.LOGGER.info("[EWC] Started!");
    }

    public static void initClient() {
        EwcConstants.LOGGER.info("[EWC] Initializing Client:");
        initServer();
        EwcClientConfig.init();
        EwcConstants.LOGGER.info("[EWC] Started Client!");
    }
}
