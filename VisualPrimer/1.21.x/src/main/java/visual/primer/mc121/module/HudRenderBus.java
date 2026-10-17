/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: HudRenderBus.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.DrawContext;

public final class HudRenderBus {
    private static final List<HudPass> LISTENERS = new ArrayList<>();

    private HudRenderBus() {
    }

    public static void register(HudPass pass) {
        LISTENERS.add(pass);
    }

    public static void dispatch(DrawContext context, float frameDt) {
        for (int i = 0; i < LISTENERS.size(); i++) {
            LISTENERS.get(i).render(context, frameDt);
        }
    }
}


