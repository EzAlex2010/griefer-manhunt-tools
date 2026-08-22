package ezalex.manhunt_tools.client.screens;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import ezalex.manhunt_tools.client.ServerConfigCopy;
import ezalex.manhunt_tools.client.widgets.ConfigButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;

public class ChallengeScreen extends Screen {

    public ChallengeScreen() {
        super(Component.literal("Challenge Select"));
    }

    private Component description = Component.literal("Select A Challenge To View Its Description");

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

        graphics.centeredText(
                this.font,
                Component.literal("Select Challenge"),
                this.width / 2,
                20,
                0xFFFFFFFF
        );

        graphics.text(
                this.font,
                description,
                this.width / 2 - 100,
                40,
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