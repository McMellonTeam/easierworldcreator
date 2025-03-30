package net.rodofire.easierworldcreator.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.rodofire.easierworldcreator.Ewc;
import net.rodofire.easierworldcreator.client.gui.screen.AbstractInfoScreen;

/**
 * class that allows to open an info screen
 */
@Environment(EnvType.CLIENT)
public class InfoButtonWidget extends ImageButtonWidget {
    public <T extends AbstractInfoScreen> InfoButtonWidget(int x, int y, int width, int height, T screen) {
        super(x, y, width, height, Identifier.of(Ewc.MOD_ID, "textures/gui/info_button.png"), button -> MinecraftClient.getInstance().setScreen(screen));
        this.setTooltip(Tooltip.of(Text.translatable("config.ewc.info")));
    }
}
