package ezalex.manhunt_tools.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen extends Screen {
    private final Screen parent;

    public ClientConfigScreen(Screen parent) {
        super(Component.literal("Griefer Manhunt Tools"));
        this.parent = parent;
    }

    private Checkbox showTimerBox;
    private Checkbox showEndScreenBox;

    @Override
    protected void init() {
        super.init();

        int y = 30;
        int y_space = 20;

        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Select Challenge"),
                        button -> {
                            save();
                            Minecraft.getInstance().setScreenAndShow(new ChallengeScreen());
                        }
                ).bounds(this.width / 2 - 60, y, 120, 20).build()
        );

        y = y + 25;

        showEndScreenBox = Checkbox.builder(Component.literal("Show Challenge End Screen"), this.font)
                .selected(ClientConfigManager.get().showEndScreen)
                .pos(this.width / 2 - 100, y)
                .build();

        y = y + y_space;

        showTimerBox = Checkbox.builder(Component.literal("Show Timer On Action Bar"), this.font)
                .selected(ClientConfigManager.get().showTimer)
                .pos(this.width / 2 - 100, y)
                .build();


        this.addRenderableWidget(showEndScreenBox);
        this.addRenderableWidget(showTimerBox);
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Save"),
                        button -> {
                            save();
                        }
                ).bounds(this.width / 2 - 120, this.height - 30, 120, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(
                        Component.literal("Close"),
                        button -> {
                            this.onClose();
                        }
                ).bounds(this.width / 2 + 20, this.height - 30, 120, 20).build()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        String title = "Config";

        int titleWidth = this.font.width(title);

        graphics.text(
                this.font,
                title,
                this.width / 2 - titleWidth / 2,
                20,
                0xFFFFFFFF,
                true
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }

    private void save() {
        ClientConfig config = ClientConfigManager.get();
        config.showEndScreen = showEndScreenBox.selected();
        config.showTimer = showTimerBox.selected();
        ClientConfigManager.save();
    }
}
