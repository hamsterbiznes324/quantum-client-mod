package com.quantumclient.mixin;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.impl.NoHurtCam;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "bobViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void cancelHurtCam(net.minecraft.client.util.math.MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        NoHurtCam module = (NoHurtCam) QuantumClientMod.moduleManager.getModule("NoHurtCam");
        if (module != null && module.isEnabled()) {
            ci.cancel();
        }
    }
}
