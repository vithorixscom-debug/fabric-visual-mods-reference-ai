/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ChinaHatModule.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import team.vixo.mc121.render.LineDrawer;
import team.vixo.mc121.render.RenderStateGuard;
import team.vixo.mc121.render.WorldMatrices;

public final class ChinaHatModule {
    private static final int SEGMENTS = 24;
    private static final float[] LINES = new float[(SEGMENTS * 2) * 8 * 6];

    private ChinaHatModule() {
    }

    public static void init() {
        WorldRenderBus.register(ChinaHatModule::onWorld);
    }

    private static void onWorld(float tickDelta, float frameDt) {
        if (!VisualSettings.SHARED.hatEnabled()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            return;
        }
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getCameraPos();
        float radius = VisualSettings.SHARED.hatRadius();
        float height = VisualSettings.SHARED.hatHeight();
        int seg = 0;
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            Vec3d pos = player.getLerpedPos(tickDelta);
            float ox = (float) (pos.x - cam.x);
            float oy = (float) (pos.y - cam.y + player.getHeight() + 0.05);
            float oz = (float) (pos.z - cam.z);
            float prevX = ox + radius;
            float prevZ = oz;
            for (int i = 1; i <= SEGMENTS; i++) {
                double ang = (Math.PI * 2.0 * i) / SEGMENTS;
                float x = ox + (float) (Math.cos(ang) * radius);
                float z = oz + (float) (Math.sin(ang) * radius);
                seg = put(LINES, seg, ox, oy + height, oz, x, oy, z);
                seg = put(LINES, seg, prevX, oy, prevZ, x, oy, z);
                prevX = x;
                prevZ = z;
            }
        }
        if (seg == 0) {
            return;
        }
        MatrixStack stack = WorldMatrices.identity();
        int count = seg;
        RenderStateGuard.forTranslucentWorld(() -> LineDrawer.draw(stack, VisualSettings.SHARED.hatColor(), LINES, count));
    }

    private static int put(float[] dst, int seg, float x0, float y0, float z0, float x1, float y1, float z1) {
        int o = seg * 6;
        if (o + 6 > dst.length) {
            return seg;
        }
        dst[o] = x0;
        dst[o + 1] = y0;
        dst[o + 2] = z0;
        dst[o + 3] = x1;
        dst[o + 4] = y1;
        dst[o + 5] = z1;
        return seg + 1;
    }
}
