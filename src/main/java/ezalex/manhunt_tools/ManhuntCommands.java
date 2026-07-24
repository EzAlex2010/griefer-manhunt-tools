package ezalex.manhunt_tools;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import ezalex.manhunt_tools.challenges.Classic;
import net.minecraft.world.scores.PlayerTeam;

public class ManhuntCommands {
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

    public static int track(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String targetName = StringArgumentType.getString(context, "player");
        ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayerByName(targetName);
        if (target == null) {
            context.getSource().sendFailure(Component.literal("Player not found."));
            return 0;
        }
        Compass.setTarget(player.getUUID(), target.getUUID());
        context.getSource().sendSuccess(() -> Component.literal("Now Tracking " + targetName), false);
        return 1;
    }

    public static int challenge_select(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayNetworking.send(
                context.getSource().getPlayerOrException(),
                new OpenChallengeScreenPayload()
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

    public static int set_compass_update_interval(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        int ticks = IntegerArgumentType.getInteger(context, "ticks");
        ConfigManager.get().compassUpdateInterval = ticks;
        GrieferManhuntTools.LOGGER.info("Set Compass update interval to " + ticks);
        GrieferManhuntTools.UPDATE_INTERVAL = ticks;
        GrieferManhuntTools.ticks = 0;
        ConfigManager.save();
        return ticks;
    }

    public static int set_give_hunters_compass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ConfigManager.get().giveHuntersCompass = BoolArgumentType.getBool(context, "bool");
        GrieferManhuntTools.LOGGER.info("Set Give Hunters compass to " + ConfigManager.get().giveHuntersCompass);
        ConfigManager.save();
        return 1;
    }

    public static int set_show_team_colors(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ConfigManager.get().showTeamColors = BoolArgumentType.getBool(context, "bool");
        GrieferManhuntTools.LOGGER.info("Set Show Team Colors to " + ConfigManager.get().showTeamColors);
        ConfigManager.save();
        return 1;
    }

    public static int set_hunter_friendly_fire(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ConfigManager.get().hunterfriendlyfire = BoolArgumentType.getBool(context, "bool");
        GrieferManhuntTools.LOGGER.info("Set Hunter Friendly Fire to " + ConfigManager.get().hunterfriendlyfire);
        ConfigManager.save();
        return 1;
    }
}
