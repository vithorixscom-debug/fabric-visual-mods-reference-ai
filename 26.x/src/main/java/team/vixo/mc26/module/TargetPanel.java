/*
 * Fabric visual mods reference
 * Module: TargetPanel.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc26.module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public final class TargetPanel {
    private TargetPanel() {
    }

    public static void extract(GuiGraphicsExtractor graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.hitResult instanceof EntityHitResult hit)) {
            return;
        }
        if (!(hit.getEntity() instanceof LivingEntity living) || living == mc.player) {
            return;
        }
        graphics.fill(8, 36, 168, 72, 0xC0101820);
        graphics.text(mc.font, living.getName().getString(), 14, 42, 0xFFFFFFFF, false);
        float max = Math.max(1.0f, living.getMaxHealth());
        int width = Math.round(140.0f * Math.min(1.0f, living.getHealth() / max));
        graphics.fill(14, 56, 154, 64, 0xFF202028);
        graphics.fill(14, 56, 14 + width, 64, 0xFF55CC77);
    }
}
