/*
 * Fabric visual mods reference
 * Module: RenderStateGuard.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.render;

import org.lwjgl.opengl.GL11;

/**
 * Reads depth, depth mask, cull, and blend at entry, then writes them back.
 * 1.21.11 RenderSystem does not expose those helpers, so the write is GL11.
 * GL11 does not update Minecraft's own state cache. The four reads can stall
 * on the driver; see COMPAT.md. A launched client has not confirmed that the
 * cache and the driver agree after this returns.
 */
public final class RenderStateGuard implements AutoCloseable {
    private final boolean depthTest;
    private final boolean depthMask;
    private final boolean cull;
    private final boolean blend;

    private RenderStateGuard() {
        this.depthTest = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        this.cull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        this.blend = GL11.glIsEnabled(GL11.GL_BLEND);
        this.depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
    }

    /** The four values that close() will write back. */
    public static RenderStateGuard capture() {
        return new RenderStateGuard();
    }

    /** Depth test stays on. Jump rings, the hat, and the trail use this. */
    public static void forTranslucentWorld(Runnable draw) {
        drawCaptured(draw, true);
    }

    /** Depth test off, so a line is not hidden by terrain. Tracers use this. */
    public static void forOverlay(Runnable draw) {
        drawCaptured(draw, false);
    }

    private static void drawCaptured(Runnable draw, boolean depthTest) {
        RenderStateGuard previous = capture();
        setDepthTest(depthTest);
        setDepthMask(false);
        setCull(false);
        setBlend(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        try {
            draw.run();
        } finally {
            previous.close();
        }
    }

    private static void setDepthTest(boolean enabled) {
        if (enabled) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    private static void setDepthMask(boolean enabled) {
        GL11.glDepthMask(enabled);
    }

    private static void setCull(boolean enabled) {
        if (enabled) {
            GL11.glEnable(GL11.GL_CULL_FACE);
        } else {
            GL11.glDisable(GL11.GL_CULL_FACE);
        }
    }

    private static void setBlend(boolean enabled) {
        if (enabled) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    @Override
    public void close() {
        setDepthTest(depthTest);
        setDepthMask(depthMask);
        setCull(cull);
        setBlend(blend);
    }
}
