package fr.rodofire.ewc.client.gui.widget;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ImageButtonWidget extends Button {
    private final ResourceLocation image;

    public ImageButtonWidget(int x, int y, int width, int height, ResourceLocation image, Button.OnPress onPress) {
        super(x, y, width, height, Component.literal(""), onPress, DEFAULT_NARRATION);
        this.image = image;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        context.blit(image, this.getX() + 3, this.getY() + 3, 0, 0, 0, this.width - 6, this.height - 6, this.width - 6, this.height - 6);
    }
}

