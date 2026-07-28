package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.Compass;
import ezalex.manhunt_tools.Timer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class Classic {
    public static Timer stopwatch = new Timer(Timer.Mode.STOPWATCH, 0, true);

    public static void start(MinecraftServer server) {
        stopwatch.start();
        give_items(server);
    }

    public static void tick(MinecraftServer server) {
        stopwatch.show(server);
    }

    public static void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                player.getInventory().add(Compass.create(player));
            }
        }
    }
}
