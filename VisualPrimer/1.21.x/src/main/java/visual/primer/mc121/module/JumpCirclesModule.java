/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: JumpCirclesModule.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import visual.primer.mc121.render.RenderStateGuard;
import visual.primer.mc121.render.WorldMatrices;
import visual.primer.shared.ColorUtility;

public final class JumpCirclesModule {
    /** Hard cap. Oldest ring is overwritten. */
    public static final int CAP = 32;
    private static final double[] X = new double[CAP];
    private static final double[] Y = new double[CAP];
    private static final double[] Z = new double[CAP];
    private static final float[] AGE = new float[CAP];
    private static final boolean[] LIVE = new boolean[CAP];
    private static int cursor;
    private static ClientWorld world;

    private JumpCirclesModule() {
    }

    public static void init() {
        WorldRenderBus.register(JumpCirclesModule::onWorld);
    }

    public static void onPlayerJump(double x, double y, double z) {
        if (!VisualSettings.SHARED.circlesEnabled()) {
            return;
        }
        int i = cursor;
        cursor = (cursor + 1) % CAP;
        X[i] = x;
        Y[i] = y + 0.015;
        Z[i] = z;
        AGE[i] = 0.0f;
        LIVE[i] = true;
    }

    private static void clear() {
        for (int i = 0; i < CAP; i++) {
            LIVE[i] = false;
        }
    }

    private static void onWorld(float tickDelta, float frameDt) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            world = null;
            clear();
            return;
        }
        if (world != mc.world) {
            world = mc.world;
            clear();
        }
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getCameraPos();
        float base = VisualSettings.SHARED.circleRadius();
        int argb = VisualSettings.SHARED.circleColor();
        MatrixStack stack = WorldMatrices.identity();
        boolean any = false;
        for (int i = 0; i < CAP; i++) {
            if (!LIVE[i]) {
                continue;
            }
            AGE[i] += frameDt;
            if (AGE[i] > 0.9f) {
                LIVE[i] = false;
                continue;
            }
            any = true;
        }
        if (!any) {
            return;
        }
        RenderStateGuard.forTranslucentWorld(() -> {
            var buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            for (int i = 0; i < CAP; i++) {
                if (!LIVE[i]) {
                    continue;
                }
                float t = AGE[i] / 0.9f;
                float radius = base * (0.3f + t);
                float alpha = (1.0f - t) * ColorUtility.channel(argb, 24);
                float r = ColorUtility.channel(argb, 16);
                float g = ColorUtility.channel(argb, 8);
                float b = ColorUtility.channel(argb, 0);
                stack.push();
                stack.translate(X[i] - cam.x, Y[i] - cam.y, Z[i] - cam.z);
                var entry = stack.peek();
                buffer.vertex(entry, -radius, 0.0f, -radius).color(r, g, b, alpha);
                buffer.vertex(entry, -radius, 0.0f, radius).color(r, g, b, alpha);
                buffer.vertex(entry, radius, 0.0f, radius).color(r, g, b, alpha);
                buffer.vertex(entry, radius, 0.0f, -radius).color(r, g, b, alpha);
                stack.pop();
            }
            RenderLayers.debugQuads().draw(buffer.end());
        });
    }
}


