package fr.rodofire.ewc.platform.services;

import fr.rodofire.ewc.config.ModClientConfig;

public interface IPlatformConfigHelper {
    void putModId(String modId, ModClientConfig config);
}
