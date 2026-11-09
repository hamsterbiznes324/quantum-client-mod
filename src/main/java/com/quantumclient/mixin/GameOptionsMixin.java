package com.quantumclient.mixin;

import com.quantumclient.QuantumClientMod;
import com.quantumclient.modules.impl.AntiFireOverlay;
import net.minecraft.client.option.GameOptions;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Inject(method = "shouldRenderEntityOutlines", at = @At("HEAD"))
    private void onRenderEntityOutlines(CallbackInfoReturnable<Boolean> cir) {
    }
}
