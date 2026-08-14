package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.networking.ConfigDataPayload;
import ezalex.manhunt_tools.networking.EndScreenPayload;
import ezalex.manhunt_tools.networking.OpenChallengeScreenPayload;
import net.fabricmc.api.ClientModInitializer;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrieferManhuntToolsClient implements ClientModInitializer{
	public static final String MOD_ID = "griefer-manhunt-tools";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	@Override
	public void onInitializeClient() {
		ClientConfigManager.load();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (this.openConfigScreen.consumeClick()) {
				Minecraft.getInstance().setScreenAndShow(
						new ConfigScreen(null)
				);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(
				OpenChallengeScreenPayload.TYPE,
				(payload, context) -> {
					context.client().setScreenAndShow(new ChallengeScreen());
				}
		);

		ClientPlayNetworking.registerGlobalReceiver(
				ConfigDataPayload.TYPE,
				(payload, context) -> {
					ServerConfigCopy.set(payload.config());
					context.client().setScreenAndShow(new ServerConfigScreen(ServerConfigCopy.parentScreen));
				}
		);

		ClientPlayNetworking.registerGlobalReceiver(
				EndScreenPayload.TYPE,
				(payload, context) -> {

					if (!ClientConfigManager.get().showEndScreen) {
						return;
					}

					Minecraft.getInstance().gui.hud.setTitle(
							payload.title()
					);

					Minecraft.getInstance().gui.hud.setSubtitle(
							payload.subtitle()
					);
				}
		);
	}

	KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(
					GrieferManhuntTools.MOD_ID,
					"general"
			)
	);

	KeyMapping openConfigScreen = KeyMappingHelper.registerKeyMapping(
			new KeyMapping(
					"key.griefer-manhunt-tools.open_config_screen",
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_P,
					this.CATEGORY
			)
	);
}