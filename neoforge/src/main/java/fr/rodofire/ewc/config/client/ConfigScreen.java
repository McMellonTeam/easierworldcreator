package fr.rodofire.ewc.config.client;

import com.mojang.datafixers.util.Pair;
import fr.rodofire.ewc.config.ModClientConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ConfigScreen {
    private static final Map<String, Screen> screen = new HashMap<>();
    private static final Map<String, ModClientConfig> modId = new HashMap<>();
    private static final Map<String, Pair<ResourceLocation, Pair<Integer, Integer>>> backgrounds = new HashMap<>();
    private static final Map<String, Pair<Integer, Integer>> backgroundsShader = new HashMap<>();

    public static Screen getScreen(Screen parent, String modId) {
        if (screen.containsKey(modId)) {
            return screen.get(modId);
        }
        if (backgrounds.containsKey(modId) && backgroundsShader.containsKey(modId)) {
            return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId), backgrounds.get(modId).getFirst(), backgrounds.get(modId).getSecond().getFirst(), backgrounds.get(modId).getSecond().getSecond(), backgroundsShader.get(modId).getFirst(), backgroundsShader.get(modId).getSecond());
        } else if (backgrounds.containsKey(modId)) {
            return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId), backgrounds.get(modId).getFirst(), backgrounds.get(modId).getSecond().getFirst(), backgrounds.get(modId).getSecond().getSecond());
        }
        return getDefaultScreen(parent, modId, ConfigScreen.modId.get(modId));
    }

    public static void setScreen(Screen screen, String modId) {
        ConfigScreen.screen.put(modId, screen);
    }


    public static Map<String, Screen> getScreenMap() {
        return screen;
    }

    public static void putModId(String modId, ModClientConfig config) {
        ConfigScreen.modId.put(modId, config);
    }

    public static void setDefaultScreen(Screen parent, String modId, ModClientConfig modConfig) {
        if (screen.containsKey(modId)) {
            return;
        }

        DefaultConfigScreen screen1 = new DefaultConfigScreen(parent, modConfig, modId);
        screen.put(modId, screen1);
    }

    public static void setBackgroundScreen(String modId, ResourceLocation image, int width, int height) {
        if (backgrounds.containsKey(modId)) {
            return;
        }
        backgrounds.put(modId, new Pair<>(image, new Pair<>(width, height)));
    }

    public static void setBackgroundScreen(String modId, ResourceLocation image, int width, int height, int backgroundShader, int darkRectangleShader) {
        setBackgroundScreen(modId, image, width, height);
        if (backgroundsShader.containsKey(modId)) {
            return;
        }
        ConfigScreen.backgroundsShader.put(modId, new Pair<>(backgroundShader, darkRectangleShader));
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig) {
        return new DefaultConfigScreen(parent, modConfig, modId);
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig, ResourceLocation image, int width, int height) {
        return new DefaultConfigScreen(parent, modConfig, modId, image, width, height);
    }

    public static Screen getDefaultScreen(Screen parent, String modId, ModClientConfig modConfig, ResourceLocation image, int width, int height, int backgroundShaderColor, int darkbackgroundShaderColor) {
        return new DefaultConfigScreen(parent, modConfig, modId, image, width, height, backgroundShaderColor, darkbackgroundShaderColor);
    }


}
