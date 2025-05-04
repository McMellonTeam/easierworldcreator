package fr.rodofire.ewc.config.client;

import fr.rodofire.ewc.client.gui.widget.TextButtonWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ShouldRestartScreen extends Screen {
    protected ShouldRestartScreen() {
        super(Component.translatable("screen.config.restart"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(new TextButtonWidget(this.width / 2 - this.width / 8, 2 * this.height / 3, this.width / 4, 30, Component.translatable("config.ewc.accept_restart"), (button) ->
                Minecraft.getInstance().stop(), 0xFFFFFF, 0xFF8000));
    }

    @Override
    public void render(@NotNull GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        MultiLineLabel.create(this.font, Component.translatable("config.ewc.restart_message"), 2 * this.width / 3).renderCentered(context, this.width / 2, this.height / 3, 30, 0xFFFFFF);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().stop();
    }
}
