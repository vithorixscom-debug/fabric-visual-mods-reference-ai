/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: WorldMatrices.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.render;

import net.minecraft.client.util.math.MatrixStack;

/** One shared stack. A listener that push()es must pop() before it returns. */
public final class WorldMatrices {
    private static final MatrixStack IDENTITY = new MatrixStack();

    private WorldMatrices() {
    }

    public static MatrixStack identity() {
        return IDENTITY;
    }
}
