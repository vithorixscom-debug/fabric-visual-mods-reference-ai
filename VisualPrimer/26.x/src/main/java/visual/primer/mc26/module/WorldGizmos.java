/*
 * Fabric visual mods reference
 * Module: WorldGizmos.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26.module;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * One cuboid is twelve edges. A block outline is that cuboid, not a cloud of points.
 * Gizmos.line is one segment. The tracer loop stops at 48.
 */
public final class WorldGizmos {
    public static final int TRACER_CAP = 48;
    private static final double TRACER_RANGE_SQR = 64.0 * 64.0;
    private static final int TRAIL = 24;
    private static final double BREAK_SQR = 64.0 * 64.0;
    private static final Vec3[] TRAIL_AT = new Vec3[TRAIL];
    private static int trailCount;
    private static int trailCursor;
    private static ClientLevel trailLevel;

    private WorldGizmos() {
    }

    public static void emit(LevelRenderer levelRenderer, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (!JumpMarks.bound() || mc.level == null || mc.player == null) {
            return;
        }
        try (var scope = levelRenderer.collectPerFrameRenderThreadGizmos()) {
            float partial = delta.getGameTimeDeltaPartialTick(false);
            outlineTargetBlock(mc);
            players(mc, partial);
            hat(mc, partial);
            trail(mc, partial);
            JumpMarks.emit(delta.getGameTimeDeltaTicks());
        }
    }

    private static void outlineTargetBlock(Minecraft mc) {
        if (mc.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            Gizmos.cuboid(hit.getBlockPos(), GizmoStyle.stroke(0xFF66CCFF, 2.0f));
        }
    }

    private static void players(Minecraft mc, float partial) {
        Vec3 camera = mc.gameRenderer.mainCamera().position();
        int drawn = 0;
        for (Entity entity : mc.level.entitiesForRendering()) {
            if (drawn >= TRACER_CAP) {
                break;
            }
            if (!(entity instanceof Player player) || player == mc.player) {
                continue;
            }
            if (player.distanceToSqr(mc.player) > TRACER_RANGE_SQR) {
                continue;
            }
            Vec3 now = player.position();
            Vec3 lerped = player.getPosition(partial);
            AABB box = player.getBoundingBox().move(lerped.x - now.x, lerped.y - now.y, lerped.z - now.z);
            Gizmos.cuboid(box, GizmoStyle.stroke(0xFF55FF88, 2.0f), false);
            Vec3 at = lerped.add(0.0, player.getBbHeight() * 0.5, 0.0);
            Gizmos.line(camera, at, 0xFF55FF88, 2.0f);
            drawn++;
        }
    }

    private static void clearTrail() {
        trailCount = 0;
        trailCursor = 0;
    }

    /** Vec3 coordinates are final. The ring keeps the instance created with the sample, and LineGizmo holds that reference. */
    private static void trail(Minecraft mc, float partial) {
        if (!TrailSettings.SHARED.trailsEnabled()) {
            clearTrail();
            return;
        }
        if (!mc.player.isAlive()) {
            trailLevel = mc.level;
            clearTrail();
            return;
        }
        if (trailLevel != mc.level) {
            trailLevel = mc.level;
            clearTrail();
        }
        Vec3 feet = mc.player.getPosition(partial);
        int previous = (trailCursor - 1 + TRAIL) % TRAIL;
        double gap = trailCount == 0 ? 0.0 : feet.distanceToSqr(TRAIL_AT[previous]);
        if (gap > BREAK_SQR) {
            clearTrail();
            gap = 0.0;
        }
        if (trailCount == 0 || gap > 0.04) {
            TRAIL_AT[trailCursor] = new Vec3(feet.x, feet.y, feet.z);
            trailCursor = (trailCursor + 1) % TRAIL;
            if (trailCount < TRAIL) {
                trailCount++;
            }
        }
        if (trailCount < 2) {
            return;
        }
        int color = TrailSettings.SHARED.trailColor();
        int start = (trailCursor - trailCount + TRAIL) % TRAIL;
        for (int i = 0; i < trailCount - 1; i++) {
            int a = (start + i) % TRAIL;
            int b = (start + i + 1) % TRAIL;
            Gizmos.line(TRAIL_AT[a], TRAIL_AT[b], color, 2.0f);
        }
    }

    /** Cone of 24 edges plus the rim. Each edge is one line, not a point strip. */
    private static void hat(Minecraft mc, float partial) {
        Player self = mc.player;
        Vec3 feet = self.getPosition(partial);
        double topY = feet.y + self.getBbHeight() + 0.35;
        double rimY = feet.y + self.getBbHeight() + 0.05;
        double radius = 0.55;
        int slices = 24;
        for (int i = 0; i < slices; i++) {
            double a0 = (Math.PI * 2.0 * i) / slices;
            double a1 = (Math.PI * 2.0 * (i + 1)) / slices;
            double x0 = feet.x + Math.cos(a0) * radius;
            double z0 = feet.z + Math.sin(a0) * radius;
            double x1 = feet.x + Math.cos(a1) * radius;
            double z1 = feet.z + Math.sin(a1) * radius;
            Gizmos.line(new Vec3(feet.x, topY, feet.z), new Vec3(x0, rimY, z0), 0xFFFFCC66, 2.0f);
            Gizmos.line(new Vec3(x0, rimY, z0), new Vec3(x1, rimY, z1), 0xFFFFCC66, 2.0f);
        }
    }
}


