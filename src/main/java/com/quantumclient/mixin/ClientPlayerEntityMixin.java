package com.quantumclient.mixin;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.impl.AntiFireOverlay;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    private void cancelFireOverlay(CallbackInfoReturnable<Boolean> cir) {
        AntiFireOverlay module = (AntiFireOverlay) QuantumClientMod.moduleManager.getModule("AntiFireOverlay");
        if (module != null && module.isEnabled()) {
            cir.setReturnValue(false);
        }
    }
}
