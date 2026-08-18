package ezalex.manhunt_tools.client.widgets;

import ezalex.manhunt_tools.client.ui.UIStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ConfigButton extends AbstractWidget {
    public ConfigButton(int x, int y, int width, int height, Component message, Runnable onClick) {
        super(x, y, width, height, message);
        this.onClick = onClick;
    }

    private final Runnable onClick;

    @Override
    public void onClick(final MouseButtonEvent event, final boolean doubleClick) {
        if (event.y() > 35 && event.y() < Minecraft.getInstance().getWindow().getScreenHeight() - 35) {
            onClick.run();
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
                getMessage(),
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
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

}
