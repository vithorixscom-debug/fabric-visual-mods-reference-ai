/*
 * Fabric visual mods reference
 * Module: MathUtility.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.shared;

public final class MathUtility {
    private MathUtility() {
    }

    public static float clamp(float v, float lo, float hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    /** Unit direction of a segment. Returns false when the segment length is ~0. */
    public static boolean direction(float[] out, float x0, float y0, float z0, float x1, float y1, float z1) {
        float dx = x1 - x0;
        float dy = y1 - y0;
        float dz = z1 - z0;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0e-5f) {
            out[0] = 0.0f;
            out[1] = 1.0f;
            out[2] = 0.0f;
            return false;
        }
        float inv = 1.0f / len;
        out[0] = dx * inv;
        out[1] = dy * inv;
        out[2] = dz * inv;
        return true;
    }
}


