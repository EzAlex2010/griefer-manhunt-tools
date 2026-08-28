package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.ConfigManager;
import ezalex.manhunt_tools.GameDisplay;
import ezalex.manhunt_tools.Manager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class Timed implements Challenge{
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
        Manager.getTimer().addTime((long) (20*60*30));
        for (ServerPlayer player: server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(Component.literal("Added 30 Minutes To The Timer").withColor(TextColor.YELLOW));
        }
    }
}
