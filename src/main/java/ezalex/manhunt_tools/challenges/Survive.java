package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.Manager;
import net.minecraft.server.MinecraftServer;

public class Survive {
    public static void start(MinecraftServer server) {
        Manager.getTimer().configureStopwatch();
    }

    public static void tick(MinecraftServer server) {}

    public static void preChallengeTick(MinecraftServer server) {}
}
