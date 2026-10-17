/*
 * Fabric visual mods reference
 * Module: HudPass.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.module;

import net.minecraft.client.gui.DrawContext;

public interface HudPass {
    void render(DrawContext context, float frameDt);
}
