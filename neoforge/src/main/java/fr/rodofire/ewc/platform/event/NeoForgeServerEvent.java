package fr.rodofire.ewc.platform.event;

import fr.rodofire.ewc.platform.services.event.IPlatformServerEvents;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class NeoForgeServerEvent implements IPlatformServerEvents {

    private static final List<Consumer<ServerLevel>> loadCallbacks = new ArrayList<>();
    private static final List<Consumer<ServerLevel>> unloadCallbacks = new ArrayList<>();

    public NeoForgeServerEvent() {
    }

    @Override
    public void onWorldLoad(Consumer<ServerLevel> callback) {
        loadCallbacks.add(callback);
    }

    @Override
    public void onWorldUnload(Consumer<ServerLevel> callback) {
        unloadCallbacks.add(callback);
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel level) {
            for (Consumer<ServerLevel> callback : loadCallbacks) {
                callback.accept(level);
            }
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            for (Consumer<ServerLevel> callback : unloadCallbacks) {
                callback.accept(level);
            }
        }
    }
}
