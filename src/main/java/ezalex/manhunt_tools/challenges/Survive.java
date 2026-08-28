package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.GameDisplay;
import ezalex.manhunt_tools.Manager;
import net.minecraft.server.MinecraftServer;

public class Survive implements Challenge {
    @Override
    public void start(MinecraftServer server) {
        Manager.getTimer().configureCountdown((ConfigManager.get().timerLength * 20L));
    }
    @Override
    public void tick(MinecraftServer server) {}
    @Override
    public void preChallengeTick(MinecraftServer server) {}

    @Override
    public void timerDone(MinecraftServer server) {
        GameDisplay.runnerWin(server);
    }

    @Override
    public void onRunnerDeath(MinecraftServer server) {
        GameDisplay.hunterWin(server);
    }
}
