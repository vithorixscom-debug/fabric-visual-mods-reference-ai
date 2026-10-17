/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: JumpMarks.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc26.module;

import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.phys.Vec3;

/**
 * Thirty-two rings. Vanilla CircleGizmo emits 20 segments, not a point cloud.
 */
public final class JumpMarks {
    public static final int CAP = 32;
    private static final double[] X = new double[CAP];
    private static final double[] Y = new double[CAP];
    private static final double[] Z = new double[CAP];
    private static final float[] AGE = new float[CAP];
    private static final boolean[] LIVE = new boolean[CAP];
    private static int cursor;
    private static boolean bound;

    private JumpMarks() {
    }

    public static void bind() {
        bound = true;
    }

    public static boolean bound() {
        return bound;
    }

    public static void add(Vec3 pos) {
        if (!bound) {
            return;
        }
        int i = cursor;
        cursor = (cursor + 1) % CAP;
        X[i] = pos.x;
        Y[i] = pos.y + 0.015;
        Z[i] = pos.z;
        AGE[i] = 0.0f;
        LIVE[i] = true;
    }

    public static void emit(float tickDt) {
        for (int i = 0; i < CAP; i++) {
            if (!LIVE[i]) {
                continue;
            }
            AGE[i] += tickDt;
            if (AGE[i] > 18.0f) {
                LIVE[i] = false;
                continue;
            }
            float t = AGE[i] / 18.0f;
            float radius = 0.4f + t * 1.2f;
            Gizmos.circle(new Vec3(X[i], Y[i], Z[i]), radius, GizmoStyle.stroke(0x66CCFFFF, 2.0f));
        }
    }
}
