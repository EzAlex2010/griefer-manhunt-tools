package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.networking.RequestConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
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

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 100,
                        60,
                        200,
                        20,
                        Component.literal("Client Settings")
                )
//                Button.builder(
//                        Component.literal("Client Settings"),
//                        button -> Minecraft.getInstance().setScreenAndShow(
//                                new ClientConfigScreen(this)
//                        )
//                ).bounds(
//                        this.width / 2 - 100,
//                        60,
//                        200,
//                        20
//                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Server Settings"),
                        button -> requestServerConfig()
                ).bounds(
                        this.width / 2 - 100,
                        90,
                        200,
                        20
                ).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Done"),
                        button -> this.onClose()
                ).bounds(
                        this.width / 2 - 100,
                        this.height - 40,
                        200,
                        20
                ).build()
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
