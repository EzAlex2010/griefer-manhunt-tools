package ezalex.manhunt_tools.client.widgets;

import ezalex.manhunt_tools.client.ui.UIStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;

public class ConfigRow extends AbstractContainerWidget {
    private final AbstractWidget widget;
    private boolean fillbg = true;

    public ConfigRow(int x, int y, int width, int height, Component label, AbstractWidget child, boolean fillbg) {
        super(x, y, width, height, label);
        this.widget = child;
        this.fillbg = fillbg;
    }
    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Row background
        if (fillbg) {
            graphics.fill(
                    getX(),
                    getY(),
                    getX() + width,
                    getY() + height,
                    UIStyle.BACKGROUND
            );
        }

        // Label
        graphics.text(
                Minecraft.getInstance().font,
                getMessage(),
                (this.width / 2) - 170,
                getY() + 11,
                UIStyle.TEXT
        );

        widget.setY(getY() + 5);

        // Child widget
        widget.extractRenderState(
                graphics,
                mouseX,
                mouseY,
                a
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    public AbstractWidget getWidget() {
        return widget;
    }

    @Override
    protected int contentHeight() {
        return 0;
    }

    @Override
    public List<? extends GuiEventListener> children() {
        return List.of();
    }
}
