package ezalex.manhunt_tools.client.screens;

import ezalex.manhunt_tools.client.ClientConfig;
import ezalex.manhunt_tools.client.ClientConfigManager;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.client.widgets.ConfigCheckbox;
import ezalex.manhunt_tools.client.widgets.ConfigRow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClientConfigScreen extends Screen {
    private final Screen parent;

    public ClientConfigScreen(Screen parent) {
        super(Component.literal("Griefer Manhunt Tools"));
        this.parent = parent;
    }

    private ConfigCheckbox showTimerButton;
    private ConfigRow showTimerRow;
    private ConfigCheckbox showEndScreenButton;
    private ConfigRow showEndScreenRow;

    @Override
    protected void init() {
        super.init();

        int y = 30;
        int y_space = 30;

        y = y + 25;

        showEndScreenButton = new ConfigCheckbox(
                this.width / 2,
                0,
                80,
                20,
                ClientConfigManager.get().showEndScreen
        );

        showEndScreenRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Show End Screen"),
                showEndScreenButton,
                true
        );

        y = y + y_space;

        showTimerButton = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                ClientConfigManager.get().showTimer
        );

        showTimerRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Show Timer On The Actionbar"),
                showTimerButton,
                true
        );


        this.addRenderableWidget(showEndScreenButton);
        this.addRenderableWidget(showTimerButton);

        this.addRenderableWidget(showTimerRow);
        this.addRenderableWidget(showEndScreenRow);

        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 - 120,
                        this.height - 30,
                        120,
                        20,
                        Component.literal("Save"),
                        this::save
                )
        );
        this.addRenderableWidget(
                new ConfigButton(
                        this.width / 2 + 20,
                        this.height - 30,
                        120,
                        20,
                        Component.literal("Close"),
                        this::onClose
                )
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(
                this.font,
                "Client Config",
                this.width / 2,
                20,
                0xFFFFFFFF
        );
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }

    private void save() {
        ClientConfig config = ClientConfigManager.get();
        config.showEndScreen = showEndScreenButton.isToggled();
        config.showTimer = showTimerButton.isToggled();
        ClientConfigManager.save();
    }
}
