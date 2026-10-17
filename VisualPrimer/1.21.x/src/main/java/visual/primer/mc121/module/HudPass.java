/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: HudPass.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import net.minecraft.client.gui.DrawContext;

public interface HudPass {
    void render(DrawContext context, float frameDt);
}


