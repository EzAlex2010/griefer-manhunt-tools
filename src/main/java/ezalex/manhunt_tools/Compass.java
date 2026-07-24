package ezalex.manhunt_tools;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static ezalex.manhunt_tools.GrieferManhuntTools.getRunner;

public class Compass {
    private static final Map<UUID, UUID> TRACKING = new HashMap<>();
    private static final Map<UUID, Map<ResourceKey<Level>, GlobalPos>> LAST_LOCATIONS = new HashMap<>();

    public static void setTarget(UUID hunter, UUID target) {
        TRACKING.put(hunter, target);
    }

    public static UUID getTarget(UUID hunter) {
        return TRACKING.get(hunter);
    }

    public static void removeTarget(UUID hunter) {
        TRACKING.remove(hunter);
    }

    public static ItemStack create(ServerPlayer target) {
        if (target == null) {
            return new ItemStack(Items.COMPASS);
        }
        GlobalPos location = GlobalPos.of(
                target.level().dimension(),
                target.blockPosition()
        );
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("player_tracker", true);

        ItemStack compass = new ItemStack(Items.COMPASS);
        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(location),false));
        compass.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return compass;
    }

    private static void updateCompass(ItemStack stack, GlobalPos targetPos) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        if (data != null && data.copyTag().getBooleanOr("player_tracker", false)) {
            stack.set(
                    DataComponents.LODESTONE_TRACKER,
                    new LodestoneTracker(Optional.of(targetPos), false)
            );
        }
    }

    public static void update(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            LAST_LOCATIONS.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>()).put(
                player.level().dimension(),
                GlobalPos.of(
                    player.level().dimension(),
                    player.blockPosition()
                ));
        }

        ServerPlayer defaultTarget = getRunner(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                UUID targetUUID = getTarget(player.getUUID());
                if (targetUUID == null) {
                    if (defaultTarget == null) {
                        continue;
                    }
                    targetUUID = defaultTarget.getUUID();
                }
                ServerPlayer target = server.getPlayerList().getPlayer(targetUUID);
                if (target == null) {
                    continue;
                }
                Map<ResourceKey<Level>, GlobalPos> locations = LAST_LOCATIONS.get(target.getUUID());
                if (locations == null) {
                    continue;
                }
                GlobalPos targetPos = locations.get(player.level().dimension());
                if (targetPos == null) {
                    continue;
                }
                for (int i = 0; i < 9; i++) {
                    updateCompass(player.getInventory().getItem(i), targetPos);
                }
                ItemStack offhand = player.getOffhandItem();
                updateCompass(offhand, targetPos);
            }
        }
    }
}
