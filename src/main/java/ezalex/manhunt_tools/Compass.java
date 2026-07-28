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

import static ezalex.manhunt_tools.Manager.getRunner;

public class Compass {
    private static final Map<UUID, UUID> TRACKING = new HashMap<>();
    private static final Map<UUID, Map<ResourceKey<Level>, GlobalPos>> LAST_LOCATIONS = new HashMap<>();
    private static final Map<UUID, Integer> MISSING_COMPASS_TICKS = new HashMap<>();

    public static void setTarget(UUID hunter, UUID target) {
        TRACKING.put(hunter, target);
    }

    public static UUID getTarget(UUID hunter) {
        return TRACKING.get(hunter);
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
        compass.set(DataComponents.MAX_STACK_SIZE, 1);
        return compass;
    }

    public static void give(MinecraftServer server, ServerPlayer player) {
        if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
            ServerPlayer defaultTarget = getRunner(server);
            UUID targetUUID = getTarget(player.getUUID());
            if (targetUUID == null) {
                if (defaultTarget == null) {
                    return;
                }
                targetUUID = defaultTarget.getUUID();
            }
            ServerPlayer target = server.getPlayerList().getPlayer(targetUUID);
            if (target == null) {
                return;
            }
            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (isTrackingCompass(stack)) {
                    return;
                }
            }
            if (isTrackingCompass(player.getOffhandItem())) {
                return;
            }
            player.getInventory().add(create(target));
        }
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

    public static void updateLocations(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            LAST_LOCATIONS.computeIfAbsent(player.getUUID(), uuid -> new HashMap<>()).put(
                    player.level().dimension(),
                    GlobalPos.of(
                            player.level().dimension(),
                            player.blockPosition()
                    ));
        }
    }

    public static void update(MinecraftServer server) {
        updateLocations(server); // Update Positions

        // Find Target

        ServerPlayer defaultTarget = getRunner(server);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.getTeam() != null && player.getTeam().getName().equals("hunter")) {
                boolean hasTrackingCompass = false;
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

                //Check Inventory

                for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                    hasTrackingCompass = isHasTrackingCompass(hasTrackingCompass, targetPos, stack);
                }

                ItemStack offhand = player.getOffhandItem();
                hasTrackingCompass = isHasTrackingCompass(hasTrackingCompass, targetPos, offhand);

                // Final Logic

                if (hasTrackingCompass) {
                    MISSING_COMPASS_TICKS.remove(player.getUUID());
                } else {
                    int missing = MISSING_COMPASS_TICKS.getOrDefault(player.getUUID(), 0) + 1;
                    int updatesNeeded = Math.max(1, 100 / ConfigManager.get().compassUpdateInterval);

                    if (missing >= updatesNeeded) {
                        player.getInventory().add(create(target));
                        MISSING_COMPASS_TICKS.remove(player.getUUID());
                    } else {
                        MISSING_COMPASS_TICKS.put(player.getUUID(), missing);
                    }
                }
            }
        }
    }

    private static boolean isTrackingCompass(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);

        return stack.is(Items.COMPASS)
                && data != null
                && data.copyTag().getBooleanOr("player_tracker", false);
    }

    private static boolean isHasTrackingCompass(boolean hasTrackingCompass, GlobalPos targetPos, ItemStack stack) {
        if (isTrackingCompass(stack)) {
            if (hasTrackingCompass) {
                stack.setCount(0);
            } else {
                hasTrackingCompass = true;
                updateCompass(stack, targetPos);
            }
        }
        return hasTrackingCompass;
    }
}
