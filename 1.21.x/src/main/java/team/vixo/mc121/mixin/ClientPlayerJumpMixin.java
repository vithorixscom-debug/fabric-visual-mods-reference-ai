/*
 * Fabric visual mods reference
 * Module: ClientPlayerJumpMixin.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.mixin;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.vixo.mc121.module.JumpCirclesModule;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerJumpMixin {
    @Unique
    private boolean vixo$wasOnGround = true;

    @Inject(method = "tickMovement", at = @At("TAIL"))
    private void vixo$detectJump(CallbackInfo ci) {
        ClientPlayerEntity self = (ClientPlayerEntity) (Object) this;
        boolean ground = self.isOnGround();
        if (vixo$wasOnGround && !ground && self.getVelocity().y > 0.0) {
            JumpCirclesModule.onPlayerJump(self.getX(), self.getY(), self.getZ());
        }
        vixo$wasOnGround = ground;
    }
}
