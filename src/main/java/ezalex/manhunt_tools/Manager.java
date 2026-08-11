package ezalex.manhunt_tools;

import ezalex.manhunt_tools.challenges.Classic;
import ezalex.manhunt_tools.challenges.NetheriteAssassins;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;
import net.minecraft.ChatFormatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Manager {
    public static int ticks = 0;
    public static int UPDATE_INTERVAL = 20;
    public static boolean challengeRunning = false;
    public static String challenge = "classic";

    private static Timer timer = new Timer();

    public static Timer getTimer() {
        return timer;
    }

    public static void setTimer(Timer newTimer) {
        timer = newTimer;
    }

    public static void save(MinecraftServer server) {
        Path file = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("challengeData.txt");
        String data = (challengeRunning ? "1" : "0") + "|" + (timer.getMode().toString()) + "|" + timer.getTicks() + "|" + timer.getInitialTicks();
        try {
            Files.writeString(file, data);
        } catch (IOException e) {
            e.printStackTrace();
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

    public static void load(MinecraftServer server) {
        ConfigManager.load();
        UPDATE_INTERVAL = ConfigManager.get().compassUpdateInterval;
        Path file = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("challengeData.txt");
        if (Files.exists(file)) {
            String data;
            try {
                data = Files.readString(file);
            } catch (IOException e) {
                GrieferManhuntTools.LOGGER.error("Failed to load challenge data", e);
                return;
            }
            String[] parts = data.split("\\|");

            challengeRunning = parts[0].equals("1");
            if (parts[1].equals(Timer.Mode.COUNTDOWN.toString())) {
                timer.configureCountdown(Long.parseLong(parts[3]));
            } else {
                timer.configureStopwatch();
            }
            timer.setTicks(Long.parseLong(parts[2]));
        }
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
        if (challengeRunning) {
            ServerLevel end = server.getLevel(Level.END);
            if (end != null) {
                EnderDragonFight fight = end.getDragonFight();
                if (fight != null && fight.hasPreviouslyKilledDragon()) {
                    runnerWin(server);
                }
            }
            timer.tick();
            if (ConfigManager.get().showTimer) {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    timer.showTo(player);
                }
            }
            switch (challenge) {
                case "classic": {
                    Classic.tick(server);
                }
                case "netherite_assassins": {
                    NetheriteAssassins.tick(server);
                }
            }
        } else {
            switch (ConfigManager.get().challenge) {
                case "challenge": {
                    break;
                }
                case "netherite_assassins": {
                    NetheriteAssassins.give_items(server);
                }
            }
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
        GrieferManhuntTools.LOGGER.info("Starting Game");
        challenge = ConfigManager.get().challenge;
        if (Objects.equals(challenge, "classic")) {
            Classic.start(server);
        } else if (Objects.equals(challenge, "netherite_assassins")) {
            NetheriteAssassins.start(server);
        }
        challengeRunning = true;
        save(server);
    }

    public static boolean isRunner(ServerPlayer player) {
        return player.getTeam() != null && player.getTeam().getName().equals("runner");
    }

    public static boolean isHunter(ServerPlayer player) {
        return player.getTeam() != null && player.getTeam().getName().equals("hunter");
    }

    public static void runnerWin(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Runner Win");
        challengeRunning = false;

        ServerPlayer runner = getRunner(server);
        String runnerName = runner != null ? runner.getName().getString() : "Unknown";

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(
                    new ClientboundSetTitleTextPacket(
                            Component.literal("Runner Wins!").withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true))
                    )
            );
            player.connection.send(
                    new ClientboundSetSubtitleTextPacket(
                            Component.literal(runnerName).withStyle(style -> style.withColor(ChatFormatting.DARK_GREEN).withBold(true))
                    )
            );
        }
    }

    public static void hunterWin(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Hunter Win");
        challengeRunning = false;

        String hunterNames = server.getPlayerList().getPlayers().stream().filter(Manager::isHunter).map(player -> player.getName().getString()).collect(Collectors.joining(", "));

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(
                    new ClientboundSetTitleTextPacket(
                            Component.literal("Hunters Win!").withStyle(style -> style.withColor(ChatFormatting.RED).withBold(true))
                    )
            );
            player.connection.send(
                    new ClientboundSetSubtitleTextPacket(
                            Component.literal(hunterNames).withStyle(style -> style.withColor(ChatFormatting.DARK_RED).withBold(true))
                    )
            );
        }
    }

    public static void runnerDeath(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Runner Died");
        switch (challenge) {
            case "classic":
                challengeRunning = false;
                hunterWin(server);
        }
    }
}
