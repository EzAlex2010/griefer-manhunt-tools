package ezalex.manhunt_tools.client.widgets;

import ezalex.manhunt_tools.client.ui.UIStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public class ConfigCheckbox extends AbstractWidget {
    public ConfigCheckbox(int x, int y, int width, int height, boolean toggled) {
        super(x, y, width, height, Component.literal(""));
        this.toggled = toggled;
    }

    private boolean toggled;

    @Override
    public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
        if (event.y() > 35) {
            toggle();
        }
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int backgroundColor = isHovered() ? UIStyle.PANEL_HOVER : UIStyle.PANEL;

        graphics.fill(
                getX(),
                getY(),
                getX() + width,
                getY() + height,
                backgroundColor
        );
        graphics.centeredText(
                Minecraft.getInstance().font,
                toggled ? Component.literal("True").withColor(TextColor.GREEN) : Component.literal("False").withColor(TextColor.RED),
                getX() + width / 2,
                getY() + 6,
                UIStyle.TEXT
        );
        graphics.outline(
                getX(),
                getY(),
                width,
                height,
                UIStyle.BORDER
        );
        graphics.text(
                Minecraft.getInstance().font,
                getMessage(),
                getX() - 170,
                getY() + 6,
                UIStyle.TEXT
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    private void toggle() {
        toggled = !toggled;
    }

    public boolean isToggled() {
        return toggled;
    }

}
