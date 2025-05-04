package fr.rodofire.ewc.client.gui.screen;


import fr.rodofire.ewc.client.gui.widget.TextButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("unused")
public abstract class AbstractInfoScreen extends BackgroundScreen {
    Screen parent;

    protected AbstractInfoScreen(Component title) {
        super(title);
    }

    protected AbstractInfoScreen(Component title, ResourceLocation background, int backgroundWidth, int backgroundHeight) {
        super(title, background, backgroundWidth, backgroundHeight);
    }

    public AbstractInfoScreen(Component title, ResourceLocation background, int backgroundWidth, int backgroundHeight, int backgroundShaderColor) {
        super(title, background, backgroundWidth, backgroundHeight, backgroundShaderColor);
    }

    public void setParent(Screen parent) {
        this.parent = parent;
    }

    @Override
    public void onClose() {
        if(parent != null) {
            Minecraft.getInstance().setScreen(parent);
        }
        else
            super.onClose();
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new TextButtonWidget(this.width / 2 - 30, this.height - 22, 60, 20, Component.literal("ok"), button -> onClose(), 0xFFFFFF, 0xa8fffb));
    }
}
