package fr.rodofire.ewc;

import net.fabricmc.api.DedicatedServerModInitializer;

public class Ewc implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        EWCCommon.initServer();
    }
}
