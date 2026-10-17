/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: FrameClock.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.module;

public final class FrameClock {
    private static long lastNs;

    private FrameClock() {
    }

    /** Seconds since the previous call. tickDelta is a 0..1 sawtooth and is not used here. */
    public static float seconds() {
        long now = System.nanoTime();
        float dt = lastNs == 0L ? 0.016f : (now - lastNs) / 1_000_000_000.0f;
        lastNs = now;
        if (dt < 0.0f || dt > 0.1f) {
            return 0.016f;
        }
        return dt;
    }
}
