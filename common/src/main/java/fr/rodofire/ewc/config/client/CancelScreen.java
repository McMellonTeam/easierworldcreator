package fr.rodofire.ewc.config.client;


import fr.rodofire.ewc.client.gui.widget.TextButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class CancelScreen extends Screen {
    private final Screen parent;
    private final Screen base;

    public CancelScreen(Screen parent, Screen base) {
        super(Component.translatable("config.ewc.cancel"));
        this.parent = parent;
        this.base = base;
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new TextButtonWidget(this.width / 2 - this.width / 3, 2 * this.height / 3, this.width / 3 - 10, 25, Component.translatable("config.ewc.cancel"), button -> {
            this.clearWidgets();
            this.onClose();
            Minecraft.getInstance().setScreen(base);
        }, 0xFFFFFF, 0x00FF00));
        this.addRenderableWidget(new TextButtonWidget(this.width / 2 + 10, 2 * this.height / 3, this.width / 3 - 10, 25, Component.translatable("config.ewc.confirm"), button -> {
            this.clearWidgets();
            this.onClose();
            Minecraft.getInstance().setScreen(parent);
        }, 0xFFFFFF, 0xFF0000));
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(base);
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        MultiLineLabel.create(font, Component.translatable("config.ewc.wanna_quit"), 2 * this.width / 3).renderCentered(context, this.width / 2, this.height / 4, 30, 0xFFFFFF);
    }
}
