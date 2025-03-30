package net.rodofire.easierworldcreator.client.gui.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class IntegerEntryWidget extends AbstractEntryWidget {
    public IntegerEntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        super(textRenderer, x, y, width, height, text);
    }

    public IntegerEntryWidget(TextRenderer textRenderer,
                              int x, int y, int width, int height,
                              @Nullable AbstractEntryWidget copyFrom,
                              Text text, String defaultText,
                              PressAction pressAction, TypeAction typeAction
    ) {
        super(textRenderer, x, y, width, height, copyFrom, text, defaultText, pressAction, typeAction);
    }

    @Override
    protected boolean canWrite(char chr) {
        if (chr == 45) return true;
        if (!(chr >= 48 && chr <= 57)) return false;
        String text = getText();
        if (text.isEmpty()) text = String.valueOf(chr);
        else text += chr;
        long testLong = Long.parseLong(text);
        return testLong <= Integer.MAX_VALUE && testLong >= Integer.MIN_VALUE;
    }

    @Override
    protected boolean customWrite(String chr, String text) {
        if (chr.length() == 1 && chr.charAt(0) == '-') {
            if (text.startsWith("-")) text = text.substring(1);
            else text = "-" + text;
            this.text = text;
            return true;
        }
        return false;
    }
}
