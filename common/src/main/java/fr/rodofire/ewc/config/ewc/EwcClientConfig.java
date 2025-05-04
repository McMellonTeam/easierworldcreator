package fr.rodofire.ewc.config.ewc;


import fr.rodofire.ewc.config.ModClientConfig;
import fr.rodofire.ewc.config.ewc.screen.MultiChunkInfoScreen;

public class EwcClientConfig {
    public static final ModClientConfig CLIENT_CONFIG = new ModClientConfig(EwcConfig.MOD_CONFIG);

    public static void init() {
        CLIENT_CONFIG.put("server", CLIENT_CONFIG.getConfig().getCategory("server").getBools().get("multi_chunk_features"), new MultiChunkInfoScreen());
    }
}
