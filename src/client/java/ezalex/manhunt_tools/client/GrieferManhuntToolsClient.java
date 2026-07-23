package ezalex.manhunt_tools.client;

import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.OpenChallengeScreenPayload;
import net.fabricmc.api.ClientModInitializer;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;

public class GrieferManhuntToolsClient implements ClientModInitializer{
	public static final String MOD_ID = "griefer-manhunt-tools";
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (this.openChallengeScreen.consumeClick()) {
				Minecraft.getInstance().setScreenAndShow(
						new ChallengeScreen()
				);
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(
				OpenChallengeScreenPayload.TYPE,
				(payload, context) -> {
					context.client().setScreenAndShow(new ChallengeScreen());
				}
		);
	}

	KeyMapping.Category CATEGORY = KeyMapping.Category.register(
			Identifier.fromNamespaceAndPath(
					GrieferManhuntTools.MOD_ID,
					"general"
			)
	);

	KeyMapping openChallengeScreen = KeyMappingHelper.registerKeyMapping(
			new KeyMapping(
					"key.griefer-manhunt-tools.open_challenge",
					InputConstants.Type.KEYSYM,
					GLFW.GLFW_KEY_P,
					this.CATEGORY
			)
	);
}