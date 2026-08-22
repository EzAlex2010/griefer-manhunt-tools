package ezalex.manhunt_tools.client.screens;

import ezalex.manhunt_tools.client.ServerConfigCopy;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.networking.RequestConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {
    private final Screen parent;

    public ConfigScreen(Screen parent) {
        super(Component.literal("Griefer Manhunt Tools"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int y = this.height / 2 - 40;

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 100,
                        y,
                        200,
                        20,
                        Component.literal("Client Settings"),
                        () -> Minecraft.getInstance().setScreenAndShow(
                            new ClientConfigScreen(this)
                        )
                )
        );

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 100,
                        y + 40,
                        200,
                        20,
                        Component.literal("Server Settings"),
                        this::requestServerConfig
                )
        );

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 100,
                        y + 80,
                        200,
                        20,
                        Component.literal("Done"),
                        this::onClose
                )
        );
    }

    private void requestServerConfig() {
        ServerConfigCopy.parentScreen = this;

        ClientPlayNetworking.send(new RequestConfigPayload());
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }
}
