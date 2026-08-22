package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.Manager;
import ezalex.manhunt_tools.Timer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Classic implements Challenge {
    @Override
    public void start(MinecraftServer server) {
        Manager.getTimer().configureStopwatch();
    }

    @Override
    public void tick(MinecraftServer server) {

    }

    @Override
    public void preChallengeTick(MinecraftServer server) {

    }
}
