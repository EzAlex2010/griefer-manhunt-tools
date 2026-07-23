package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.SetChallengePayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
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
                            ClientPlayNetworking.send(
                                    new SetChallengePayload("classic")
                            );
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Tank vs Assassins"),
                        button -> {
                            ClientPlayNetworking.send(
                                    new SetChallengePayload("tva")
                            );
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Netherite Assassins"),
                        button -> {
                            ClientPlayNetworking.send(
                                    new SetChallengePayload("netherite_assassins")
                            );
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Manhunt Tag"),
                        button -> {
                            ClientPlayNetworking.send(
                                    new SetChallengePayload("tag")
                            );
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + y_space;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Time Challenge"),
                        button -> {
                            ClientPlayNetworking.send(
                                    new SetChallengePayload("timed")
                            );
                            this.onClose();
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Close"),
                        button -> this.onClose()
                ).bounds(this.width / 2 - 60, this.height - 30, 120, 20).build()
        );
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