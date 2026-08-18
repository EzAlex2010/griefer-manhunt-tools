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

//@Mixin(ServerPlayer.class)
//public class PlayerMixin {
//    @Redirect(
//            method = "die",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/minecraft/server/level/ServerPlayer;dropAllDeathLoot(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;)V"
//            )
//    )
//    private void customDropAllDeathLoot(ServerPlayer player, ServerLevel level, DamageSource source) {
//        if (!TeamManager.isHunter(player)) {
//            ((LivingEntityInvoker) player).invokeDropAllDeathLoot(level, source);
//        }
//    }
//}
