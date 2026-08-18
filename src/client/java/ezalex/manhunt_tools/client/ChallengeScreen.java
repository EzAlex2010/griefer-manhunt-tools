package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.client.widgets.ConfigButton;
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
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Classic"),
                        () -> {
                            ServerConfigCopy.get().challenge = "classic";
                        }
                )
        );

        y = y + y_space;

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Survive"),
                        () -> {
                            ServerConfigCopy.get().challenge = "survive";
                        }
                )
        );

        y = y + y_space;

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Tank vs Assassins"),
                        () -> {
                            ServerConfigCopy.get().challenge = "tva";
                        }
                )
        );

        y = y + y_space;

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Netherite Assassins"),
                        () -> {
                            ServerConfigCopy.get().challenge = "netherite_assassins";
                        }
                )
        );

        y = y + y_space;

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Manhunt Tag"),
                        () -> {
                            ServerConfigCopy.get().challenge = "tag";
                        }
                )
        );

        y = y + y_space;

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Time Challenge"),
                        () -> {
                            ServerConfigCopy.get().challenge = "timed";
                        }
                )
        );

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 60,
                        this.height - 30,
                        120,
                        20,
                        Component.literal("Back"),
                        this::onClose
                )
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(new ServerConfigScreen(ServerConfigCopy.parentScreen));
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