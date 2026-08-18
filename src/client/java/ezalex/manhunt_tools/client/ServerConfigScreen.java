package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.Config;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.client.widgets.ConfigCheckbox;
import ezalex.manhunt_tools.client.widgets.ConfigRow;
import ezalex.manhunt_tools.networking.SaveConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ServerConfigScreen extends Screen {

    private final Screen parent;

    public ServerConfigScreen(Screen parent) {
        this.parent = parent;
        super(Component.literal("Config"));
    }

    Config config = ServerConfigCopy.get();

    private ConfigButton selectChallengeButton;
    private ConfigRow compassIntervalBox;
    private ConfigCheckbox showTeamColorsBox;
    private ConfigCheckbox giveHuntersCompassBox;
    private ConfigCheckbox hunterFriendlyFireBox;
    private EditBox TimeBox;

    private int scrollOffset = 0;

    private ConfigButton saveButton;
    private ConfigButton closeButton;

    @Override
    protected void init() {
        super.init();

        int y = 40;
        int y_space = 30;

        selectChallengeButton = new ConfigButton(
                this.width / 2 - 60,
                y,
                120,
                20,
                Component.literal("Select Challenge"),
                () -> {
                    save();
                    Minecraft.getInstance().setScreenAndShow(new ChallengeScreen());
                }
        );

        this.addRenderableWidget(selectChallengeButton);

        y = y + y_space;

        compassIntervalBox = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Compass Update Interval"),

        );
        compassIntervalBox.getWidget().setValue(
                Integer.toString(config.compassUpdateInterval)
        );
        y = y + y_space;

        showTeamColorsBox = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                Component.literal("Show Team Colors"),
                config.showTeamColors,
                true
        );

        y = y + y_space;

        giveHuntersCompassBox = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                Component.literal("Give Hunters Compass"),
                config.giveHuntersCompass,
                false
        );

        y = y + y_space;

        hunterFriendlyFireBox = new ConfigCheckbox(
                this.width / 2,
                y,
                80,
                20,
                Component.literal("Allow Hunter Friendly Fire"),
                config.hunterFriendlyFire,
                true
        );

        y = y + y_space;

        TimeBox = new EditBox(
                this.font,
                (this.width / 2),
                y,
                60,
                20,
                Component.literal("Compass Interval")
        );

        TimeBox.setValue(
                Long.toString(config.timerLength)
        );


        this.addRenderableWidget(compassIntervalBox);
        this.addRenderableWidget(showTeamColorsBox);
        this.addRenderableWidget(giveHuntersCompassBox);
        this.addRenderableWidget(hunterFriendlyFireBox);
        this.addRenderableWidget(TimeBox);

        saveButton = new ConfigButton(
                this.width / 2 - 140,
                this.height - 30,
                120,
                20,
                Component.literal("Save"),
                this::save
        );
        closeButton = new ConfigButton(
                this.width / 2 + 20,
                this.height - 30,
                120,
                20,
                Component.literal("Close"),
                this::onClose
        );

        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(closeButton);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        selectChallengeButton.setY(40 - scrollOffset);
        compassIntervalBox.setY(65 - scrollOffset);
        showTeamColorsBox.setY(100 - scrollOffset);
        giveHuntersCompassBox.setY(130 - scrollOffset);
        hunterFriendlyFireBox.setY(160 - scrollOffset);
        TimeBox.setY(190 - scrollOffset);

        graphics.enableScissor(0, 35, this.width, this.height - 35);
        selectChallengeButton.extractRenderState(graphics, mouseX, mouseY, delta);
        compassIntervalBox.extractRenderState(graphics, mouseX, mouseY, delta);
        showTeamColorsBox.extractRenderState(graphics, mouseX, mouseY, delta);
        giveHuntersCompassBox.extractRenderState(graphics, mouseX, mouseY, delta);
        hunterFriendlyFireBox.extractRenderState(graphics, mouseX, mouseY, delta);
        TimeBox.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.disableScissor();

        closeButton.extractRenderState(graphics, mouseX, mouseY, delta);
        saveButton.extractRenderState(graphics, mouseX, mouseY, delta);

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
        int interval;
        Long time;

        try {
            interval = Integer.parseInt(compassIntervalBox.getWidget().getValue());
        } catch (NumberFormatException e) {
            interval = 20;
        }

        try {
            time = Long.parseLong(TimeBox.getValue());
        } catch (NumberFormatException e) {
            time = 720L;
        }

        config.compassUpdateInterval = interval;
        config.showTeamColors = showTeamColorsBox.isToggled();
        config.giveHuntersCompass = giveHuntersCompassBox.isToggled();
        config.hunterFriendlyFire = hunterFriendlyFireBox.isToggled();
        config.timerLength = time;

        ClientPlayNetworking.send(new SaveConfigPayload(config));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 10;
        scrollOffset = Math.clamp(scrollOffset, 0, 100);
        return true;
    }
}