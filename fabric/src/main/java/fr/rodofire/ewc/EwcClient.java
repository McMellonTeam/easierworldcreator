package fr.rodofire.ewc;

import net.fabricmc.api.ClientModInitializer;

public class EwcClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EWCCommon.initClient();
    }
}
