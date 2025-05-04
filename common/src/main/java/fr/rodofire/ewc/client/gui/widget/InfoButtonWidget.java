package fr.rodofire.ewc.client.gui.widget;

import fr.rodofire.ewc.EwcConstants;
import fr.rodofire.ewc.client.gui.screen.AbstractInfoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * class that allows to open an info screen
 */
public class InfoButtonWidget extends ImageButtonWidget {
    public <T extends AbstractInfoScreen> InfoButtonWidget(int x, int y, int width, int height, T screen) {
        super(x, y, width, height, ResourceLocation.fromNamespaceAndPath(EwcConstants.MOD_ID, "textures/gui/info_button.png"), button -> Minecraft.getInstance().setScreen(screen));
        this.setTooltip(Tooltip.create(Component.translatable("config.ewc.info")));
    }
}
