package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Classic {
    public static Timer stopwatch = new Timer(Timer.Mode.STOPWATCH, 0, true);

    public static void start(MinecraftServer server) {
        stopwatch.start();
    }

    public static void tick(MinecraftServer server) {
        stopwatch.tick();
        if (ConfigManager.get().showTimer) {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                stopwatch.showTo(player);
            }
        }
    }
}
