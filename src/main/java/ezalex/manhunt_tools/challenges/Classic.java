package ezalex.manhunt_tools.challenges;

import ezalex.manhunt_tools.Compass;
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
    public static void start(MinecraftServer server) {
        give_items(server);
    }

    public static void give_items(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                player.getInventory().add(Compass.create(player));
            }
        }
    }
}
