/*
 * Fabric visual mods reference
 * Module: ViewModelModule.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;

public final class ViewModelModule {
    private ViewModelModule() {
    }

    public static void init() {
    }

    public static void apply(MatrixStack matrices, Hand hand) {
        VisualSettings s = VisualSettings.SHARED;
        if (hand == Hand.OFF_HAND && !s.viewOffhand()) {
            return;
        }
        float sign = hand == Hand.MAIN_HAND ? 1.0f : -1.0f;
        matrices.translate(s.viewX() * sign, s.viewY(), s.viewZ());
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(s.viewRotX()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(s.viewRotY() * sign));
        float scale = s.viewScale();
        matrices.scale(scale, scale, scale);
    }
}


