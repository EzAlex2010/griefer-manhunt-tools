package ezalex.manhunt_tools.client.screens;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.client.GrieferManhuntToolsClient;
import ezalex.manhunt_tools.client.ServerConfigCopy;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;

public class ChallengeScreen extends Screen {

    public ChallengeScreen() {
        super(Component.literal("Challenge Select"));
    }

    private Component description = Component.literal("Select A Challenge To View Its Description");
    private String selected_challenge = "classic";

    @Override
    protected void init() {
        super.init();

        int y = 40;
        int y_space = 25;
        int y_test = (this.height - 50) / 6;
        GrieferManhuntToolsClient.LOGGER.info("y_test: " + y_test);
        if (y_test > 25) {
            y_space = y_test;
        }

        selected_challenge = ServerConfigCopy.get().challenge;
        description = loadDescription(selected_challenge);

        this.addRenderableWidget(
                new ConfigButton(
                        20,
                        y,
                        120,
                        20,
                        Component.literal("Classic"),
                        () -> {
                            selected_challenge = "classic";
                            description = loadDescription("classic");
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
                            selected_challenge = "survive";
                            description = loadDescription("survive");
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
                            selected_challenge = "tva";
                            description = loadDescription("tva");
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
                            selected_challenge = "netherite_assassins";
                            description = loadDescription("netherite_assassins");
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
                            selected_challenge = "tag";
                            description = loadDescription("tag");
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
                            selected_challenge = "timed";
                            description = loadDescription("timed");
                        }
                )
        );

        this.addRenderableWidget(
                new ConfigButton(
                        this.width - 130,
                        this.height - 60,
                        120,
                        20,
                        Component.literal("Select Challenge"),
                        this::selectChallenge
                )
        );

        this.addRenderableWidget(
                new ConfigButton(
                        this.width - 130,
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

    public void selectChallenge() {
        ServerConfigCopy.get().challenge = selected_challenge;
        GrieferManhuntToolsClient.LOGGER.info("Challenge changed to: " + ServerConfigCopy.get().challenge);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.centeredText(
                this.font,
                Component.literal("Select Challenge"),
                this.width / 2,
                20,
                0xFFFFFFFF
        );

        graphics.pose().pushMatrix();
        graphics.pose().scale(2.0F, 2.0F);

        Component title = Component.literal("Challenge Title");

        switch (selected_challenge) {
            case "classic" -> {
                title = Component.literal("Classic").withColor(TextColor.GREEN);
            }
            case "survive" -> {
                title = Component.literal("Survive").withColor(TextColor.GREEN);
            }
            case "tva" -> {
                title = Component.literal("Tank VS Assassins").withColor(TextColor.AQUA);
            }
            case "netherite_assassins" -> {
                title = Component.literal("Netherite Assassins").withColor(TextColor.DARK_RED);
            }
            case "tag" -> {
                title = Component.literal("Manhunt Tag").withColor(TextColor.YELLOW);
            }
            case "timed" -> {
                title = Component.literal("Time Challenge").withColor(TextColor.BLUE);
            }
        }

        graphics.text(
                this.font,
                title,
                75,
                20,
                0xFFFFFFFF
        );

        graphics.pose().popMatrix();

        graphics.textWithWordWrap(
                this.font,
                description,
                150,
                60,
                this.width - 170,
                0xFFFFFFFF,
                true
        );
    }

    private Component loadDescription(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(
                "griefer-manhunt-tools",
                "challenges/" + name + ".json"
        );

        try {
            var resource = Minecraft.getInstance()
                    .getResourceManager()
                    .getResource(id);

            if (resource.isEmpty()) {
                return Component.literal("Missing description: " + name);
            }

            try (var reader = resource.get().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);

                return ComponentSerialization.CODEC
                        .parse(JsonOps.INSTANCE, json)
                        .getOrThrow();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Component.literal("Failed to load description: " + name);
        }
    }
}