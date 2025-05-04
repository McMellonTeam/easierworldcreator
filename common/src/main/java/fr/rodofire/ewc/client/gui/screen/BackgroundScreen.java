package fr.rodofire.ewc.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class BackgroundScreen extends Screen {
    protected ResourceLocation TEXTURE = null;
    int backgroundHeight = 32;
    int backgroundWidth = 32;

    int backgroundShaderColor = 0x3F3F3FFF;

    protected BackgroundScreen(Component title) {
        super(title);
    }

    protected BackgroundScreen(Component title, ResourceLocation background, int backgroundWidth, int backgroundHeight) {
        super(title);
        this.TEXTURE = background;
        this.backgroundHeight = backgroundHeight;
        this.backgroundWidth = backgroundWidth;
    }

    protected BackgroundScreen(Component title, ResourceLocation background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor) {
        super(title);
        this.TEXTURE = background;
        this.backgroundHeight = backgroundHeight;
        this.backgroundWidth = backgroundWidth;
        this.backgroundShaderColor = backgroundShaderColor;
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        renderBackgroundTexture(context, mouseX, mouseY, delta);
        renderOverBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

    }

    public void renderOverBackground(GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
    }

    /**
     * method to render the backgound
     */
    public void renderBackgroundTexture(GuiGraphics context, int mouseX, int mouseY, float delta) {
        if (TEXTURE == null) {
            super.renderBackground(context, mouseX, mouseY, delta);
            return;
        }
        float textureRatio = (float) this.backgroundWidth / this.backgroundHeight;
        float screenRatio = (float) this.width / this.height;

        int renderWidth, renderHeight, offsetX, offsetY;
        context.setColor(
                (float) ((this.backgroundShaderColor & 0xFF000000) >>> 24) / 0xFF,
                (float) ((this.backgroundShaderColor & 0x00FF0000) >> 16) / 0xFF,
                (float) ((this.backgroundShaderColor & 0x0000FF00) >> 8) / 0xFF,
                (float) (this.backgroundShaderColor & 0x0000000FF) / 0xFF
        );
        if (textureRatio == 1) {
            context.blit(
                    TEXTURE, 0, 0,
                    0, 0, this.width, this.height, this.backgroundWidth, this.backgroundHeight
            );
        } else {
            if (screenRatio > textureRatio) {
                renderWidth = this.width;
                renderHeight = (int) (this.width / textureRatio);
                offsetX = 0;
                offsetY = (renderHeight - this.height) / 2;
            } else {
                renderWidth = (int) (this.height * textureRatio);
                renderHeight = this.height;
                offsetX = (renderWidth - this.width) / 2;
                offsetY = 0;
            }

            context.blit(
                    TEXTURE,
                    -offsetX,
                    -offsetY,
                    0, 0,
                    renderWidth,
                    renderHeight,
                    renderWidth,
                    renderHeight
            );
        }
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * method to render a dark rectangle on top of the background, used in config screen
     * @param context draw context
     * @param x corner coordinates of the rectangle
     * @param y corner coordinates of the rectangle
     * @param x2 corner coordinates of the rectangle
     * @param y2 corner coordinates of the rectangle
     * @param color the color of the rectangle
     */
    public void renderDarkRectangle(GuiGraphics context, int x, int y, int x2, int y2, int color) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        context.setColor(
                (float) ((color & 0x00FF0000) >> 16) / 256,
                (float) ((color & 0x0000FF00) >> 8) / 256,
                (float) (color & 0x000000FF) / 256,
                (float) (color >>> 24) / 256
        );
        context.fill(x, y, x2, y2, color);
        context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
    }
}
