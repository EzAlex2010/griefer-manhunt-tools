package ezalex.manhunt_tools;

import ezalex.manhunt_tools.challenges.Classic;
import ezalex.manhunt_tools.challenges.NetheriteAssassins;
import ezalex.manhunt_tools.challenges.TankVsAssassins;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Manager {
    public static int compassUpdateTicks = 0;
    public static int UPDATE_INTERVAL = 20;
    public static boolean challengeRunning = false;
    public static String challenge = "";
    public static MinecraftServer server;
    private static Timer timer = new Timer();
    private static String lastConfigChallenge = "";

    public static Timer getTimer() {
        return timer;
    }

    public static void setTimer(Timer newTimer) {
        timer = newTimer;
    }

    public static void setServer(MinecraftServer serverinstance) {
        server = serverinstance;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static void stopServer(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("SERVER_STOPPING: resetting Manager");
        challengeRunning = false;
        challenge = "";
        timer = new Timer();
        Manager.server = null;
    }

    public static void start(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Starting Game");
        Manager.challenge = ConfigManager.get().challenge;
        switch (Manager.challenge) {
            case "classic" -> {
                Classic.start(server);
            }
            case "netherite_assassins" -> {
                NetheriteAssassins.start(server);
            }
            case "tva" -> {
                TankVsAssassins.start(server);
            }
        }
        Manager.challengeRunning = true;
        GameData.save(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.setExperiencePoints(0);
            player.setExperienceLevels(0);
        }
    }

    public static void tick(MinecraftServer server) {
        String configuredChallenge = ConfigManager.get().challenge;
        if (!Manager.challengeRunning && !Objects.equals(configuredChallenge, lastConfigChallenge)) {
            GrieferManhuntTools.LOGGER.info(
                    "Challenge changed from {} to {}. Resetting inventories.",
                    lastConfigChallenge,
                    configuredChallenge
            );
            clearInventories(server);
            lastConfigChallenge = configuredChallenge;
        }
        Manager.compassUpdateTicks++;
        if (Manager.compassUpdateTicks >= Manager.UPDATE_INTERVAL) {
            Manager.compassUpdateTicks = 0;
            if (ConfigManager.get().giveHuntersCompass) {
                Compass.update(server);
            } else {
                TeamManager.getRunner(server); // Run a check for the runner anyway.
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    Compass.clear(player);
                }
            }
        }
        TeamManager.teamConfigs(server.getScoreboard());
        if (Manager.challengeRunning) {
            if (TeamManager.validRunnerFound) {
                whileChallengeRunning();
            } else {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    player.connection.send(
                            new ClientboundSetActionBarTextPacket(
                                    Component.literal("No Runner Found").withColor(TextColor.RED)
                            )
                    );
                }

            }
        } else {
            switch (configuredChallenge) {
                case "classic" -> {
                    Classic.preChallengeTick(server);
                }
                case "netherite_assassins" -> {
                    NetheriteAssassins.preChallengeTick(server);
                }
                case "tva" -> {
                    TankVsAssassins.preChallengeTick(server);
                }
            }
        }
    }

    public static void whileChallengeRunning() {
        ServerLevel end = Manager.server.getLevel(Level.END);
        if (end != null) {
            EnderDragonFight fight = end.getDragonFight();
            if (fight != null && fight.hasPreviouslyKilledDragon()) {
                GameDisplay.runnerWin(server);
            }
        }
        timer.tick();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            timer.showTo(player);
        }
        switch (challenge) {
            case "classic" -> {
                Classic.tick(server);
            }
            case "netherite_assassins" -> {
                NetheriteAssassins.tick(server);
            }
            case "tva" -> {
                TankVsAssassins.tick(server);
            }
        }
    }

    public static void timerDone() {
        switch (challenge) {
            case "classic" -> {

            }
            case "netherite_assassins" -> {
                GameDisplay.hunterWin(server);
            }
            case "tva" -> {
                GameDisplay.runnerWin(server);
            }
        }
    }

    public static void runnerDeath(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Runner Died");
        switch (challenge) {
            case "classic", "tva" -> {
                GameDisplay.hunterWin(server);
            }
            case "netherite_assassins" -> {
                // score system later maybe?
            }
        }
    }

    public static void clearInventories(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.getInventory().clearContent();
            player.setExperiencePoints(0);
            player.setExperienceLevels(0);
        }
    }
}
