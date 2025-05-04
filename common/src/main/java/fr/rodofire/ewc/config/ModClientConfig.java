package fr.rodofire.ewc.config;


import fr.rodofire.ewc.client.gui.screen.AbstractInfoScreen;
import fr.rodofire.ewc.config.objects.AbstractConfigObject;
import fr.rodofire.ewc.platform.Services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings("unused")
public class ModClientConfig {
    private final ModConfig config;
    private final Map<String, Map<AbstractConfigObject<?>, AbstractInfoScreen>> screens = new LinkedHashMap<>();

    public ModClientConfig(ModConfig config) {
        this.config = config;
    }

    public ModConfig getConfig() {
        return config;
    }

    public Map<String, Map<AbstractConfigObject<?>, AbstractInfoScreen>> getScreens() {
        return screens;
    }

    public Map<AbstractConfigObject<?>, AbstractInfoScreen> getCategoryScreens(String name) {
        return screens.get(name);
    }

    public Optional<AbstractInfoScreen> getScreen(String name, AbstractConfigObject<?> configObject) {
        return Optional.ofNullable(screens.get(name).get(configObject));
    }

    public AbstractInfoScreen put(String name, AbstractConfigObject<?> configObject, AbstractInfoScreen screen) {
        if (config.contains(name, configObject)) {
            screens.computeIfAbsent(name, key -> new HashMap<>());
            Map<AbstractConfigObject<?>, AbstractInfoScreen> nestedMap = screens.get(name);
            return nestedMap.put(configObject, screen);
        }
        return screen;
    }

    public void init() {
        Services.CONFIG.putModId(config.getMOD_ID(), this);
    }
}
