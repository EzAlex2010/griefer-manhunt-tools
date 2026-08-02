package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.Config;
import ezalex.manhunt_tools.networking.SaveConfigPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreen extends Screen {

    private final Screen parent;

    public ConfigScreen(Screen parent) {
        this.parent = parent;
        super(Component.literal("Config"));
    }

    Config config = ServerConfigCopy.get();

    private EditBox compassIntervalBox;
    private Checkbox showTeamColorsBox;
    private Checkbox giveHuntersCompassBox;
    private Checkbox hunterFriendlyFireBox;
    private Checkbox showTimerBox;
    private EditBox TimeBox;

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

        compassIntervalBox = new EditBox(
                this.font,
                (this.width / 2),
                y,
                60,
                20,
                Component.literal("Compass Interval")
        );

        compassIntervalBox.setValue(
                Integer.toString(config.compassUpdateInterval)
        );

        y = y + y_space;

        showTeamColorsBox = Checkbox.builder(Component.literal("Show Team Colors"), this.font)
                .selected(config.showTeamColors)
                .pos(this.width / 2 - 100, y)
                .build();

        y = y + y_space;

        giveHuntersCompassBox = Checkbox.builder(Component.literal("Give Hunters Compass"), this.font)
                .selected(config.giveHuntersCompass)
                .pos(this.width / 2 - 100, y)
                .build();

        y = y + y_space;

        hunterFriendlyFireBox = Checkbox.builder(Component.literal("Allow Hunter Friendly Fire"), this.font)
                .selected(config.hunterFriendlyFire)
                .pos(this.width / 2 - 100, y)
                .build();

        y = y + y_space;

        showTimerBox = Checkbox.builder(Component.literal("Show Timer On Action Bar"), this.font)
                .selected(config.showTimer)
                .pos(this.width / 2 - 100, y)
                .build();

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
        this.addRenderableWidget(showTimerBox);
        this.addRenderableWidget(TimeBox);
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
        int interval;
        Long time;

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

        config.compassUpdateInterval = interval;
        config.showTeamColors = showTeamColorsBox.selected();
        config.giveHuntersCompass = giveHuntersCompassBox.selected();
        config.hunterFriendlyFire = hunterFriendlyFireBox.selected();
        config.showTimer = showTimerBox.selected();
        config.timerLength = time;

        ClientPlayNetworking.send(new SaveConfigPayload(config));
    }
}