package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformCommandRegistrationEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

import java.util.ArrayList;
import java.util.List;

public class FabricCommandRegistrationEvent implements IPlatformCommandRegistrationEvents {
    private final List<CommandRegistration> commands = new ArrayList<>();

    public FabricCommandRegistrationEvent() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandBuildContext, commandSelection) ->
                commands.forEach(command ->
                        command.onRegistration(commandDispatcher, commandBuildContext, commandSelection)));
    }

    @Override
    public void register(CommandRegistration registration) {
        commands.add(registration);
    }
}
