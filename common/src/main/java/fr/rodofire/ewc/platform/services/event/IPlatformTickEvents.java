package fr.rodofire.ewc.platform.services.event;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

public interface IPlatformTickEvents {

    void onEndWorldTick(Consumer<ServerLevel> world);

    void onEndServerTick(Consumer<MinecraftServer> world);

}
