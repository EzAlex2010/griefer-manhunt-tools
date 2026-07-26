package ezalex.manhunt_tools;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.Commands;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class GrieferManhuntTools implements ModInitializer {
	public static int ticks = 0;
	public static int UPDATE_INTERVAL = 20;

	public static final String MOD_ID = "griefer-manhunt-tools";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
				Commands.literal("manhunt")
					.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
					.then(Commands.literal("reset").executes(ManhuntCommands::reset_command))
					.then(Commands.literal("challenges").executes(ManhuntCommands::challenge_select))
					.then(Commands.literal("set")
							.then(Commands.literal("compass_update_interval").then(Commands.argument("ticks", IntegerArgumentType.integer(1)).executes(ManhuntCommands::set_compass_update_interval)))
							.then(Commands.literal("show_team_colors").then(Commands.argument("bool", BoolArgumentType.bool()).executes(ManhuntCommands::set_show_team_colors)))
							.then(Commands.literal("give_hunters_compass").then(Commands.argument("bool", BoolArgumentType.bool()).executes(ManhuntCommands::set_give_hunters_compass)))
							.then(Commands.literal("hunter_friendly_fire").then(Commands.argument("bool", BoolArgumentType.bool()).executes(ManhuntCommands::set_hunter_friendly_fire)))
					)
					.then(Commands.literal("start").executes(ManhuntCommands::start_challenge))
			);
			dispatcher.register(
					Commands.literal("hunter").executes(ManhuntCommands::join_hunters)
			);
			dispatcher.register(
					Commands.literal("runner").executes(ManhuntCommands::join_runner)
			);
			dispatcher.register(
					Commands.literal("leave").executes(ManhuntCommands::leave_team)
			);
			dispatcher.register(
					Commands.literal("track")
							.then(Commands.argument("player", StringArgumentType.word()).suggests(new OnlinePlayerSuggestionProvider()).executes(ManhuntCommands::track))
			);
		});

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

		ConfigManager.load();

		UPDATE_INTERVAL = ConfigManager.get().compassUpdateInterval;

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (ConfigManager.get().giveHuntersCompass) {
				ticks++;
				if (ticks >= UPDATE_INTERVAL) {
					ticks = 0;
					Compass.update(server);
				}
			}
			ServerScoreboard scoreboard = server.getScoreboard();
			PlayerTeam runner = scoreboard.getPlayerTeam("runner");
			PlayerTeam hunters = scoreboard.getPlayerTeam("hunter");
			if (hunters != null && runner != null) {
				if (ConfigManager.get().showTeamColors) {
					runner.setColor(Optional.of(TeamColor.GREEN));
					hunters.setColor(Optional.of(TeamColor.RED));
				} else {
					runner.setColor(Optional.empty());
					hunters.setColor(Optional.empty());
				}
			}
		});

		ServerLifecycleEvents.SERVER_STARTED.register(GrieferManhuntTools::createTeams);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	public static void createTeams(MinecraftServer server) {
		ServerScoreboard scoreboard = server.getScoreboard();

		if (scoreboard.getPlayerTeam("hunter") == null) {
			PlayerTeam hunter = scoreboard.addPlayerTeam("hunter");
			// Optional settings:
			hunter.setColor(Optional.of(TeamColor.RED));
			//hunter.setAllowFriendlyFire(false);
			hunter.setSeeFriendlyInvisibles(true);
			GrieferManhuntTools.LOGGER.info("Created hunter team.");
		}

		if (scoreboard.getPlayerTeam("runner") == null) {
			PlayerTeam runner = scoreboard.addPlayerTeam("runner");
			runner.setColor(Optional.of(TeamColor.GREEN));
			GrieferManhuntTools.LOGGER.info("Created runner team.");
		}
	}

	public static ServerPlayer getRunner(MinecraftServer server) {
		ServerPlayer runner = null;
		for (ServerPlayer player : server.getPlayerList().getPlayers()) {
			if (player.getTeam() != null && player.getTeam().getName().equals("runner")) {
				runner = player;
				return runner;
			}
		}
		System.err.println("Error: No player is on the runner team!");
		return null;
	}
}



