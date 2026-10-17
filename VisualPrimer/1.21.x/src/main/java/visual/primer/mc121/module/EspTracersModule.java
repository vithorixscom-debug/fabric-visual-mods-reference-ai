/*
 * Fabric visual mods reference
 * Module: EspTracersModule.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import visual.primer.mc121.render.LineDrawer;
import visual.primer.mc121.render.RenderStateGuard;
import visual.primer.mc121.render.WorldMatrices;

public final class EspTracersModule {
    private static final int CAP = 48;
    private static final float[] SEGMENTS = new float[CAP * 13 * 6];
    private static final Vector3f FORWARD = new Vector3f();
    private static final Vector3f RIGHT = new Vector3f();
    private static final Vector3f UP = new Vector3f();
    private static final Quaternionf ROT = new Quaternionf();

    private EspTracersModule() {
    }

    public static void init() {
        WorldRenderBus.register(EspTracersModule::onWorld);
    }

    /**
     * {@code forOverlay} turns the depth test off for this pass and restores it after.
     * A box behind terrain is still drawn. The reject test is the camera's view cone.
     * The 48 cap stops the scan (break).
     */
    private static void onWorld(float tickDelta, float frameDt) {
        if (!VisualSettings.SHARED.tracersEnabled()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            return;
        }
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cam = camera.getCameraPos();
        ROT.set(camera.getRotation());
        FORWARD.set(0.0f, 0.0f, -1.0f).rotate(ROT);
        RIGHT.set(1.0f, 0.0f, 0.0f).rotate(ROT);
        UP.set(0.0f, 1.0f, 0.0f).rotate(ROT);
        int seg = 0;
        int drawn = 0;
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) {
                continue;
            }
            if (drawn >= CAP) {
                break;
            }
            Vec3d pos = player.getLerpedPos(tickDelta);
            double dx = pos.x - cam.x;
            double dy = pos.y - cam.y;
            double dz = pos.z - cam.z;
            if (!inView(mc, camera, player, tickDelta, dx, dy, dz)) {
                continue;
            }
            Box box = player.getBoundingBox().offset(pos.x - player.getX(), pos.y - player.getY(), pos.z - player.getZ());
            seg = putBox(SEGMENTS, seg, box, cam);
            seg = putLine(SEGMENTS, seg, 0.0f, 0.0f, 0.0f, (float) dx, (float) (dy + player.getHeight() * 0.5), (float) dz);
            drawn++;
        }
        if (seg == 0) {
            return;
        }
        MatrixStack stack = WorldMatrices.identity();
        int segments = seg;
        RenderStateGuard.forOverlay(() -> LineDrawer.draw(stack, VisualSettings.SHARED.boxColor(), SEGMENTS, segments));
    }

    /**
     * The body stays until its bounding sphere leaves the cone. The angle is the
     * options FOV times the focused player's public multiplier, then the water
     * and lava scale. Sprint and zoom live in that multiplier.
     */
    private static boolean inView(MinecraftClient mc, Camera camera, PlayerEntity player, float tickDelta, double dx, double dy, double dz) {
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 1.0e-4) {
            return true;
        }
        double nx = dx / len;
        double ny = dy / len;
        double nz = dz / len;
        double fz = nx * FORWARD.x + ny * FORWARD.y + nz * FORWARD.z;
        if (fz <= 0.0) {
            return false;
        }
        float fov = mc.options.getFov().getValue();
        if (camera.getFocusedEntity() instanceof AbstractClientPlayerEntity focused) {
            fov *= focused.getFovMultiplier(true, tickDelta);
        }
        CameraSubmersionType sub = camera.getSubmersionType();
        if (sub == CameraSubmersionType.WATER || sub == CameraSubmersionType.LAVA) {
            float scale = mc.options.getFovEffectScale().getValue().floatValue();
            fov *= MathHelper.lerp(scale, 1.0f, 0.85714287f);
        }
        double radius = Math.hypot(player.getWidth() * 0.5, player.getHeight() * 0.5);
        double margin = Math.atan2(radius, len);
        int w = Math.max(1, mc.getWindow().getFramebufferWidth());
        int h = Math.max(1, mc.getWindow().getFramebufferHeight());
        double limit = Math.PI * 0.5 - 0.01;
        double halfV = Math.min(Math.toRadians(fov) * 0.5, limit);
        double halfH = Math.atan(Math.tan(halfV) * (w / (double) h));
        double tanV = Math.tan(Math.min(halfV + margin, limit));
        double tanH = Math.tan(Math.min(halfH + margin, limit));
        double rx = nx * RIGHT.x + ny * RIGHT.y + nz * RIGHT.z;
        double uy = nx * UP.x + ny * UP.y + nz * UP.z;
        return Math.abs(rx / fz) <= tanH && Math.abs(uy / fz) <= tanV;
    }

    private static int putLine(float[] dst, int seg, float x0, float y0, float z0, float x1, float y1, float z1) {
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

    private static int putBox(float[] dst, int seg, Box box, Vec3d cam) {
        float x0 = (float) (box.minX - cam.x);
        float y0 = (float) (box.minY - cam.y);
        float z0 = (float) (box.minZ - cam.z);
        float x1 = (float) (box.maxX - cam.x);
        float y1 = (float) (box.maxY - cam.y);
        float z1 = (float) (box.maxZ - cam.z);
        seg = putLine(dst, seg, x0, y0, z0, x1, y0, z0);
        seg = putLine(dst, seg, x1, y0, z0, x1, y0, z1);
        seg = putLine(dst, seg, x1, y0, z1, x0, y0, z1);
        seg = putLine(dst, seg, x0, y0, z1, x0, y0, z0);
        seg = putLine(dst, seg, x0, y1, z0, x1, y1, z0);
        seg = putLine(dst, seg, x1, y1, z0, x1, y1, z1);
        seg = putLine(dst, seg, x1, y1, z1, x0, y1, z1);
        seg = putLine(dst, seg, x0, y1, z1, x0, y1, z0);
        seg = putLine(dst, seg, x0, y0, z0, x0, y1, z0);
        seg = putLine(dst, seg, x1, y0, z0, x1, y1, z0);
        seg = putLine(dst, seg, x1, y0, z1, x1, y1, z1);
        seg = putLine(dst, seg, x0, y0, z1, x0, y1, z1);
        return seg;
    }
}


