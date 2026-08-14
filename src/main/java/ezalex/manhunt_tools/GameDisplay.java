package ezalex.manhunt_tools;

import ezalex.manhunt_tools.networking.EndScreenPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.stream.Collectors;

public class GameDisplay {

    public static void runnerWin(MinecraftServer server) {
        GrieferManhuntTools.LOGGER.info("Runner Win");
        Manager.challengeRunning = false;

        ServerPlayer runner = TeamManager.getRunner(server);
        String runnerName = runner != null ? runner.getName().getString() : "Unknown";

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.connection.send(
                    new EndScreenPayload(
                            Component.literal("Runner Wins!").withStyle(style -> style.withColor(ChatFormatting.GREEN).withBold(true)), Component.literal(runnerName).withStyle(style -> style.withColor(ChatFormatting.DARK_GREEN).withBold(true))
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
        Manager.challengeRunning = false;

        String hunterNames = server.getPlayerList().getPlayers().stream().filter(TeamManager::isHunter).map(player -> player.getName().getString()).collect(Collectors.joining(", "));

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
}
