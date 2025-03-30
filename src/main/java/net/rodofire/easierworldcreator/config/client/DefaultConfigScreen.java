package net.rodofire.easierworldcreator.config.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.client.gui.screen.AbstractInfoScreen;
import net.rodofire.easierworldcreator.client.gui.widget.ImageButtonWidget;
import net.rodofire.easierworldcreator.client.gui.widget.IntegerEntryWidget;
import net.rodofire.easierworldcreator.client.gui.widget.ScrollBarWidget;
import net.rodofire.easierworldcreator.client.gui.widget.TextButtonWidget;
import net.rodofire.easierworldcreator.config.ConfigCategory;
import net.rodofire.easierworldcreator.config.ModClientConfig;
import net.rodofire.easierworldcreator.config.objects.AbstractConfigObject;
import net.rodofire.easierworldcreator.config.objects.BooleanConfigObject;
import net.rodofire.easierworldcreator.config.objects.EnumConfigObject;
import net.rodofire.easierworldcreator.config.objects.IntegerConfigObject;

import java.util.*;

@Environment(EnvType.CLIENT)
public class DefaultConfigScreen extends AbstractConfigScreen {
    protected final Screen parent;

    List<Drawable> elements = new ArrayList<>();

    private final int UP_PADDING = 40;
    private final int DOWN_PADDING = 35;

    /**
     * used to knox where the mouse is and how we should put the "hole"
     */
    int mouseY = 0;
    boolean bl = false;

    protected int currentCategoryIndex = 0;
    protected final int maxCategoriesVisible = 5;


    protected short maxScrollY = 0;

    protected boolean cancelScreen = false;
    protected boolean restartScreen = false;

    protected List<Short> heights = new ArrayList<>();
    protected Map<Short, Boolean> widths = new LinkedHashMap<>();

    int backgroundDarkRectangleShaderColor = 0xD8000000;

    ScrollBarWidget scrollbar = new ScrollBarWidget(0, 0, 0, (short) 0, button -> {
    }, Text.of(""));

    public DefaultConfigScreen(Screen parent, ModClientConfig config, String modId) {
        super(config, modId);
        this.parent = parent;
    }

    public DefaultConfigScreen(Screen parent, ModClientConfig config, String modId, Identifier background, int backgroundWidth, int backgroundHeight) {
        super(background, backgroundWidth, backgroundHeight, config, modId);
        this.parent = parent;
    }

    public DefaultConfigScreen(Screen parent, ModClientConfig config, String modId, Identifier background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor, int backgroundDarkRectangleShaderColor) {
        super(background, backgroundWidth, backgroundHeight, config, modId);
        this.parent = parent;
        this.backgroundDarkRectangleShaderColor = backgroundDarkRectangleShaderColor;
    }


    @Override
    protected void init(ConfigCategory category) {
        cancelScreen = false;
        int centerX = this.width / 2;
        int startY = 13;
        int buttonWidth = this.width / 8;
        int buttonHeight = 20;

        maxScrollY = (short) calculateContentHeight(category, buttonHeight);

        drawTopCategories(buttonWidth, buttonHeight, startY, centerX);

        drawBottomElements();
        addElements(category, buttonHeight, startY + 27 - scrollbar.getScroll());

        scrollbar.refresh(this.width - 10, UP_PADDING, this.height - DOWN_PADDING, maxScrollY);
        this.addDrawableChild(scrollbar);

    }

    /**
     * we add the config elements (booleans, integer and enums)
     *
     * @param category     the category to choose from
     * @param buttonHeight the height of the buttons
     * @param startY       the start of the buttons
     */
    public void addElements(ConfigCategory category, int buttonHeight, int startY) {
        if (!category.getBools().isEmpty()) {
            heights.add((short) (startY - scrollbar.getScroll() - 1));
            widths.put((short) (startY - scrollbar.getScroll() - 1), true);
            this.addElementChild(new TextWidget(0, startY - scrollbar.getScroll(), 4 * this.width / 12, buttonHeight, Text.translatable("config.ewc.boolean_category"), this.textRenderer));

            startY += buttonHeight + 3;
            for (BooleanConfigObject obj : category.getBools().values()) {
                heights.add((short) (startY - scrollbar.getScroll() - 1));
                widths.put((short) (startY - scrollbar.getScroll() - 1), false);
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollbar.getScroll(), 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));

                this.addElementChild(textWidget);
                this.addElementChild(
                        new TextButtonWidget(
                                14 * this.width / 24,
                                startY - scrollbar.getScroll(),
                                3 * this.width / 12,
                                buttonHeight,
                                Text.translatable("config.ewc.boolean." + obj.getActualValue()),
                                button -> toggleBoolean(obj, button),
                                obj.getActualValue() ? 0x00FF00 : 0xFF0000)
                );
                addSideButtons(buttonHeight, startY, obj);

                startY += buttonHeight + 3;
            }
            startY += 4;
        }
        if (!category.getInts().isEmpty()) {
            heights.add((short) (startY - scrollbar.getScroll() - 1));
            widths.put((short) (startY - scrollbar.getScroll() - 1), true);
            this.addElementChild(new TextWidget(0, startY - scrollbar.getScroll(), 4 * this.width / 12, buttonHeight, Text.translatable("config.ewc.integer_category"), this.textRenderer));

            startY += buttonHeight + 3;

            for (IntegerConfigObject obj : category.getInts().values()) {
                heights.add((short) (startY - scrollbar.getScroll() - 1));
                widths.put((short) (startY - scrollbar.getScroll() - 1), false);
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollbar.getScroll(), 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));
                this.addElementChild(textWidget);


                IntegerEntryWidget integerEntryWidget = new IntegerEntryWidget(this.textRenderer,
                        14 * this.width / 24, startY - scrollbar.getScroll(),
                        3 * this.width / 12, buttonHeight,
                        null, Text.literal("config_entry"), String.valueOf(obj.getActualValue()), null, (button, chr) -> this.verifyInteger(obj, button, chr));

                integerEntryWidget.setTooltip(Tooltip.of(Text.of(
                        Text.translatable("config.ewc.min_value").getString() + ": " + obj.getMinValue() + ", " + Text.translatable("config.ewc.max_value").getString() + ": " + obj.getMaxValue()
                )));

                this.addElementChild(
                        integerEntryWidget
                );
                addSideButtons(buttonHeight, startY, obj);

                startY += buttonHeight + 3;
            }
            startY += 4;
        }

        if (!category.getEnums().isEmpty()) {
            heights.add((short) (startY - scrollbar.getScroll() - 1));
            widths.put((short) (startY - scrollbar.getScroll() - 1), true);
            this.addElementChild(new TextWidget(0, startY - scrollbar.getScroll(), 4 * this.width / 12, buttonHeight, Text.translatable("config.ewc.enum_category"), this.textRenderer));

            startY += buttonHeight + 3;
            for (EnumConfigObject obj : category.getEnums().values()) {
                heights.add((short) (startY - scrollbar.getScroll() - 1));
                widths.put((short) (startY - scrollbar.getScroll() - 1), false);
                TextWidget textWidget = new TextWidget(5 * this.width / 24, startY - scrollbar.getScroll(), 7 * this.width / 24, buttonHeight, Text.translatable("config." + modId + "." + obj.getKey()), this.textRenderer);
                textWidget.setTooltip(Tooltip.of(Text.translatable(obj.getDescriptionKey(modId))));
                this.addElementChild(textWidget);

                this.addElementChild(new TextButtonWidget(14 * this.width / 24, startY - scrollbar.getScroll(), 3 * this.width / 12, buttonHeight, Text.translatable("config." + modId + "." + obj.getActualValue()), button -> cycleEnum(obj, button)));
                addSideButtons(buttonHeight, startY, obj);

                startY += buttonHeight + 3;
            }
        }
    }

    private <T extends AbstractConfigObject<U>, U> void addSideButtons(int buttonHeight, int startY, T obj) {
        this.addElementChild(addResetButton(27 * this.width / 32, startY - scrollbar.getScroll(), obj));
        if (hasInfoScreen(obj)) {
            AbstractInfoScreen screen = getInfoScreen(obj);
            screen.setParent(this);
            this.addElementChild(addInfoButton(27 * this.width / 32 + 22, startY - scrollbar.getScroll(), screen));
        }
    }

    public <T extends Element & Drawable & Selectable> T addElementChild(T element) {
        this.elements.add(element);
        return this.addSelectableChild(element);
    }

    private int calculateContentHeight(ConfigCategory category, int buttonHeight) {
        int height = -this.height + 75;
        if (!category.getBools().isEmpty()) {
            height += buttonHeight + 7;
            height += category.getBools().size() * (buttonHeight + 3);
        }
        if (!category.getInts().isEmpty()) {
            height += buttonHeight + 7;
            height += category.getInts().size() * (buttonHeight + 3);
        }
        if (!category.getEnums().isEmpty()) {
            height += buttonHeight + 3;
            height += category.getEnums().size() * (buttonHeight + 3);
        }
        return height / 2;
    }

    private void drawTopCategories(int buttonWidth, int buttonHeight, int startY, int centerX) {
        int categoriesNumber = Math.min(this.categories.size(), maxCategoriesVisible);
        int startX;
        startX = centerX - (buttonWidth + 2) * categoriesNumber / 2 + 1;

        Iterator<ConfigCategory> iterator = categories.iterator();
        for (int i = 0; i < currentCategoryIndex; i++) {
            iterator.next();
        }
        for (int i = 0; i < Math.min(this.categories.size(), maxCategoriesVisible); i++) {
            ConfigCategory category = iterator.next();
            if (currentCategoryIndex + i == this.selected) {
                this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable(category.getName()), action -> openCategory(action.getMessage().getString()), 0xFFFFFF, 0x00FF00));
            } else {
                this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable(category.getName()), action -> openCategory(action.getMessage().getString())));
            }
            startX += buttonWidth + 2;
        }
        if (categories.size() > maxCategoriesVisible) {
            if (currentCategoryIndex > 0) {

                this.addDrawableChild(new ImageButtonWidget(centerX - 10 - (maxCategoriesVisible * buttonWidth / 2 + 15), startY, 20, buttonHeight, Identifier.of(Ewc.MOD_ID, "textures/gui/before_button.png"), button -> scrollCategories(-1)));
            }
            if (this.categories.size() - currentCategoryIndex > 5) {
                this.addDrawableChild(new ImageButtonWidget(centerX - 10 + (maxCategoriesVisible * buttonWidth / 2 + 15), startY, 20, buttonHeight, Identifier.of(Ewc.MOD_ID, "textures/gui/after_button.png"), button -> scrollCategories(1)));

            }
        }
    }

    public void drawBottomElements() {
        int buttonWidth = 3 * this.width / 10;
        int buttonHeight = 20;
        int startX = this.width / 2 - buttonWidth - 10;
        int startY = this.height - 35;

        this.addDrawableChild(new TextButtonWidget(startX, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.cancel"), button -> this.cancel(), 0xFFFFFF, 0xFF0000));
        this.addDrawableChild(new TextButtonWidget(startX + buttonWidth + 20, startY, buttonWidth, buttonHeight, Text.translatable("config.ewc.save_exit"), button -> this.saveExit(), 0xFFFFFF, 0x00FF00));
    }

    public void openCategory(String name) {
        this.selected = this.indexes.get(name);
        this.clearChildren();
        this.init();
    }

    @Override
    protected void clearChildren() {
        super.clearChildren();
        this.elements.clear();
    }

    private void scrollCategories(int direction) {
        int newIndex = currentCategoryIndex + direction;
        if (newIndex >= 0 && newIndex <= categories.size() - maxCategoriesVisible) {
            currentCategoryIndex = newIndex;
            this.clearChildren();
            this.init();
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.enableScissor(0, UP_PADDING, this.width, this.height - DOWN_PADDING);
        for (Drawable drawable : this.elements) {
            drawable.render(context, mouseX, mouseY, delta);
        }
        context.disableScissor();
    }

    @Override
    public void renderBackgroundTexture(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderBackgroundTexture(context, mouseX, mouseY, delta);
        if (TEXTURE != null) {
            int darkRectX = 0;
            int darkRectY = 40;
            int darkRectWidth = this.width;
            int darkRectHeight = this.height - 75;
            // Coordonnes du trou (zone transparente)
            int holeY = getStartY(mouseY);
            int holeX = getStartX();
            int holeEndX = getEndX();
            int holeEndY = getEndY();
            if (holeY > darkRectY) {
                this.renderDarkRectangle(context, darkRectX, darkRectY, darkRectX + darkRectWidth, holeY, this.backgroundDarkRectangleShaderColor);
                this.renderDarkRectangle(context, holeEndX, holeY, darkRectX + darkRectWidth, holeY + holeEndY, this.backgroundDarkRectangleShaderColor);
            } else {

                this.renderDarkRectangle(context, holeEndX, darkRectY, darkRectX + darkRectWidth, holeY + holeEndY, this.backgroundDarkRectangleShaderColor);
            }
            this.renderDarkRectangle(context, darkRectX, holeY + holeEndY, darkRectX + darkRectWidth, darkRectY + darkRectHeight, this.backgroundDarkRectangleShaderColor);
            this.renderDarkRectangle(context, darkRectX, holeY, holeX, holeY + holeEndY, this.backgroundDarkRectangleShaderColor);

            context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void saveExit() {
        this.saveConfig();
        if (shouldRestart())
            this.restartScreen = true;
        close();
    }

    private void cancel() {
        if (!this.configEquals())
            this.cancelScreen = true;
        close();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        boolean bl = super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        scrollbar.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        this.clearChildren();
        this.init();
        return bl;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean bl = super.mouseReleased(mouseX, mouseY, button);
        scrollbar.mouseReleased(mouseX, mouseY, button);
        return bl;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean bl = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        scrollbar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, this.height);
        this.clearChildren();
        this.init();
        return bl;
    }

    @Override
    public void close() {
        super.close();
        if (this.restartScreen) {
            MinecraftClient.getInstance().setScreen(new ShouldRestartScreen());
        } else if (this.cancelScreen) {
            MinecraftClient.getInstance().setScreen(new CancelScreen(parent, this));
        } else {
            MinecraftClient.getInstance().setScreen(this.parent);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        this.mouseY = (int) mouseY;
    }

    private int getStartY(int y) {
        int i = 0;
        int size = heights.size();
        while (i < size && heights.get(i) < y) {
            i++;
        }
        short yp = heights.get(i == 0 ? 0 : i - 1);
        bl = widths.get(yp);
        return yp;
    }

    /**
     * get the end pos of the "hole"
     *
     * @return the end pos
     */
    private int getEndY() {
        return bl ? 23 : 22;
    }

    /**
     * get the start pos of the "hole"
     *
     * @return the start pos
     */
    private int getStartX() {
        return bl ? 0 : 4 * this.width / 24 - 2;
    }

    /**
     * get the end pos of the "hole"
     *
     * @return the end pos
     */
    private int getEndX() {
        return bl ? 6 * this.width / 12 + 2 : 28 * this.width / 32 + 22;
    }
}
