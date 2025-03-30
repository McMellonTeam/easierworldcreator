package net.rodofire.easierworldcreator.config.client;

import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.client.gui.screen.AbstractInfoScreen;
import net.rodofire.easierworldcreator.client.gui.screen.BackgroundScreen;
import net.rodofire.easierworldcreator.client.gui.widget.AbstractEntryWidget;
import net.rodofire.easierworldcreator.client.gui.widget.ImageButtonWidget;
import net.rodofire.easierworldcreator.client.gui.widget.InfoButtonWidget;
import net.rodofire.easierworldcreator.client.gui.widget.TextButtonWidget;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModClientConfig;
import net.rodofire.easierworldcreator.config.ModConfig;
import net.rodofire.easierworldcreator.config.objects.AbstractConfigObject;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;
import org.spongepowered.include.com.google.common.collect.BiMap;
import org.spongepowered.include.com.google.common.collect.HashBiMap;

import java.util.Map;
import java.util.Set;

public abstract class AbstractConfigScreen extends BackgroundScreen {
    protected int selected = 0;

    protected final String modId;

    ModClientConfig clientConfig;
    private final ModConfig config;
    private final ModConfig copy;

    Set<ConfigCategory> categories;
    ConfigCategory category;
    protected final BiMap<String, Integer> indexes = HashBiMap.create();

    protected AbstractConfigScreen(ModClientConfig config, String modId) {
        super(Text.translatable("config.screen." + modId + ".title"));
        this.clientConfig = config;
        this.modId = modId;
        this.config = config.getConfig();
        this.copy = config.getConfig().copy();
        this.categories = config.getConfig().getCategories();
        this.initIndexes();
        renitCategory();
    }

    public AbstractConfigScreen(Identifier background, int backgroundWidth, int backgroundHeight, ModClientConfig config, String modId) {
        super(Text.translatable("config.screen." + modId + ".title"), background, backgroundWidth, backgroundHeight);
        this.clientConfig = config;
        this.config = config.getConfig();
        this.modId = modId;
        this.copy = config.getConfig().copy();
        this.categories = config.getConfig().getCategories();
        this.initIndexes();
        renitCategory();
    }

    public AbstractConfigScreen(Identifier background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor, ModClientConfig config, String modId) {
        super(Text.translatable("config.screen." + modId + ".title"), background, backgroundWidth, backgroundHeight, backgroundShaderColor);
        this.clientConfig = config;
        this.config = config.getConfig();
        this.modId = modId;
        this.copy = config.getConfig().copy();
        this.categories = config.getConfig().getCategories();
        this.initIndexes();
        renitCategory();
    }

    @Override
    protected void init() {
        copy.apply(category);
        renitCategory();
        this.init(category);
    }

    private void renitCategory() {
        category = copy.getTemporaryCategory(indexes.inverse().get(selected));
    }

    protected abstract void init(ConfigCategory category);

    public <T extends AbstractConfigObject<U>, U> ImageButtonWidget addResetButton(int startX, int yOffset, T obj) {

        ImageButtonWidget buttonWidget = new ImageButtonWidget(startX, yOffset, 20, 20, Identifier.of(Ewc.MOD_ID, "textures/gui/reset_button.png"), press -> reset(obj));

        buttonWidget.setTooltip(Tooltip.of(Text.translatable("config.ewc.reset")));
        return buttonWidget;
    }

    public <T extends AbstractInfoScreen> InfoButtonWidget addInfoButton(int startX, int yOffset, T obj) {
        return new InfoButtonWidget(startX, yOffset, 20, 20, obj);
    }

    private void initIndexes() {
        int i = 0;
        for (ConfigCategory category : categories) {
            indexes.put(category.getName(), i);
            i++;
        }
    }

    protected boolean hasInfoScreen(AbstractConfigObject<?> category) {
        Map<AbstractConfigObject<?>, AbstractInfoScreen> a = clientConfig.getCategoryScreens(this.category.getName());
        return a != null && a.containsKey(category);
    }

    protected AbstractInfoScreen getInfoScreen(AbstractConfigObject<?> category) {
        return clientConfig.getCategoryScreens(this.category.getName()).get(category);
    }

    protected void toggleBoolean(BooleanConfigObject configObject, ButtonWidget button) {
        boolean newValue = !configObject.getActualValue();
        configObject.setActualValue(newValue);
        button.setMessage(Text.translatable("config.ewc.boolean." + configObject.getActualValue()));
        if (button instanceof TextButtonWidget textButtonWidget) {
            textButtonWidget.setColor(newValue ? 0x00FF00 : 0xFF0000);
        }
    }

    /**
     * method to change the actual value of an enum to the next value
     * @param configObject the enum that will be cycled
     * @param button the button on which the enum appears
     */
    public void cycleEnum(EnumConfigObject configObject, ButtonWidget button) {
        configObject.setActualValue(configObject.getNext());
        button.setMessage(Text.translatable("config." + modId + "." + configObject.getActualValue()));
    }

    /**
     * Method to verify that the input is an integer and manage the addition of "-".
     * It also manage the color of the text
     * @param configObject the config object that will be verified
     * @param button the entry that is used
     * @param cgr the text
     */
    protected void verifyInteger(IntegerConfigObject configObject, AbstractEntryWidget button, String cgr) {
        if (cgr.isEmpty()) return;
        if (cgr.equals("-")) {
            button.setEditableColor(0xFF0000);
            return;
        }
        if (!configObject.isAcceptable(Integer.parseInt(cgr))) {
            button.setEditableColor(0xFF0000);
        } else {
            button.setEditableColor(0xFFFFFF);
            configObject.setActualValue(Integer.parseInt(cgr));
        }
    }

    private <T extends AbstractConfigObject<U>, U> void reset(T configObject) {
        configObject.setActualValue(configObject.getDefaultValue());
        this.clearChildren();
        this.init();
    }

    protected boolean configEquals() {
        return config.equals(copy);
    }

    /**
     * this saves the config
     */
    protected void saveConfig() {
        this.config.apply(copy);
        this.config.save();
    }

    /**
     * method to know if the game should restart in the case one or many config got changed that requires the game to restart
     * @return <ul>
     *     <li> true if the game should restart
     *         <li> false if not
     * </ul>
     */
    protected boolean shouldRestart() {
        return config.shouldRestart();
    }
}
