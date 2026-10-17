/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: InGameHudMixin.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc121.module.FrameClock;
import visual.primer.mc121.module.HudRenderBus;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void primer$afterHud(DrawContext context, RenderTickCounter counter, CallbackInfo ci) {
        HudRenderBus.dispatch(context, FrameClock.seconds());
    }
}


