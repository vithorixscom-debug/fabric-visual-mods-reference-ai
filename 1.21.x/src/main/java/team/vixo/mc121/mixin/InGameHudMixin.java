/*
 * Fabric visual mods reference
 * Module: InGameHudMixin.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.vixo.mc121.module.FrameClock;
import team.vixo.mc121.module.HudRenderBus;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void vixo$afterHud(DrawContext context, RenderTickCounter counter, CallbackInfo ci) {
        HudRenderBus.dispatch(context, FrameClock.seconds());
    }
}
