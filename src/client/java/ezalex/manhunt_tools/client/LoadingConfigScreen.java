package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.networking.RequestConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class LoadingConfigScreen extends Screen {

    private final Screen parent;

    public LoadingConfigScreen(Screen parent) {
        super(Component.literal("Config Loading Screen"));
        this.parent = parent;
        ServerConfigCopy.parentScreen = parent;
        ClientPlayNetworking.send(new RequestConfigPayload());
    }


    @Override
    protected void init() {
        super.init();

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Close"),
                        button -> this.onClose()
                ).bounds(this.width / 2 - 60, this.height - 30, 120, 20).build()
        );
    }

    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int titleWidth = this.font.width("Loading Config...");

        graphics.text(
                this.font,
                "Loading Config...",
                this.width / 2 - titleWidth / 2,
                20,
                0xFFFFFFFF,
                true
        );
    }
}