package ezalex.manhunt_tools;

import ezalex.manhunt_tools.networking.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
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

		ServerLifecycleEvents.SERVER_STARTING.register(Manager::load);

		PayloadTypeRegistry.clientboundPlay().register(
				OpenChallengeScreenPayload.TYPE,
				OpenChallengeScreenPayload.CODEC
		);

		PayloadTypeRegistry.clientboundPlay().register(
				ConfigDataPayload.TYPE,
				ConfigDataPayload.CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				SetChallengePayload.TYPE,
				SetChallengePayload.CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				SaveConfigPayload.TYPE,
				SaveConfigPayload.CODEC
		);

		PayloadTypeRegistry.serverboundPlay().register(
				RequestConfigPayload.TYPE,
				RequestConfigPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				SetChallengePayload.TYPE,
				(payload, context) -> {

					ConfigManager.get().challenge = payload.challenge();
					ConfigManager.save();

					LOGGER.info("Challenge set to {}", payload.challenge());
				}
		);

		ServerPlayNetworking.registerGlobalReceiver(
				SaveConfigPayload.TYPE,
				(payload, context) -> {
					if (!context.player().permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
						return;
					}

					ConfigManager.set(payload.config());
					ConfigManager.save();
				}
		);

		ServerPlayNetworking.registerGlobalReceiver(
				RequestConfigPayload.TYPE,
				(payload, context) -> {
					if (!context.player().permissions().hasPermission(Permissions.COMMANDS_ADMIN)) {
						return;
					}

					ServerPlayNetworking.send(
							context.player(),
							new ConfigDataPayload(ConfigManager.get())
					);
				}
		);

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!(player instanceof ServerPlayer attacker)) {
				return InteractionResult.PASS;
			}

			if (!(entity instanceof ServerPlayer target)) {
				return InteractionResult.PASS;
			}
			LOGGER.info("hit");

			LOGGER.info("isRunner={}, isHunter={}, running={}", Manager.isHunter(target), Manager.isRunner(attacker), Manager.challengeRunning);

			if (Manager.isHunter(target) && Manager.isRunner(attacker) && !Manager.challengeRunning) {
				LOGGER.info("start");
				Manager.start(attacker.level().getServer());
				return InteractionResult.SUCCESS;
			}
			return InteractionResult.PASS;
		});

		ServerTickEvents.END_SERVER_TICK.register(Manager::tick);

		ServerLifecycleEvents.SERVER_STOPPING.register(Manager::stopServer);

		ServerLifecycleEvents.BEFORE_SAVE.register((server, flush, force) -> {
			Manager.save(server);
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			LOGGER.info("AFTER_DEATH");
			if (entity instanceof ServerPlayer player) {
				if (Manager.isRunner(player)) {
					MinecraftServer server = player.level().getServer();
					if (Manager.challengeRunning) {
						Manager.runnerDeath(server);
					}
				}


			}
		});

		ServerLifecycleEvents.SERVER_STARTED.register(Manager::setServer);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}



