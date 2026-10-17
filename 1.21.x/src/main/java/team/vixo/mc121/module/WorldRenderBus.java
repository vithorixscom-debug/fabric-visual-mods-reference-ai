/*
 * Fabric visual mods reference
 * Module: WorldRenderBus.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.module;

import java.util.ArrayList;
import java.util.List;

public final class WorldRenderBus {
    public interface Listener {
        void onWorld(float tickDelta, float frameDt);
    }

    private static final List<Listener> LISTENERS = new ArrayList<>();

    private WorldRenderBus() {
    }

    public static void register(Listener listener) {
        LISTENERS.add(listener);
    }

    public static void dispatch(float tickDelta, float frameDt) {
        for (int i = 0; i < LISTENERS.size(); i++) {
            LISTENERS.get(i).onWorld(tickDelta, frameDt);
        }
    }
}
