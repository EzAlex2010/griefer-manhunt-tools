package ezalex.manhunt_tools.client.widgets;

import ezalex.manhunt_tools.client.ui.UIStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;

public class ConfigRow {
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    private final Component label;
    private final AbstractWidget widget;

    public ConfigRow(int x, int y, int width, int height, Component label, AbstractWidget widget) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.label = label;
        this.widget = widget;
    }

    public void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Row background
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                UIStyle.PANEL
        );

        // Label
        graphics.text(
                Minecraft.getInstance().font,
                label,
                x + 6,
                y + 6,
                UIStyle.TEXT
        );

        // Child widget
        widget.extractRenderState(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );

        // Border
        graphics.outline(
                x,
                y,
                width,
                height,
                UIStyle.BORDER
        );
    }

    public AbstractWidget getWidget() {
        return widget;
    }
}
