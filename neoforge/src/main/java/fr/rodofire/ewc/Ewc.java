package fr.rodofire.ewc;


import fr.rodofire.ewc.config.client.ConfigScreen;
import fr.rodofire.ewc.config.ewc.screen.EwcConfigScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(EwcConstants.MOD_ID)
public class Ewc {

    public Ewc(IEventBus eventBus) {
        EwcConstants.LOGGER.info("Hello NeoForge world!");
        EWCCommon.initServer();
    }

    @EventBusSubscriber(modid = EwcConstants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class MidnightLibBusEvents {
        @SubscribeEvent
        public static void onPostInit(FMLClientSetupEvent event) {
            ModList.get().forEachModContainer((modid, modContainer) -> {
                if (modid.equals(EwcConstants.MOD_ID)) {
                    modContainer.registerExtensionPoint(IConfigScreenFactory.class, (minecraftClient, screen) -> new EwcConfigScreen(screen));
                }
                else if(ConfigScreen.getScreenMap().containsKey(modid)){
                    modContainer.registerExtensionPoint(IConfigScreenFactory.class, (minecraftClient, screen) -> ConfigScreen.getScreen(screen, modid));
                }
            });
            EWCCommon.initServer();
        }
    }
}