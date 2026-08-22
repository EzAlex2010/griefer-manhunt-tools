package ezalex.manhunt_tools;

import ezalex.manhunt_tools.challenges.Classic;
import ezalex.manhunt_tools.challenges.NetheriteAssassins;
import ezalex.manhunt_tools.challenges.Survive;
import ezalex.manhunt_tools.challenges.TankVsAssassins;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.end.EnderDragonFight;

import java.util.Objects;

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
        ConfigManager.setRules();
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
            case "survive" -> {
                Survive.start(server);
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
                case "survive" -> {
                    Survive.preChallengeTick(server);
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
            case "survive" -> {
                Survive.tick(server);
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
            case "tva", "survive" -> {
                GameDisplay.runnerWin(server);
            }
        }
    }

    public static void runnerDeath(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Runner Died");
        switch (challenge) {
            case "classic", "tva", "survive" -> {
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
