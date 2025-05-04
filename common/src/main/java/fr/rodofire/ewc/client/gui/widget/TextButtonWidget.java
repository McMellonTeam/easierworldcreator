package fr.rodofire.ewc.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;


@SuppressWarnings("unused")
public class TextButtonWidget extends Button {
    int textColor = 0xFFFFFF;
    int buttonColor = 0xFFFFFF;
    private static final WidgetSprites TEXTURES = new WidgetSprites(
            ResourceLocation.withDefaultNamespace("widget/button"), ResourceLocation.withDefaultNamespace("widget/button_disabled"), ResourceLocation.withDefaultNamespace("widget/button_highlighted")
    );

    public TextButtonWidget(int x, int y, int width, int height, Component text, OnPress onPress) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION);
    }

    public TextButtonWidget(int x, int y, int width, int height, Component text, OnPress onPress, int textColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION);
        this.textColor = textColor;
    }

    public TextButtonWidget(int x, int y, int width, int height, Component text, OnPress onPress, int textColor, int buttonColor) {
        super(x, y, width, height, text, onPress, DEFAULT_NARRATION);
        this.textColor = textColor;
        this.buttonColor = buttonColor;
    }

    public void setColor(int color) {
        this.textColor = color;
    }

    @Override
    protected void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta) {
        Minecraft client = Minecraft.getInstance();
        context.setColor(
                (float) ((buttonColor & 0xFF0000) >> 16) / 256,
                (float) ((buttonColor & 0xFF00) >> 8) / 256,
                (float) (buttonColor & 0xFF) / 256,
                this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        context.blitSprite(TEXTURES.get(this.active, this.isHoveredOrFocused()), this.getX(), this.getY(), this.getWidth(), this.getHeight());
        context.setColor(
                (float) ((textColor & 0xFF0000) >> 16) / 256,
                (float) ((textColor & 0xFF00) >> 8) / 256,
                (float) (textColor & 0xFF) / 256,
                1.0F
        );
        int i = this.active ? 16777215 : 10526880;
        this.renderString(context, client.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
        context.setColor(1.0f, 1.0f, 1.0f, 1.0f);
    }
}
