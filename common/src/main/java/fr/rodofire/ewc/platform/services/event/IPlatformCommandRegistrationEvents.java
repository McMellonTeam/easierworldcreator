package fr.rodofire.ewc.platform.services.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public interface IPlatformCommandRegistrationEvents {
    void register(CommandRegistration registration);

    @FunctionalInterface
    interface CommandRegistration {
        void onRegistration(CommandDispatcher<CommandSourceStack> dispatcher,
                            CommandBuildContext commandRegistryAccess,
                            Commands.CommandSelection registrationEnvironmen);
    }
}
