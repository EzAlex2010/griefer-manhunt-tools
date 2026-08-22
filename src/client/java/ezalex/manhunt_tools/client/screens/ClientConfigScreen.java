package ezalex.manhunt_tools.client.screens;

import ezalex.manhunt_tools.client.ClientConfig;
import ezalex.manhunt_tools.client.ClientConfigManager;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.client.widgets.ConfigCheckbox;
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

    private ConfigCheckbox showTimerBox;
    private ConfigCheckbox showEndScreenBox;

    @Override
    protected void init() {
        super.init();

        int y = 30;
        int y_space = 30;

        y = y + 25;

        showEndScreenBox = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                Component.literal("Show End Screen"),
                ClientConfigManager.get().showEndScreen,
                true
        );

        y = y + y_space;

        showTimerBox = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                Component.literal("Show Timer On Actionbar"),
                ClientConfigManager.get().showTimer,
                false
        );


        this.addRenderableWidget(showEndScreenBox);
        this.addRenderableWidget(showTimerBox);
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
        config.showEndScreen = showEndScreenBox.isToggled();
        config.showTimer = showTimerBox.isToggled();
        ClientConfigManager.save();
    }
}
