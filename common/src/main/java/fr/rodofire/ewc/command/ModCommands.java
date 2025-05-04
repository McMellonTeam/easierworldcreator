package fr.rodofire.ewc.command;


import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.platform.Services;

public class ModCommands {
    public static void registerCommands() {
        EwcConstants.LOGGER.info("|\t- Registering Commands");
        Services.PlatformEvents.COMMAND.register(DebugStructureCommand::register);
        Services.PlatformEvents.COMMAND.register(DebugStructureCommand::register);
    }
}
