package ezalex.manhunt_tools.client.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class ConfigButton extends AbstractWidget {
    public ConfigButton(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int backgroundColor = isHovered() ? 0xFF444444 : 0xFF2A2A2A;

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
                0xFFFFFFFF
        );
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

}
