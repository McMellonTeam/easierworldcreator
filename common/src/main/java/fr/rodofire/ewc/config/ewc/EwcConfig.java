package fr.rodofire.ewc.config.ewc;


import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.config.ConfigCategory;
import fr.rodofire.ewc.config.ModConfig;
import fr.rodofire.ewc.config.objects.BooleanConfigObject;

@SuppressWarnings("unused")
public class EwcConfig {
    public static final ModConfig MOD_CONFIG = new ModConfig(EwcConstants.MOD_ID);
    static final ConfigCategory SERVER_CATEGORY = new ConfigCategory("server");
    static String SERVER = "server";


    public static void setConfig() {
        EwcConstants.LOGGER.info("|\t- Registering Config");
        BooleanConfigObject bool = new BooleanConfigObject(true, "performance_mode");
        bool.requireRestart = true;
        SERVER_CATEGORY.addBoolean(bool);

        BooleanConfigObject bool2 = new BooleanConfigObject(true, "multi_chunk_features");

        SERVER_CATEGORY.addBoolean(bool2);
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "chat_warns"));
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "log_warns"));
        SERVER_CATEGORY.addBoolean(new BooleanConfigObject(true, "log_performance_info"));

        /*IntegerConfigObject integerConfigObject = new IntegerConfigObject(1, 1, 4, "features_chunk_distance", "define how much chunks can be acced by feature generation");
        integerConfigObject.requireRestart = true;
        SERVER_CATEGORY.addInt(integerConfigObject);*/

        MOD_CONFIG.addCategories(SERVER_CATEGORY);

        MOD_CONFIG.init();
        initValues();
    }

    private static void initValues() {
    }

    public static boolean getPerformanceConfig() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("performance_mode").getActualValue();
    }

    public static boolean getMultiChunkFeatures() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("multi_chunk_features").getActualValue();
    }

    public static boolean getChatWarns() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("chat_warns").getActualValue();
    }

    public static boolean getLogWarns() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("log_warns").getActualValue();
    }

    public static boolean getLogPerformanceInfo() {
        return MOD_CONFIG.getCategory(SERVER).getBools().get("log_performance_info").getActualValue();
    }

    public static int getFeaturesChunkDistance() {
        /*if (MOD_CONFIG.isConfigProtected())
            return distance;
        distance = MOD_CONFIG.getCategory(SERVER).getInts().get("features_chunk_distance").getActualValue();
        return distance;*/
        return 1;
    }
}
