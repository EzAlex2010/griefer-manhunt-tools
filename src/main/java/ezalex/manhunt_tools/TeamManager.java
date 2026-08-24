package ezalex.manhunt_tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import org.apache.logging.log4j.core.jmx.Server;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static ezalex.manhunt_tools.Manager.server;

public class TeamManager {
    public static boolean validRunnerFound = false;
    private static final Path RUNNER_PATH = Path.of("config", "griefer-manhunt-tools-team-runners.json");
    private static final Path HUNTER_PATH = Path.of("config", "griefer-manhunt-tools-team-hunters.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Set<UUID> runners = new HashSet<>();
    private static Set<UUID> hunters = new HashSet<>();

    public static ServerPlayer getRunner(MinecraftServer server) {
        ServerPlayer runner;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("runner")) {
                runner = player;
                validRunnerFound = true;
                return runner;
            }
        }
        if (validRunnerFound) {
            GrieferManhuntTools.LOGGER.error("No player is on the runner team!"); // Only Print on first fail
        }
        validRunnerFound = false;
        return null;
    }

    public static void loadTeams() {
        try {
            Files.createDirectories(RUNNER_PATH.getParent());

            if (!Files.exists(RUNNER_PATH)) {
                return;
            }

            try (Reader reader = Files.newBufferedReader(RUNNER_PATH)) {
                runners = GSON.fromJson(reader, Set.class);
                if (runners == null) {
                    new HashSet<>();
                }
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to load team runner", e);
        }
        try {
            Files.createDirectories(HUNTER_PATH.getParent());

            if (!Files.exists(HUNTER_PATH)) {
                return;
            }

            try (Reader reader = Files.newBufferedReader(HUNTER_PATH)) {
                hunters = GSON.fromJson(reader, Set.class);
                if (hunters == null) {
                    new HashSet<>();
                }
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to load team hunter", e);
        }
        GrieferManhuntTools.LOGGER.error("Loaded teams successfully!");
        updateTeamConfigs(server);
    }

    public static void saveTeams() {
        try {
            Files.createDirectories(RUNNER_PATH.getParent());

            try (Writer writer = Files.newBufferedWriter(RUNNER_PATH)) {
                GSON.toJson(runners, writer);
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to save runner team", e);
        }

        try {
            Files.createDirectories(HUNTER_PATH.getParent());

            try (Writer writer = Files.newBufferedWriter(HUNTER_PATH)) {
                GSON.toJson(hunters, writer);
            }

        } catch (Exception e) {
            GrieferManhuntTools.LOGGER.error("Failed to save hunter team", e);
        }
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

    public static void updateTeamConfigs(MinecraftServer server) {
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

    public static void addRunner(UUID uuid) {
        removePlayer(uuid);
        runners.add(uuid);
        saveTeams();
    }

    public static void addHunter(UUID uuid) {
        removePlayer(uuid);
        hunters.add(uuid);
        saveTeams();
    }

    public static void removePlayer(UUID uuid) {
        runners.remove(uuid);
        hunters.remove(uuid);
        saveTeams();
    }
}
