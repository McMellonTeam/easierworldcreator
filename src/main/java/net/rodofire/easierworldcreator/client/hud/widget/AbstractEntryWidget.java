package net.rodofire.easierworldcreator.client.hud.widget;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@Environment(EnvType.CLIENT)
public abstract class AbstractEntryWidget extends ClickableWidget implements Drawable {

    private boolean drawsBackground = true;
    private boolean focusUnlocked = true;
    protected boolean editable = true;
    private boolean selecting;

    private final TextRenderer textRenderer;
    protected String text = "";
    private int maxLength = 32;
    private int focusedTicks;
    /**
     * The index of the leftmost character that is rendered on a screen.
     */
    private int firstCharacterIndex;
    private int selectionStart;
    private int selectionEnd;
    private int editableColor = 14737632;
    private int uneditableColor = 7368816;
    @Nullable
    private String suggestion;
    @Nullable
    private Consumer<String> changedListener;
    private Predicate<String> textPredicate = Objects::nonNull;
    private BiFunction<String, Integer, OrderedText> renderTextProvider = (string, firstCharacterIndex) -> OrderedText.styledForwardsVisitedString(
            string, Style.EMPTY
    );
    @Nullable
    private Text placeholder;

    PressAction onPress;
    TypeAction onType;

    public AbstractEntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, Text text) {
        this(textRenderer, x, y, width, height, null, text, "", null, null);
    }

    public AbstractEntryWidget(TextRenderer textRenderer, int x, int y, int width, int height, @Nullable AbstractEntryWidget copyFrom, Text text, String defaultText, PressAction pressAction, TypeAction typeAction) {
        super(x, y, width, height, text);
        this.textRenderer = textRenderer;
        if (copyFrom != null) {
            this.setText(copyFrom.getText());
        }
        this.text = defaultText;
        this.onPress = pressAction;
        this.onType = typeAction;
    }

    public void setChangedListener(@Nullable Consumer<String> changedListener) {
        this.changedListener = changedListener;
    }

    public void setRenderTextProvider(BiFunction<String, Integer, OrderedText> renderTextProvider) {
        this.renderTextProvider = renderTextProvider;
    }

    public void tick() {
        this.focusedTicks++;
    }

    @Override
    protected MutableText getNarrationMessage() {
        Text text = this.getMessage();
        return Text.translatable("gui.narrate.editBox", text, this.text);
    }

    public void setText(String text) {
        if (this.textPredicate.test(text)) {
            if (text.length() > this.maxLength) {
                this.text = text.substring(0, this.maxLength);
            } else {
                this.text = text;
            }

            this.setCursorToEnd();
            this.setSelectionEnd(this.selectionStart);
            this.onChanged(text);
        }
    }

    /**
     * method called when pressing the mouse on the entry
     */
    public void onPress() {
        if (onPress != null) {
            this.onPress.onPress(this);
        }
    }

    /**
     * method called when typing on the entry with the keyboard
     */
    public void onType(String chr) {
        if (onType != null) {
            this.onType.onType(this, chr);
        }
    }

    public String getText() {
        return this.text;
    }

    public String getSelectedText() {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        return this.text.substring(i, j);
    }

    public void setTextPredicate(Predicate<String> textPredicate) {
        this.textPredicate = textPredicate;
    }

    public void write(String text) {
        int i = Math.min(this.selectionStart, this.selectionEnd);
        int j = Math.max(this.selectionStart, this.selectionEnd);
        int k = this.maxLength - this.text.length() - (i - j);
        if (k > 0) {
            String string = StringHelper.stripInvalidChars(text);
            int l = string.length();
            if (k < l) {
                if (Character.isHighSurrogate(string.charAt(k - 1))) {
                    k--;
                }

                string = string.substring(0, k);
                l = k;
            }

            String string2 = new StringBuilder(this.text).replace(i, j, string).toString();
            if (this.textPredicate.test(string2)) {
                if (!this.customWrite(text, this.text))
                    this.text = string2;
                this.setSelectionStart(i + l);
                this.setSelectionEnd(this.selectionStart);
                this.onChanged(this.text);
                this.onType(this.text);
            }
        }
    }

    private void onChanged(String newText) {
        if (this.changedListener != null) {
            this.changedListener.accept(newText);
        }
    }

    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            this.eraseWords(offset);
        } else {
            this.eraseCharacters(offset);
        }
    }

    public void eraseWords(int wordOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                this.eraseCharacters(this.getWordSkipPosition(wordOffset) - this.selectionStart);
            }
        }
    }

    public void eraseCharacters(int characterOffset) {
        if (!this.text.isEmpty()) {
            if (this.selectionEnd != this.selectionStart) {
                this.write("");
            } else {
                int i = this.getCursorPosWithOffset(characterOffset);
                int j = Math.min(i, this.selectionStart);
                int k = Math.max(i, this.selectionStart);
                if (j != k) {
                    String string = new StringBuilder(this.text).delete(j, k).toString();
                    if (this.textPredicate.test(string)) {
                        this.text = string;
                        this.setCursor(j);
                    }
                }
            }
        }
    }

    public int getWordSkipPosition(int wordOffset) {
        return this.getWordSkipPosition(wordOffset, this.getCursor());
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition) {
        return this.getWordSkipPosition(wordOffset, cursorPosition, true);
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition, boolean skipOverSpaces) {
        int i = cursorPosition;
        boolean bl = wordOffset < 0;
        int j = Math.abs(wordOffset);

        for (int k = 0; k < j; k++) {
            if (!bl) {
                int l = this.text.length();
                i = this.text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (skipOverSpaces && i < l && this.text.charAt(i) == ' ') {
                        i++;
                    }
                }
            } else {
                while (skipOverSpaces && i > 0 && this.text.charAt(i - 1) == ' ') {
                    i--;
                }

                while (i > 0 && this.text.charAt(i - 1) != ' ') {
                    i--;
                }
            }
        }

        return i;
    }

    public void moveCursor(int offset) {
        this.setCursor(this.getCursorPosWithOffset(offset));
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(this.text, this.selectionStart, offset);
    }

    public void setCursor(int cursor) {
        this.setSelectionStart(cursor);
        if (!this.selecting) {
            this.setSelectionEnd(this.selectionStart);
        }

        this.onChanged(this.text);
    }

    public void setSelectionStart(int cursor) {
        this.selectionStart = MathHelper.clamp(cursor, 0, this.text.length());
    }

    public void setCursorToStart() {
        this.setCursor(0);
    }

    public void setCursorToEnd() {
        this.setCursor(this.text.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!this.isActive()) {
            return false;
        } else {
            this.selecting = Screen.hasShiftDown();
            if (Screen.isSelectAll(keyCode)) {
                this.setCursorToEnd();
                this.setSelectionEnd(0);
                this.onType(this.text);
                return true;
            } else if (Screen.isCopy(keyCode)) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                this.onType(this.text);
                return true;
            } else if (Screen.isPaste(keyCode)) {
                if (this.editable) {
                    this.write(MinecraftClient.getInstance().keyboard.getClipboard());
                }

                this.onType(this.text);
                return true;
            } else if (Screen.isCut(keyCode)) {
                MinecraftClient.getInstance().keyboard.setClipboard(this.getSelectedText());
                if (this.editable) {
                    this.write("");
                }

                this.onType(this.text);
                return true;
            } else {
                switch (keyCode) {
                    case 259:
                        if (this.editable) {
                            this.selecting = false;
                            this.erase(-1);
                            this.selecting = Screen.hasShiftDown();
                        }

                        this.onType(this.text);
                        return true;
                    case 260:
                    case 264:
                    case 265:
                    case 266:
                    case 267:
                    default:
                        this.onType(this.text);
                        return false;
                    case 261:
                        if (this.editable) {
                            this.selecting = false;
                            this.erase(1);
                            this.selecting = Screen.hasShiftDown();
                        }

                        this.onType(this.text);
                        return true;
                    case 262:
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        this.onType(this.text);
                        return true;
                    case 263:
                        if (Screen.hasControlDown()) {
                            this.setCursor(this.getWordSkipPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        this.onType(this.text);
                        return true;
                    case 268:
                        this.setCursorToStart();
                        this.onType(this.text);
                        return true;
                    case 269:
                        this.setCursorToEnd();
                        this.onType(this.text);
                        return true;
                }
            }
        }
    }

    public boolean isActive() {
        return this.isVisible() && this.isFocused() && this.isEditable();
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!this.isActive()) {
            return false;
        } else if (StringHelper.isValidChar(chr)) {
            if (this.editable && this.canWrite(chr)) {
                this.write(Character.toString(chr));
            }
            return true;
        } else {
            return false;
        }
    }

    protected abstract boolean canWrite(char chr);

    protected boolean customWrite(String originText, String text) {
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.onPress();
        int i = MathHelper.floor(mouseX) - this.getX();
        if (this.drawsBackground) {
            i -= 4;
        }

        String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
        this.setCursor(this.textRenderer.trimToWidth(string, i).length() + this.firstCharacterIndex);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.isVisible()) {
            if (this.drawsBackground()) {
                int i = this.isFocused() ? -1 : -6250336;
                context.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, i);
                context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
            }

            int i = this.editable ? this.editableColor : this.uneditableColor;
            int j = this.selectionStart - this.firstCharacterIndex;
            int k = this.selectionEnd - this.firstCharacterIndex;
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), this.getInnerWidth());
            boolean bl = j >= 0 && j <= string.length();
            boolean bl2 = this.isFocused() && this.focusedTicks / 6 % 2 == 0 && bl;
            int l = this.drawsBackground ? this.getX() + 4 : this.getX();
            int m = this.drawsBackground ? this.getY() + (this.height - 8) / 2 : this.getY();
            int n = l;
            if (k > string.length()) {
                k = string.length();
            }

            if (!string.isEmpty()) {
                String string2 = bl ? string.substring(0, j) : string;
                n = context.drawTextWithShadow(this.textRenderer, (OrderedText) this.renderTextProvider.apply(string2, this.firstCharacterIndex), l, m, i);
            }

            boolean bl3 = this.selectionStart < this.text.length() || this.text.length() >= this.getMaxLength();
            int o = n;
            if (!bl) {
                o = j > 0 ? l + this.width : l;
            } else if (bl3) {
                o = n - 1;
                n--;
            }

            if (!string.isEmpty() && bl && j < string.length()) {
                context.drawTextWithShadow(this.textRenderer, (OrderedText) this.renderTextProvider.apply(string.substring(j), this.selectionStart), n, m, i);
            }

            if (this.placeholder != null && string.isEmpty() && !this.isFocused()) {
                context.drawTextWithShadow(this.textRenderer, this.placeholder, n, m, i);
            }

            if (!bl3 && this.suggestion != null) {
                context.drawTextWithShadow(this.textRenderer, this.suggestion, o - 1, m, -8355712);
            }

            if (bl2) {
                if (bl3) {
                    context.fill(RenderLayer.getGuiOverlay(), o, m - 1, o + 1, m + 1 + 9, -3092272);
                } else {
                    context.drawTextWithShadow(this.textRenderer, "_", o, m, i);
                }
            }

            if (k != j) {
                int p = l + this.textRenderer.getWidth(string.substring(0, k));
                this.drawSelectionHighlight(context, o, m - 1, p - 1, m + 1 + 9);
            }
        }
    }

    private void drawSelectionHighlight(DrawContext context, int x1, int y1, int x2, int y2) {
        if (x1 < x2) {
            int i = x1;
            x1 = x2;
            x2 = i;
        }

        if (y1 < y2) {
            int i = y1;
            y1 = y2;
            y2 = i;
        }

        if (x2 > this.getX() + this.width) {
            x2 = this.getX() + this.width;
        }

        if (x1 > this.getX() + this.width) {
            x1 = this.getX() + this.width;
        }

        context.fill(RenderLayer.getGuiTextHighlight(), x1, y1, x2, y2, -16776961);
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        if (this.text.length() > maxLength) {
            this.text = this.text.substring(0, maxLength);
            this.onChanged(this.text);
        }
    }

    private int getMaxLength() {
        return this.maxLength;
    }

    public int getCursor() {
        return this.selectionStart;
    }

    private boolean drawsBackground() {
        return this.drawsBackground;
    }

    public void setDrawsBackground(boolean drawsBackground) {
        this.drawsBackground = drawsBackground;
    }

    public void setEditableColor(int editableColor) {
        this.editableColor = editableColor;
    }

    public void setUneditableColor(int uneditableColor) {
        this.uneditableColor = uneditableColor;
    }

    @Nullable
    @Override
    public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
        return this.visible && this.editable ? super.getNavigationPath(navigation) : null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible
                && mouseX >= (double) this.getX()
                && mouseX < (double) (this.getX() + this.width)
                && mouseY >= (double) this.getY()
                && mouseY < (double) (this.getY() + this.height);
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.focusUnlocked || focused) {
            super.setFocused(focused);
            if (focused) {
                this.focusedTicks = 0;
            }
        }
    }

    private boolean isEditable() {
        return this.editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public int getInnerWidth() {
        return this.drawsBackground() ? this.width - 8 : this.width;
    }

    public void setSelectionEnd(int index) {
        int i = this.text.length();
        this.selectionEnd = MathHelper.clamp(index, 0, i);
        if (this.textRenderer != null) {
            if (this.firstCharacterIndex > i) {
                this.firstCharacterIndex = i;
            }

            int j = this.getInnerWidth();
            String string = this.textRenderer.trimToWidth(this.text.substring(this.firstCharacterIndex), j);
            int k = string.length() + this.firstCharacterIndex;
            if (this.selectionEnd == this.firstCharacterIndex) {
                this.firstCharacterIndex = this.firstCharacterIndex - this.textRenderer.trimToWidth(this.text, j, true).length();
            }

            if (this.selectionEnd > k) {
                this.firstCharacterIndex = this.firstCharacterIndex + (this.selectionEnd - k);
            } else if (this.selectionEnd <= this.firstCharacterIndex) {
                this.firstCharacterIndex = this.firstCharacterIndex - (this.firstCharacterIndex - this.selectionEnd);
            }

            this.firstCharacterIndex = MathHelper.clamp(this.firstCharacterIndex, 0, i);
        }
    }

    public void setFocusUnlocked(boolean focusUnlocked) {
        this.focusUnlocked = focusUnlocked;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSuggestion(@Nullable String suggestion) {
        this.suggestion = suggestion;
    }

    public int getCharacterX(int index) {
        return index > this.text.length() ? this.getX() : this.getX() + this.textRenderer.getWidth(this.text.substring(0, index));
    }

    @Override
    public void appendClickableNarrations(NarrationMessageBuilder builder) {
        builder.put(NarrationPart.TITLE, this.getNarrationMessage());
    }

    public void setPlaceholder(@Nullable Text placeholder) {
        this.placeholder = placeholder;
    }

    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(AbstractEntryWidget widget);
    }

    @Environment(EnvType.CLIENT)
    public interface TypeAction {
        void onType(AbstractEntryWidget widget, String chr);
    }
}
