package net.rodofire.easierworldcreator.client.gui.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.client.gui.widget.TextButtonWidget;

@Environment(EnvType.CLIENT)
public abstract class AbstractInfoScreen extends BackgroundScreen {
    Screen parent;

    protected AbstractInfoScreen(Text title) {
        super(title);
    }

    protected AbstractInfoScreen(Text title, Identifier background, int backgroundWidth, int backgroundHeight) {
        super(title, background, backgroundWidth, backgroundHeight);
    }

    public AbstractInfoScreen(Text title, Identifier background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor) {
        super(title, background, backgroundWidth, backgroundHeight, backgroundShaderColor);
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void close() {
        if(parent != null) {
            MinecraftClient.getInstance().setScreen(parent);
        }
        else
            super.close();
    }

    @Override
    protected void init() {
        this.addDrawableChild(new TextButtonWidget(this.width / 2 - 30, this.height - 22, 60, 20, Text.of("ok"), button -> close(), 0xFFFFFF, 0xa8fffb));
    }
}
