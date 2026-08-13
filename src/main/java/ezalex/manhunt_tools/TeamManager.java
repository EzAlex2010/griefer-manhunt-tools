package ezalex.manhunt_tools;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

import java.util.Optional;

public class TeamManager {
    public static boolean validRunnerFound = false;

    public static ServerPlayer getRunner(MinecraftServer server) {
        ServerPlayer runner;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("runner")) {
                runner = player;
                validRunnerFound = true;
                return runner;
            }
        }
        GrieferManhuntTools.LOGGER.error("No player is on the runner team!");
        validRunnerFound = false;
        return null;
    }

    public static void createTeams(ServerScoreboard scoreboard) {
        if (scoreboard.getPlayerTeam("hunter") == null) {
            PlayerTeam hunter = scoreboard.addPlayerTeam("hunter");
            hunter.setSeeFriendlyInvisibles(true);
            GrieferManhuntTools.LOGGER.info("Created hunter team.");
        }
        if (scoreboard.getPlayerTeam("runner") == null) {
            PlayerTeam runner = scoreboard.addPlayerTeam("runner");
            GrieferManhuntTools.LOGGER.info("Created runner team.");
        }
    }

    public static void teamConfigs(ServerScoreboard scoreboard) {
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
            if (ConfigManager.get().hunterFriendlyFire) {
                hunters.setAllowFriendlyFire(true);
            } else {
                hunters.setAllowFriendlyFire(false);
            }
        } else {
            createTeams(scoreboard);
        }
    }

    public static boolean isRunner(ServerPlayer player) {
        return player.getTeam() != null && player.getTeam().getName().equals("runner");
    }

    public static boolean isHunter(ServerPlayer player) {
        return player.getTeam() != null && player.getTeam().getName().equals("hunter");
    }
}
