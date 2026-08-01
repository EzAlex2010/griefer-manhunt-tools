package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Manager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Classic {
    public static void start(MinecraftServer server) {
        Manager.getTimer().configureStopwatch();
    }

    public static void tick(MinecraftServer server) {

    }

    public static void setup(MinecraftServer server) {
        ConfigManager.get().showTimer = false;
    }
}
