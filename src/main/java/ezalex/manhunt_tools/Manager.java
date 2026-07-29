package ezalex.manhunt_tools;

import ezalex.manhunt_tools.challenges.Classic;
import ezalex.manhunt_tools.challenges.NetheriteAssassins;
import net.minecraft.commands.arguments.ResourceKeyArgument;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

import java.util.Objects;
import java.util.Optional;

public class Manager {
    public static int ticks = 0;
    public static int UPDATE_INTERVAL = 20;

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

    public static ServerPlayer getRunner(MinecraftServer server) {
        ServerPlayer runner = null;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("runner")) {
                runner = player;
                return runner;
            }
        }
        GrieferManhuntTools.LOGGER.error("No player is on the runner team!");
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
        } else {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                Compass.clear(player);
            }
        }
        teamConfigs(server.getScoreboard());
        if (Objects.equals(ConfigManager.get().challenge, "classic")) {
            Classic.tick(server);
        } else if (Objects.equals(ConfigManager.get().challenge, "netherite_assassins")) {
            NetheriteAssassins.tick(server);
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

    public static void start(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Start function started");
        if (Objects.equals(ConfigManager.get().challenge, "classic")) {
            Classic.start(server);
        } else if (Objects.equals(ConfigManager.get().challenge, "netherite_assassins")) {
            GrieferManhuntTools.LOGGER.info("selected Netherite Assassins start");
            NetheriteAssassins.start(server);
            GrieferManhuntTools.LOGGER.info("finished netherite assassins start");
        }
        GrieferManhuntTools.LOGGER.info("Start function finished");
    }
}
