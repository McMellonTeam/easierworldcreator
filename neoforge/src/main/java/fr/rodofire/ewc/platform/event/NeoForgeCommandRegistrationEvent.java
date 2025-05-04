package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformCommandRegistrationEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeCommandRegistrationEvent implements IPlatformCommandRegistrationEvents {
    private static final List<CommandRegistration> commands = new ArrayList<>();

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        var dispatcher = event.getDispatcher();
        CommandBuildContext context = event.getBuildContext();
        Commands.CommandSelection environment = event.getCommandSelection();

        for (CommandRegistration command : commands) {
            command.onRegistration(dispatcher, context, environment);
        }
    }


    @Override
    public void register(CommandRegistration registration) {
        commands.add(registration);
    }
}
