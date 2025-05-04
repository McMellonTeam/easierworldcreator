package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformServerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FabricServerEvents implements IPlatformServerEvents {

    private final List<Consumer<ServerLevel>> loadCallbacks = new ArrayList<>();
    private final List<Consumer<ServerLevel>> unloadCallbacks = new ArrayList<>();

    public FabricServerEvents() {
        ServerWorldEvents.LOAD.register((server, world) -> {
            for (Consumer<ServerLevel> callback : loadCallbacks) {
                callback.accept(world);
            }
        });

        ServerWorldEvents.UNLOAD.register((minecraftServer, world) -> {
            for (Consumer<ServerLevel> callback : unloadCallbacks) {
                callback.accept(world);
            }
        });
    }

    @Override
    public void onWorldLoad(Consumer<ServerLevel> callback) {
        loadCallbacks.add(callback);
    }

    @Override
    public void onWorldUnload(Consumer<ServerLevel> callback) {
        unloadCallbacks.add(callback);
    }
}
