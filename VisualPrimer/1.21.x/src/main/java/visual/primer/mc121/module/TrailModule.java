/*
 * Fabric visual mods reference
 * Module: TrailModule.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import visual.primer.mc121.render.LineDrawer;
import visual.primer.mc121.render.RenderStateGuard;
import visual.primer.mc121.render.WorldMatrices;

public final class TrailModule {
    private static final int CAP = 24;
    private static final double BREAK_SQR = 64.0 * 64.0;
    private static final double[] X = new double[CAP];
    private static final double[] Y = new double[CAP];
    private static final double[] Z = new double[CAP];
    private static final float[] LINES = new float[(CAP - 1) * 6];
    private static int count;
    private static int cursor;
    private static ClientWorld world;

    private TrailModule() {
    }

    public static void init() {
        WorldRenderBus.register(TrailModule::onWorld);
    }

    private static void clear() {
        count = 0;
        cursor = 0;
    }

    private static void onWorld(float tickDelta, float frameDt) {
        if (!VisualSettings.SHARED.trailsEnabled()) {
            clear();
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayerEntity player = mc.player;
        if (mc.world == null || player == null || !player.isAlive()) {
            world = mc.world;
            clear();
            return;
        }
        if (world != mc.world) {
            world = mc.world;
            clear();
        }
        Vec3d feet = player.getLerpedPos(tickDelta);
        double gap = count == 0 ? 0.0 : dist2(feet.x, feet.y, feet.z, X[prev()], Y[prev()], Z[prev()]);
        if (gap > BREAK_SQR) {
            clear();
            gap = 0.0;
        }
        if (count == 0 || gap > 0.04) {
            X[cursor] = feet.x;
            Y[cursor] = feet.y;
            Z[cursor] = feet.z;
            cursor = (cursor + 1) % CAP;
            if (count < CAP) {
                count++;
            }
        }
        if (count < 2) {
            return;
        }
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getCameraPos();
        int segments = 0;
        int start = (cursor - count + CAP) % CAP;
        for (int i = 0; i < count - 1 && segments < CAP - 1; i++) {
            int a = (start + i) % CAP;
            int b = (start + i + 1) % CAP;
            int o = segments * 6;
            LINES[o] = (float) (X[a] - cam.x);
            LINES[o + 1] = (float) (Y[a] - cam.y);
            LINES[o + 2] = (float) (Z[a] - cam.z);
            LINES[o + 3] = (float) (X[b] - cam.x);
            LINES[o + 4] = (float) (Y[b] - cam.y);
            LINES[o + 5] = (float) (Z[b] - cam.z);
            segments++;
        }
        int n = segments;
        MatrixStack stack = WorldMatrices.identity();
        RenderStateGuard.forTranslucentWorld(() -> LineDrawer.draw(stack, VisualSettings.SHARED.tracerColor(), LINES, n));
    }

    private static int prev() {
        return (cursor - 1 + CAP) % CAP;
    }

    private static double dist2(double x, double y, double z, double x1, double y1, double z1) {
        double dx = x - x1;
        double dy = y - y1;
        double dz = z - z1;
        return dx * dx + dy * dy + dz * dz;
    }
}


