/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: HudExtractMixin.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26.mixin;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc26.module.JumpMarks;
import visual.primer.mc26.module.TargetPanel;

@Mixin(Hud.class)
public class HudExtractMixin {
    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void primer$panel(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo ci) {
        if (!JumpMarks.bound()) {
            return;
        }
        graphics.fill(8, 8, 132, 28, 0xC0101820);
        graphics.text(Minecraft.getInstance().font, "VisualPrimer", 14, 12, 0xFFFFFFFF, false);
        TargetPanel.extract(graphics);
    }
}


