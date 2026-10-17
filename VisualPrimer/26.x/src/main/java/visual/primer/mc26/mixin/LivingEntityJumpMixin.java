/*
 * Fabric visual mods reference
 * Module: LivingEntityJumpMixin.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc26.module.JumpMarks;

@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {
    @Inject(method = "jumpFromGround", at = @At("HEAD"))
    private void primer$jump(CallbackInfo ci) {
        if ((Object) this instanceof LocalPlayer player) {
            JumpMarks.add(player.position());
        }
    }
}


