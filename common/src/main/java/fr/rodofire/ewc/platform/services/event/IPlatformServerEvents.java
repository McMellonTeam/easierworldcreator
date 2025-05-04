package fr.rodofire.ewc.platform.services.event;

import net.minecraft.server.level.ServerLevel;

import java.util.function.Consumer;

public interface IPlatformServerEvents {

    void onWorldLoad(Consumer<ServerLevel> callback);

    void onWorldUnload(Consumer<ServerLevel> callback);

}
