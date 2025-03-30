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
public class CancelScreen extends Screen {
    private Screen parent;
    private Screen base;

    public CancelScreen(Screen parent, Screen base) {
        super(Text.translatable("config.ewc.cancel"));
        this.parent = parent;
        this.base = base;
    }

    @Override
    protected void init() {
        this.addDrawableChild(new TextButtonWidget(this.width / 2 - this.width / 3, 2 * this.height / 3, this.width / 3 - 10, 25, Text.translatable("config.ewc.cancel"), button -> {
            this.clearChildren();
            this.close();
            MinecraftClient.getInstance().setScreen(base);
        }, 0xFFFFFF, 0x00FF00));
        this.addDrawableChild(new TextButtonWidget(this.width / 2 + 10, 2 * this.height / 3, this.width / 3 - 10, 25, Text.translatable("config.ewc.confirm"), button -> {
            this.clearChildren();
            this.close();
            MinecraftClient.getInstance().setScreen(parent);
        }, 0xFFFFFF, 0xFF0000));
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(base);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        MultilineText.create(textRenderer, Text.translatable("config.ewc.wanna_quit"), 2 * this.width / 3).drawCenterWithShadow(context, this.width / 2, this.height / 4, 30, 0xFFFFFF);

    }
}
