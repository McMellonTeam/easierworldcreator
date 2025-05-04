package fr.rodofire.ewc.config;


import fr.rodofire.ewc.config.objects.AbstractConfigObject;
import fr.rodofire.ewc.config.objects.BooleanConfigObject;
import fr.rodofire.ewc.config.objects.EnumConfigObject;
import fr.rodofire.ewc.config.objects.IntegerConfigObject;
import fr.rodofire.ewc.platform.Services;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class ModConfig {
    boolean protectedConfig = false;
    private final String MOD_ID;
    Map<String, ConfigCategory> categories = new LinkedHashMap<>();

    public ModConfig(String modID) {
        this.MOD_ID = modID;
    }

    public boolean isConfigProtected() {
        return protectedConfig;
    }

    public void addCategory(ConfigCategory category) {
        categories.put(category.getName(), category);
    }

    public void addCategory(Set<ConfigCategory> categories) {
        this.categories.putAll(categories.stream().collect(Collectors.toMap(ConfigCategory::getName, c -> c)));
    }

    public void addCategories(ConfigCategory... categories) {
        // Convertir les catégories en une map temporaire
        Map<String, ConfigCategory> categoryMap = Arrays.stream(categories)
                .collect(Collectors.toMap(ConfigCategory::getName, category -> category));

        // Ajouter toutes les catégories à la map existante
        this.categories.putAll(categoryMap);
    }


    public String getMOD_ID() {
        return MOD_ID;
    }

    /**
     * method to get a category of the config
     *
     * @param name the name of the category
     * @return the category related to the name
     */
    public ConfigCategory getCategory(String name) {
        ConfigCategory cat = categories.get(name);
        if (cat == null) {
            return null;
        }
        if (!protectedConfig) {
            ReadableConfig reader = new ReadableConfig(MOD_ID);
            reader.refresh(cat);
        }
        return cat;
    }

    public ConfigCategory getTemporaryCategory(String name) {
        return categories.get(name);
    }

    public Set<ConfigCategory> getCategories() {
        return new LinkedHashSet<>(categories.values());
    }

    public void refreshValues() {
        ReadableConfig reader = new ReadableConfig(MOD_ID);
        for (ConfigCategory cat : categories.values()) {
            reader.refresh(cat);
        }
    }

    public void apply(ConfigCategory category) {
        String name = category.getName();
        categories.put(name, category);
    }

    public void save() {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory cat : categories.values()) {
            WritableConfig writer = new WritableConfig(MOD_ID, cat);
            writer.write(path.resolve(cat.getName() + ".toml"));
        }
    }

    public <T extends AbstractConfigObject<U>, U> void updateValues(String name) {
        WritableConfig writer = new WritableConfig(MOD_ID, getCategory(name));
        writer.repairConfig(getCategoryPath(name));
    }

    public boolean shouldRestart() {
        return this.categories.values().stream().anyMatch(
                configCategory -> configCategory.getBools().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        )
                || this.categories.values().stream().anyMatch(
                configCategory -> configCategory.getInts().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        )
                || this.categories.values().stream().anyMatch(
                configCategory -> configCategory.getEnums().values().stream().anyMatch(AbstractConfigObject::shouldRestart)
        );
    }

    public void init() {
        List<ConfigCategory> caterories = shouldWrite();
        if (!caterories.isEmpty()) {
            writeConfigs(caterories);
        }
        repair();
        refreshValues();
        Services.PlatformEvents.SERVER.onWorldLoad(serverLevel -> this.protectedConfig = true);
        Services.PlatformEvents.SERVER.onWorldUnload((world) -> this.protectedConfig = false);
    }

    public Path getCategoryPath(String name) {
        return ConfigUtil.getConfigPath(MOD_ID).resolve(getCategory(name).getName() + ".toml");
    }

    private void repair() {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory category : categories.values()) {
            WritableConfig writableConfig = new WritableConfig(MOD_ID, category);
            writableConfig.repairConfig(path.resolve(category.getName() + ".toml"));
        }
    }

    private List<ConfigCategory> shouldWrite() {
        List<ConfigCategory> configCaterories = new ArrayList<>();
        Path basePath = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory cat : categories.values()) {
            Path path = basePath.resolve(cat.getName() + ".toml");
            if (!path.toFile().exists()) {
                configCaterories.add(cat);
            }
        }
        return configCaterories;
    }

    private void writeConfigs(List<ConfigCategory> categories) {
        Path path = ConfigUtil.getConfigPath(MOD_ID);
        for (ConfigCategory category : categories) {
            WritableConfig wc = new WritableConfig(this.MOD_ID, category);
            wc.write(path.resolve(category.getName() + ".toml"));
        }
    }

    public ModConfig copy() {
        ModConfig config = new ModConfig(MOD_ID);
        config.categories = new LinkedHashMap<>(categories);
        config.protectedConfig = this.protectedConfig;
        return config;
    }

    public boolean contains(String name) {
        return categories.containsKey(name);
    }

    public <T extends AbstractConfigObject<U>, U> boolean contains(String name, T category) {
        boolean bl = false;
        if (categories.containsKey(name)) {
            if (category instanceof BooleanConfigObject && categories.get(name).getBools().containsValue(category))
                return true;
            if (category instanceof EnumConfigObject && categories.get(name).getEnums().containsValue(category))
                return true;
            return category instanceof IntegerConfigObject && categories.get(name).getInts().containsValue(category);
        }
        return false;
    }

    public void apply(ModConfig config) {
        this.protectedConfig = config.protectedConfig;
        this.categories = config.categories;
    }

    public boolean equals(ModConfig obj) {
        if (categories.size() != obj.categories.size()) {
            return false;
        }
        Iterator<ConfigCategory> catIt = categories.values().iterator();
        Iterator<ConfigCategory> catIt2 = obj.categories.values().iterator();
        for (int i = 0; i < categories.size(); i++) {
            ConfigCategory cat = catIt.next();
            ConfigCategory cat2 = catIt2.next();
            if (!cat.equals(cat2)) return false;
        }
        boolean bl = true;
        return Objects.equals(obj.MOD_ID, this.MOD_ID);
    }
}
