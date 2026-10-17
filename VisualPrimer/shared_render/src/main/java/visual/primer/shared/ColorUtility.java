/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ColorUtility.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.shared;

public final class ColorUtility {
    private ColorUtility() {
    }

    public static int argb(int a, int r, int g, int b) {
        return (a & 255) << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
    }

    public static int multiplyAlpha(int argb, float factor) {
        int a = (argb >>> 24) & 255;
        int na = Math.min(255, Math.max(0, Math.round(a * factor)));
        return (na << 24) | (argb & 0x00FFFFFF);
    }

    public static float channel(int argb, int shift) {
        return ((argb >> shift) & 255) / 255.0f;
    }
}


