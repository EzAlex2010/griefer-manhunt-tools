package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.networking.SetChallengePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ChallengeScreen extends Screen {

    public ChallengeScreen() {
        super(Component.literal("Challenge Select"));
    }

    @Override
    protected void init() {
        super.init();

        int y = 50;
        int y_space = 25;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Classic"),
                        button -> {
                            ServerConfigCopy.get().challenge = "classic";
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Tank vs Assassins"),
                        button -> {
                            ServerConfigCopy.get().challenge = "tva";
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Netherite Assassins"),
                        button -> {
                            ServerConfigCopy.get().challenge = "netherite_assassins";
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Manhunt Tag"),
                        button -> {
                            ServerConfigCopy.get().challenge = "tag";
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Time Challenge"),
                        button -> {
                            ServerConfigCopy.get().challenge = "timed";
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Back"),
                        button -> this.onClose()
                ).bounds(this.width / 2 - 60, this.height - 30, 120, 20).build()
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(new ConfigScreen(ServerConfigCopy.parentScreen));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int titleWidth = this.font.width("Select Challenge");

        graphics.text(
                this.font,
                "Select Challenge",
                this.width / 2 - titleWidth / 2,
                20,
                0xFFFFFFFF,
                true
        );
    }
}