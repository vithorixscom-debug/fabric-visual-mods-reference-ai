/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: LineDrawer.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.render;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import visual.primer.shared.ColorUtility;
import visual.primer.shared.MathUtility;

public final class LineDrawer {
    private static final float[] DIR = new float[3];

    private LineDrawer() {
    }

    /** 1.21.11 submits through RenderLayer.draw(BuiltBuffer). BufferRenderer is gone. */
    public static void draw(MatrixStack stack, int argb, float[] segments, int segmentCount) {
        if (segmentCount <= 0) {
            return;
        }
        float r = ColorUtility.channel(argb, 16);
        float g = ColorUtility.channel(argb, 8);
        float b = ColorUtility.channel(argb, 0);
        float a = ColorUtility.channel(argb, 24);
        var buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.LINES, VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH);
        var entry = stack.peek();
        for (int i = 0; i < segmentCount; i++) {
            int o = i * 6;
            float x0 = segments[o];
            float y0 = segments[o + 1];
            float z0 = segments[o + 2];
            float x1 = segments[o + 3];
            float y1 = segments[o + 4];
            float z1 = segments[o + 5];
            MathUtility.direction(DIR, x0, y0, z0, x1, y1, z1);
            buffer.vertex(entry, x0, y0, z0).color(r, g, b, a).normal(DIR[0], DIR[1], DIR[2]).lineWidth(2.0f);
            buffer.vertex(entry, x1, y1, z1).color(r, g, b, a).normal(DIR[0], DIR[1], DIR[2]).lineWidth(2.0f);
        }
        RenderLayers.LINES.draw(buffer.end());
    }
}


