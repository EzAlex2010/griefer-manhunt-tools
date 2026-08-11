package ezalex.manhunt_tools.mixin;

import ezalex.manhunt_tools.GrieferManhuntTools;
import ezalex.manhunt_tools.Manager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderDragon.class)
public class EnderDragonDeathMixin {
    @Inject(method = "handleKillingBlow", at = @At("HEAD"))
    private void onDragonDeath(CallbackInfo ci) {
        EnderDragon dragon = (EnderDragon) (Object) this;
        MinecraftServer server = dragon.level().getServer();

        GrieferManhuntTools.LOGGER.info("dragon death starting");

        if (server != null && Manager.challengeRunning) {
            Manager.runnerWin(server);
        }
    }
}