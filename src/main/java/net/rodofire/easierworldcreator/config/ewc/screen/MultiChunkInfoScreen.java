package net.rodofire.easierworldcreator.config.ewc.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmLinkScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.client.gui.screen.AbstractInfoScreen;
import net.rodofire.easierworldcreator.client.gui.widget.ImageButtonWidget;
import net.rodofire.easierworldcreator.client.gui.widget.ScrollBarWidget;

import java.util.List;

@Environment(EnvType.CLIENT)
public class MultiChunkInfoScreen extends AbstractInfoScreen {
    private int maxScroll = 0;
    private static final int PADDING = 30;
    boolean bl = false;
    ScrollBarWidget scrollBar;

    public MultiChunkInfoScreen() {
        super(Text.of("multi-chunk feature info"),
                Identifier.of(Ewc.MOD_ID, "textures/gui/config_background.png"),
                1920,
                1080,
                0xAFAFAFFF
        );
        scrollBar = new ScrollBarWidget(this.width - 10, 25, this.height - 25, (short) this.maxScroll, action -> bl = true, Text.of(""));
    }

    @Override
    protected void init() {
        super.init();
        ImageButtonWidget buttonWidget = new ImageButtonWidget(this.width / 2 + 32, this.height - 22, 20, 20, Identifier.of(Ewc.MOD_ID, "textures/gui/mushrooomsmod.png"), button -> {
            MinecraftClient.getInstance().setScreen(new ConfirmLinkScreen(
                    open -> {
                        if (open) {
                            Util.getOperatingSystem().open("https://modrinth.com/mod/mushroooms");
                        }
                        MinecraftClient.getInstance().setScreen(this);
                    }, "https://modrinth.com/mod/mushroooms", true)
            );
        });
        buttonWidget.setTooltip(Tooltip.of(Text.translatable("config.ewc.open_mushrooomsmod")));
        this.addDrawableChild(buttonWidget);


        maxScroll = calculateContentHeight() - (this.height - 2 * PADDING);

        if (maxScroll < 0) {
            maxScroll = 0; // Pas de défilement nécessaire si le contenu est plus petit que la zone visible
        }
        scrollBar.refresh(this.width - 10, 25, this.height - 25, maxScroll);
        this.addDrawableChild(scrollBar);
    }

    @Override
    public void renderOverBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        int scrollOffset = scrollBar.getScroll();

        // Début du test de découpe
        this.renderDarkRectangle(context, 0, PADDING - 5, this.width, this.height - PADDING + 5, 0xBA000000);
        context.enableScissor(0, PADDING, this.width, this.height - PADDING);

        int yOffset = PADDING - scrollOffset; // Position de départ du contenu, ajustée par le scroll

        // Rendu des éléments dans la zone scrollable
        MultilineText.create(this.textRenderer, Text.translatable("config.ewc.multi_chunk.concept"), 4 * this.width / 5)
                .drawCenterWithShadow(context, this.width / 2, yOffset);

        int width1 = 8 * this.width / 20;
        int height1 = 9 * width1 / 16;

        Text description = Text.translatable("config.ewc.multi_chunk.comparison");
        MutableText clickableMod = Text.literal("mushrooomsmod")
                .styled(
                        style -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://modrinth.com/mod/mushroooms")) // Action d'ouverture de lien
                                .withColor(Formatting.BLUE)
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("config.ewc.open_mushrooomsmod")))); // Texte au survol
        MutableText fullText = Text.literal("").append(description).append(clickableMod);
        context.drawCenteredTextWithShadow(this.textRenderer, fullText, this.width / 2, yOffset + calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 15, 0xFFFFFF);

        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("config.ewc.multi_chunk.with"), this.width / 2 + (width1 + 10) / 2, yOffset + calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 26, 0xFFFFFF);
        context.drawTexture(
                Identifier.of(Ewc.MOD_ID, "textures/gui/info/with_multi_chunk.png"),
                this.width / 2 + 10,
                yOffset + calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 36,
                0,
                0,
                width1,
                height1,
                width1,
                height1
        );
        context.drawCenteredTextWithShadow(this.textRenderer, Text.translatable("config.ewc.multi_chunk.without"), this.width / 2 - (width1 - 10) / 2, yOffset + calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 26, 0xFFFFFF);
        context.drawTexture(
                Identifier.of(Ewc.MOD_ID, "textures/gui/info/without_multi_chunk.png"),
                this.width / 2 - width1 - 10,
                yOffset + calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 36,
                0,
                0,
                width1,
                height1,
                width1,
                height1
        );
        context.disableScissor();

        this.renderDarkRectangle(context, this.width / 2 - 75, 3, this.width / 2 + 75, 15, 0xDA000000);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 5, 16777215);
    }

    private int calculateContentHeight() {
        return calculateTextHeight(Text.translatable("config.ewc.multi_chunk.concept").getString(), 4 * this.width / 5) + 32 + 9 * (8 * this.width / 20) / 16;
    }

    public int calculateTextHeight(String text, int maxWidth) {
        // Divise le texte en lignes, limitées à maxWidth
        List<OrderedText> wrappedLines = textRenderer.wrapLines(Text.of(text), maxWidth);
        return wrappedLines.size() * (textRenderer.fontHeight);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return scrollBar.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return scrollBar.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, this.height);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return scrollBar.mouseReleased(mouseX, mouseY, button);
    }
}
