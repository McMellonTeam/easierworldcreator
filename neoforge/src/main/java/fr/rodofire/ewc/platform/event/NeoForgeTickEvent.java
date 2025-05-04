package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeTickEvent implements IPlatformTickEvents {
    private static final List<Consumer<ServerLevel>> endWorldTickCallbacks = new ArrayList<>();
    private static final List<Consumer<MinecraftServer>> endServerTickCallbacks = new ArrayList<>();

    @SubscribeEvent
    public static void onWorldLoad(ServerTickEvent.Post event) {
        for (ServerLevel serverLevel : event.getServer().getAllLevels()) {
            for (Consumer<ServerLevel> callback : endWorldTickCallbacks) {
                callback.accept(serverLevel);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(ServerTickEvent.Post event) {
        for (Consumer<MinecraftServer> callback : endServerTickCallbacks) {
            callback.accept(event.getServer());
        }
    }

    @Override
    public void onEndWorldTick(Consumer<ServerLevel> world) {
        endWorldTickCallbacks.add(world);
    }

    @Override
    public void onEndServerTick(Consumer<MinecraftServer> world) {
        endServerTickCallbacks.add(world);
    }
}
