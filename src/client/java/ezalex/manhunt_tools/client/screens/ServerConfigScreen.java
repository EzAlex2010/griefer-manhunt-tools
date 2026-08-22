package ezalex.manhunt_tools.client.screens;

import ezalex.manhunt_tools.Config;
import ezalex.manhunt_tools.client.ServerConfigCopy;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import ezalex.manhunt_tools.client.widgets.ConfigCheckbox;
import ezalex.manhunt_tools.client.widgets.ConfigRow;
import ezalex.manhunt_tools.networking.SaveConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    private ConfigRow selectChallengeRow;
    private ConfigButton selectChallengeButton;
    private ConfigRow compassIntervalRow;
    private EditBox compassIntervalBox;
    private ConfigRow showTeamColorsRow;
    private ConfigCheckbox showTeamColorsButton;
    private ConfigRow giveHuntersCompassRow;
    private ConfigCheckbox giveHuntersCompassButton;
    private ConfigRow hunterFriendlyFireRow;
    private ConfigCheckbox hunterFriendlyFireButton;
    private ConfigRow TimeRow;
    private EditBox TimeBox;
    private ConfigRow keepInventoryRow;
    private ConfigCheckbox keepInventoryButton;
    private ConfigRow worldBorderSizeRow;
    private EditBox worldBorderSizeBox;
    private ConfigRow locatorBarRow;
    private ConfigCheckbox locatorBarButton;

    private int scrollOffset = 0;

    private ConfigButton saveButton;
    private ConfigButton closeButton;

    @Override
    protected void init() {
        super.init();

        int y = 40;
        int y_space = 30;

        selectChallengeButton = new ConfigButton(
                this.width / 2 + 50,
                0,
                80,
                20,
                Component.literal("Open Select"),
                () -> {
                    save();
                    Minecraft.getInstance().setScreenAndShow(new ChallengeScreen());
                }
        );

        selectChallengeRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Select Challenge"),
                selectChallengeButton,
                true
        );

        y = y + y_space;

        compassIntervalBox = new EditBox(
                this.font,
                this.width / 2 + 50,
                0,
                80,
                20,
                Component.literal("")
        );

        compassIntervalRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Compass Update Interval"),
                compassIntervalBox,
                false
        );
        compassIntervalBox.setValue(
                Integer.toString(config.compassUpdateInterval)
        );
        y = y + y_space;

        showTeamColorsButton = new ConfigCheckbox(
                this.width / 2  + 50,
                0,
                80,
                20,
                config.showTeamColors
        );

        showTeamColorsRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Show Team Colors"),
                showTeamColorsButton,
                true
        );

        y = y + y_space;

        giveHuntersCompassButton = new ConfigCheckbox(
                this.width / 2 + 50,
                0,
                80,
                20,
                config.giveHuntersCompass
        );

        giveHuntersCompassRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Give Hunters Compass"),
                giveHuntersCompassButton,
                false
        );

        y = y + y_space;

        hunterFriendlyFireButton = new ConfigCheckbox(
                this.width / 2 + 50,
                0,
                80,
                20,
                config.hunterFriendlyFire
        );

        hunterFriendlyFireRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Hunter Friendly Fire"),
                hunterFriendlyFireButton,
                true
        );

        y = y + y_space;

        TimeBox = new EditBox(
                this.font,
                (this.width / 2) + 50,
                0,
                80,
                20,
                Component.literal("")
        );
        TimeRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Time In Seconds"),
                TimeBox,
                false
        );
        TimeBox.setValue(
                Long.toString(config.timerLength)
        );

        y = y + y_space;

        keepInventoryButton = new ConfigCheckbox(
                this.width / 2 + 50,
                0,
                80,
                20,
                config.keepInventory
        );

        keepInventoryRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Keep Inventory"),
                keepInventoryButton,
                true
        );

        y = y + y_space;

        worldBorderSizeBox = new EditBox(
                this.font,
                (this.width / 2) + 50,
                0,
                80,
                20,
                Component.literal("")
        );

        worldBorderSizeRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("World Border Size"),
                worldBorderSizeBox,
                false
        );

        worldBorderSizeBox.setValue(Integer.toString(config.worldBoarderSize));

        y = y + y_space;

        locatorBarButton = new ConfigCheckbox(
                this.width / 2 + 50,
                0,
                80,
                20,
                config.allowHuntersLocatorBar
        );

        locatorBarRow = new ConfigRow(
                0,
                y,
                this.width,
                30,
                Component.literal("Allow Hunters Use Locator Bar"),
                locatorBarButton,
                true
        );

        this.addRenderableWidget(selectChallengeButton);
        this.addRenderableWidget(compassIntervalBox);
        this.addRenderableWidget(showTeamColorsButton);
        this.addRenderableWidget(giveHuntersCompassButton);
        this.addRenderableWidget(hunterFriendlyFireButton);
        this.addRenderableWidget(TimeBox);
        this.addRenderableWidget(keepInventoryButton);
        this.addRenderableWidget(worldBorderSizeBox);
        this.addRenderableWidget(locatorBarButton);

        this.addRenderableWidget(compassIntervalRow);
        this.addRenderableWidget(showTeamColorsRow);
        this.addRenderableWidget(giveHuntersCompassRow);
        this.addRenderableWidget(hunterFriendlyFireRow);
        this.addRenderableWidget(TimeRow);
        this.addRenderableWidget(keepInventoryRow);
        this.addRenderableWidget(worldBorderSizeRow);
        this.addRenderableWidget(locatorBarRow);

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
        selectChallengeRow.setY(40 - scrollOffset);
        compassIntervalRow.setY(70 - scrollOffset);
        showTeamColorsRow.setY(100 - scrollOffset);
        giveHuntersCompassRow.setY(130 - scrollOffset);
        hunterFriendlyFireRow.setY(160 - scrollOffset);
        TimeRow.setY(190 - scrollOffset);
        keepInventoryRow.setY(220 - scrollOffset);
        worldBorderSizeRow.setY(250 - scrollOffset);
        locatorBarRow.setY(280 - scrollOffset);

        graphics.enableScissor(0, 35, this.width, this.height - 35);
        selectChallengeRow.extractRenderState(graphics, mouseX, mouseY, delta);
        compassIntervalRow.extractRenderState(graphics, mouseX, mouseY, delta);
        showTeamColorsRow.extractRenderState(graphics, mouseX, mouseY, delta);
        giveHuntersCompassRow.extractRenderState(graphics, mouseX, mouseY, delta);
        hunterFriendlyFireRow.extractRenderState(graphics, mouseX, mouseY, delta);
        TimeRow.extractRenderState(graphics, mouseX, mouseY, delta);
        keepInventoryRow.extractRenderState(graphics, mouseX, mouseY, delta);
        worldBorderSizeRow.extractRenderState(graphics, mouseX, mouseY, delta);
        locatorBarRow.extractRenderState(graphics, mouseX, mouseY, delta);
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
        int worldBorderSize;

        try {
            interval = Integer.parseInt(compassIntervalBox.getValue());
        } catch (NumberFormatException e) {
            interval = 20;
        }

        try {
            time = Long.parseLong(TimeBox.getValue());
        } catch (NumberFormatException e) {
            time = 720L;
        }

        try {
            worldBorderSize = Integer.parseInt(worldBorderSizeBox.getValue());
        } catch (NumberFormatException e) {
            worldBorderSize = 9999999;
        }

        config.compassUpdateInterval = interval;
        config.showTeamColors = showTeamColorsButton.isToggled();
        config.giveHuntersCompass = giveHuntersCompassButton.isToggled();
        config.hunterFriendlyFire = hunterFriendlyFireButton.isToggled();
        config.timerLength = time;
        config.keepInventory = keepInventoryButton.isToggled();
        config.worldBoarderSize = worldBorderSize;
        config.allowHuntersLocatorBar = locatorBarButton.isToggled();

        ClientPlayNetworking.send(new SaveConfigPayload(config));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scrollOffset -= verticalAmount * 10;
        scrollOffset = Math.clamp(scrollOffset, 0, 100);
        return true;
    }

    public int getScollLowerViewportBound() {
        return this.height - 35;
    }

    public int getScollUpperViewportBound() {
        return 35;
    }
}