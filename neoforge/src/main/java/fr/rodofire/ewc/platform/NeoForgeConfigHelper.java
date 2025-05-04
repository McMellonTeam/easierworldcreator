package fr.rodofire.ewc.platform;

import fr.rodofire.ewc.config.ModClientConfig;
import fr.rodofire.ewc.config.client.ConfigScreen;
import fr.rodofire.ewc.platform.services.IPlatformConfigHelper;

public class NeoForgeConfigHelper implements IPlatformConfigHelper {


    @Override
    public void putModId(String modId, ModClientConfig config) {
        ConfigScreen.putModId(modId, config);
    }
}
