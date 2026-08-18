package ezalex.manhunt_tools.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import ezalex.manhunt_tools.TeamManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {
    @Redirect(
            method = "dropEquipment",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Inventory;dropAll()V"
            )
    )
    private void customKeepInventory(Inventory inventory) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (!shouldKeepInventory(player)) {
            inventory.dropAll();
        }
    }
    private boolean shouldKeepInventory(ServerPlayer player) {
        if (TeamManager.isHunter(player)) {
            return true;
        } else {
            return false;
        }
    }
}
