package ezalex.manhunt_tools;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.scores.Team;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GrieferManhuntTools implements ModInitializer {

	public static final String MOD_ID = "griefer-manhunt-tools";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ManhuntCommands.register();

		Manager.load();

		PayloadTypeRegistry.clientboundPlay().register(
				OpenChallengeScreenPayload.TYPE,
				OpenChallengeScreenPayload.CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				SetChallengePayload.TYPE,
				SetChallengePayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				SetChallengePayload.TYPE,
				(payload, context) -> {

					ConfigManager.get().challenge = payload.challenge();
					ConfigManager.save();

					LOGGER.info("Challenge set to {}", payload.challenge());
				}
		);

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			LOGGER.info("Attack callback fired");
			if (!(player instanceof ServerPlayer attacker)) {
				LOGGER.info("Not a ServerPlayer attacker");
				return InteractionResult.PASS;
			}

			if (!(entity instanceof ServerPlayer target)) {
				LOGGER.info("Target is not a ServerPlayer");
				return InteractionResult.PASS;
			}

			Team attackerTeam = attacker.getTeam();
			Team targetTeam = target.getTeam();

			boolean isRunner = attackerTeam != null && attackerTeam.getName().equals("runner");
			boolean isHunter = targetTeam != null && targetTeam.getName().equals("hunter");
			boolean running = ConfigManager.get().challengeRunning;

			LOGGER.info("isRunner={}, isHunter={}, running={}", isRunner, isHunter, running);

			if (isHunter && isRunner && !running) {
				LOGGER.info("About to start");
				Manager.start(attacker.level().getServer());
				LOGGER.info("hit start");
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});

		ServerTickEvents.END_SERVER_TICK.register(Manager::tick);

		//ServerLifecycleEvents.SERVER_STARTED.register(Manager::createTeams);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}



