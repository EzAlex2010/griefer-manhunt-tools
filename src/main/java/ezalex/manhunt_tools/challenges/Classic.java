package ezalex.manhunt_tools.challenges;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.nbt.CompoundTag;

import java.util.Optional;

public class Classic {
    public static ServerPlayer getRunner(MinecraftServer server) {
        ServerPlayer runner = null;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("runner")) {
                runner = player;
                return runner;
            }
        }
        System.err.println("Error: No player is on the runner team!");
        return null;
    }

    public static void start(MinecraftServer server) {
        give_items(server);
    }

    public static void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                player.getInventory().add(createCompass(server));
            }
        }
    }

    public static void update_compass(MinecraftServer server) {
        ServerPlayer runner = getRunner(server);
        if (runner == null) {
            return;
        }
        GlobalPos target = GlobalPos.of(
                runner.level().dimension(),
                runner.blockPosition()
        );
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    CustomData data = stack.get(DataComponents.CUSTOM_DATA);
                    if (data != null && data.copyTag().getBooleanOr("player_tracker", false)) {
                        stack.set(
                                DataComponents.LODESTONE_TRACKER,
                                new LodestoneTracker(Optional.of(target), false)
                        );
                    }
                }
                ItemStack offhand = player.getOffhandItem();
                CustomData data = offhand.get(DataComponents.CUSTOM_DATA);
                if (data != null && data.copyTag().getBooleanOr("player_tracker", false)) {
                    offhand.set(
                            DataComponents.LODESTONE_TRACKER,
                            new LodestoneTracker(Optional.of(target), false)
                    );
                }
            }
        }
    }
}
