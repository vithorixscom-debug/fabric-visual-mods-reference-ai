/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ClientPlayerJumpMixin.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc121.module.JumpCirclesModule;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerJumpMixin {
    @Unique
    private boolean primer$wasOnGround = true;

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void primer$detectJump(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        boolean ground = self.isOnGround();
        if (primer$wasOnGround && !ground && self.getVelocity().y > 0.0) {
            JumpCirclesModule.onPlayerJump(self.getX(), self.getY(), self.getZ());
        }
        primer$wasOnGround = ground;
    }
}


