/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: GameRendererWorldRenderMixin.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.vixo.mc121.module.FrameClock;
import team.vixo.mc121.module.WorldRenderBus;

@Mixin(GameRenderer.class)
public abstract class GameRendererWorldRenderMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void vixo$afterWorld(RenderTickCounter counter, CallbackInfo ci) {
        WorldRenderBus.dispatch(counter.getTickProgress(true), FrameClock.seconds());
    }
}
