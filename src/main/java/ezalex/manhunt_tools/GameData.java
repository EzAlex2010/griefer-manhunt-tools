package ezalex.manhunt_tools;

import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameData {
    public static void save(MinecraftServer server) {
        Timer timer = Manager.getTimer();
        GrieferManhuntTools.LOGGER.info(
                "SAVE: saving challengeRunning={}, challenge={}, mode={}, ticks={}, initialTicks={}",
                Manager.challengeRunning,
                Manager.challenge,
                timer.getMode(),
                timer.getTicks(),
                timer.getInitialTicks()
        );
        Path file = server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("challengeData.txt");
        String data = (Manager.challengeRunning ? "1" : "0") + "|" + Manager.challenge + "|" + (timer.getMode().toString()) + "|" + timer.getTicks() + "|" + timer.getInitialTicks();
        try {
            Files.writeString(file, data);
        } catch (IOException e) {
            GrieferManhuntTools.LOGGER.error("Failed to save challenge data", e);
        }
    }

    public static void load(MinecraftServer inputServer) {
        Manager.server = inputServer;
        Timer timer = Manager.getTimer();
        ConfigManager.load();
        Manager.UPDATE_INTERVAL = ConfigManager.get().compassUpdateInterval;
        Path file = Manager.server.getWorldPath(net.minecraft.world.level.storage.LevelResource.ROOT).resolve("challengeData.txt");
        if (Files.exists(file)) {
            String data;
            try {
                data = Files.readString(file);
            } catch (IOException e) {
                GrieferManhuntTools.LOGGER.error("Failed to load challenge data", e);
                return;
            }
            String[] parts = data.split("\\|");

            Manager.challengeRunning = parts[0].equals("1");
            Manager.challenge = parts[1];
            if (parts[2].equals(Timer.Mode.COUNTDOWN.toString())) {
                timer.configureCountdown(Long.parseLong(parts[4]));
            } else {
                timer.configureStopwatch();
            }
            timer.setTicks(Long.parseLong(parts[3]));
        }
    }
}
