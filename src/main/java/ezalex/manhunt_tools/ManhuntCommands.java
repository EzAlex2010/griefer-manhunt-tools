package ezalex.manhunt_tools;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import ezalex.manhunt_tools.challenges.Classic;
import ezalex.manhunt_tools.networking.ConfigDataPayload;
import ezalex.manhunt_tools.networking.OpenChallengeScreenPayload;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.scores.PlayerTeam;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

public class ManhuntCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("manhunt")
                            .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
                            .then(Commands.literal("reset").executes(ManhuntCommands::reset_command))
                            .then(Commands.literal("config").executes(ManhuntCommands::config))
                            .then(Commands.literal("set")
                                    .then(boolSetting(
                                            "show_timer",
                                            "Show Timer",
                                            b -> ConfigManager.get().showTimer = b)
                                    )
                                    .then(boolSetting(
                                            "show_team_colors",
                                            "Show Team Colors",
                                            b -> ConfigManager.get().showTeamColors = b)
                                    )
                                    .then(boolSetting(
                                            "give_hunters_compass",
                                            "Give Hunters Compass",
                                            b -> ConfigManager.get().giveHuntersCompass = b)
                                    )
                                    .then(boolSetting(
                                            "hunter_friendly_fire",
                                            "Hunter Friendly Fire",
                                            b -> ConfigManager.get().hunterFriendlyFire = b)
                                    )
                                    .then(Commands.literal("compass_update_interval").then(Commands.argument("ticks", IntegerArgumentType.integer(1)).executes(ManhuntCommands::set_compass_update_interval)))
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
                            .then(Commands.argument("player", EntityArgument.player()).executes(ManhuntCommands::track))
            );
            dispatcher.register(
                    Commands.literal("compass").executes(ManhuntCommands::compass)
            );
        });
    }

    public static int reset_command(CommandContext<CommandSourceStack> context) {
        Path flag = Path.of("reset.flag");
        try {
            Files.writeString(flag, "reset");
        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to create reset flag", e);
            return 0;
        }
        GrieferManhuntTools.LOGGER.info("Generating new world");
        context.getSource().getServer().halt(false);
        return 1;
    }

    public static int join_hunters(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        PlayerTeam hunter = scoreboard.getPlayerTeam("hunter");
        if (hunter == null) {
            context.getSource().sendFailure(Component.literal("The hunter team does not exist."));
            return 0;
        }
        scoreboard.addPlayerToTeam(player.getScoreboardName(), hunter);
        context.getSource().sendSuccess(() -> Component.literal("You are now a hunter."), false);
        return 1;
    }

    public static int join_runner(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        PlayerTeam runner = scoreboard.getPlayerTeam("runner");
        if (runner == null) {
            context.getSource().sendFailure(Component.literal("The runner team does not exist."));
            return 0;
        }
        // Check if anyone is already on the runner team
        if (!runner.getPlayers().isEmpty()) {
            context.getSource().sendFailure(Component.literal("There is already a runner. To change the runner, have the runner either run /hunter or /leave."));
            return 0;
        }
        scoreboard.addPlayerToTeam(player.getScoreboardName(), runner);
        context.getSource().sendSuccess(() -> Component.literal("You are now the runner."), false);
        return 1;
    }

    public static int leave_team(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ServerScoreboard scoreboard = context.getSource().getServer().getScoreboard();
        scoreboard.removePlayerFromTeam(player.getScoreboardName());
        context.getSource().sendSuccess(() -> Component.literal("You have left the game"), false);
        return 1;
    }

    public static int compass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Compass.give(context.getSource().getServer(), player);
        return 1;
    }

    public static int track(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        //String targetName = String.valueOf(EntityArgument.getEntity(context, "player"));
        ServerPlayer target = (ServerPlayer) EntityArgument.getEntity(context, "player");
        if (target == null) {
            context.getSource().sendFailure(Component.literal("Player not found."));
            return 0;
        }
        Compass.setTarget(player.getUUID(), target.getUUID());
        context.getSource().sendSuccess(() -> Component.literal("Now Tracking " + target.getGameProfile().name()), false);
        return 1;
    }

    public static int challenge_select(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayNetworking.send(
                context.getSource().getPlayerOrException(),
                new OpenChallengeScreenPayload()
        );
        return 1;
    }

    public static int config(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayNetworking.send(
                context.getSource().getPlayerOrException(),
                new ConfigDataPayload(ConfigManager.get())
        );
        return 1;
    }

    public static int start_challenge(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (Objects.equals(ConfigManager.get().challenge, "classic")) {
            Classic.start(context.getSource().getServer());
            return 1;
        }
        return 0;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> boolSetting(String command, String displayName, Consumer<Boolean> setter) {
        return Commands.literal(command)
                .then(Commands.argument("bool", BoolArgumentType.bool())
                        .executes(ctx -> {
                            boolean value = BoolArgumentType.getBool(ctx, "bool");
                            setter.accept(value);
                            ConfigManager.save();
                            GrieferManhuntTools.LOGGER.info(displayName + " = " + value);
                            return 1;
                        }));
    }

    public static int set_compass_update_interval(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        ConfigManager.get().compassUpdateInterval = ticks;
        GrieferManhuntTools.LOGGER.info("Set Compass update interval to " + ticks);
        Manager.UPDATE_INTERVAL = ticks;
        Manager.ticks = 0;
        ConfigManager.save();
        return ticks;
    }
}
