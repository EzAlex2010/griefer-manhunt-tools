package ezalex.manhunt_tools;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

import java.util.Optional;

public class Manager {
    public static int ticks = 0;
    public static int UPDATE_INTERVAL = 20;

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

    public static void load() {
        ConfigManager.load();
        UPDATE_INTERVAL = ConfigManager.get().compassUpdateInterval;

    }

    public static void tick(MinecraftServer server) {
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
    }
}
