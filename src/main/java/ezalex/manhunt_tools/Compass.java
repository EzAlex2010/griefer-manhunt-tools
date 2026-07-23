package ezalex.manhunt_tools;

import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.LodestoneTracker;

import java.util.Optional;

public class Compass {
    public static ItemStack createCompass(ServerPlayer target) {
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
}
