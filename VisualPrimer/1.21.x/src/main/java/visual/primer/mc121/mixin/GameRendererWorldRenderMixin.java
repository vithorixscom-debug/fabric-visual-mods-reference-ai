/*
 * Fabric visual mods reference
 * Module: GameRendererWorldRenderMixin.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc121.module.FrameClock;
import visual.primer.mc121.module.WorldRenderBus;

@Mixin(GameRenderer.class)
public abstract class GameRendererWorldRenderMixin {
    @Inject(method = "renderWorld", at = @At("TAIL"))
    private void primer$afterWorld(RenderTickCounter counter, CallbackInfo ci) {
        WorldRenderBus.dispatch(counter.getTickProgress(true), FrameClock.seconds());
    }
}


