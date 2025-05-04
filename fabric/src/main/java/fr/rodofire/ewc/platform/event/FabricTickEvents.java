package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FabricTickEvents implements IPlatformTickEvents {
    private final List<Consumer<ServerLevel>> endWorldTick = new ArrayList<>();
    private final List<Consumer<MinecraftServer>> endServerTick = new ArrayList<>();

    public FabricTickEvents() {
        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            for (Consumer<MinecraftServer> callback : endServerTick) {
                callback.accept(server);
            }
        });

        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            for (Consumer<ServerLevel> callback : endWorldTick) {
                callback.accept(world);
            }
        });
    }

    @Override
    public void onEndWorldTick(Consumer<ServerLevel> world) {
        endWorldTick.add(world);
    }

    @Override
    public void onEndServerTick(Consumer<MinecraftServer> server) {
        endServerTick.add(server);
    }
}
