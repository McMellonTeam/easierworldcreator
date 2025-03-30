package net.rodofire.easierworldcreator.config.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.rodofire.easierworldcreator.client.gui.widget.TextButtonWidget;

@Environment(EnvType.CLIENT)
public class ShouldRestartScreen extends Screen {
    protected ShouldRestartScreen() {
        super(Text.translatable("screen.config.restart"));
    }

    @Override
    protected void init() {
        this.addDrawableChild(new TextButtonWidget(this.width / 2 - this.width / 8, 2 * this.height / 3, this.width / 4, 30, Text.translatable("config.ewc.accept_restart"), (button) -> {
            MinecraftClient.getInstance().stop();
        }, 0xFFFFFF, 0xFF8000));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        MultilineText.create(textRenderer, Text.translatable("config.ewc.restart_message"), 2 * this.width / 3).drawCenterWithShadow(context, this.width / 2, this.height / 3, 30, 0xFFFFFF);

    }

    @Override
    public void close() {
        MinecraftClient.getInstance().stop();
    }
}
