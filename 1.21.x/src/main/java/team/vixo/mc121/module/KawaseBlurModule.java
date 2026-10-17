/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: KawaseBlurModule.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.vixo.mc121.render.FrameBuffer;
import team.vixo.mc121.render.ScreenQuad;
import team.vixo.mc121.render.ShaderProgram;
import team.vixo.mc121.render.ShaderSources;

public final class KawaseBlurModule {
    private static final Logger LOG = LoggerFactory.getLogger("vixoteam");
    private static final FrameBuffer PING = new FrameBuffer();
    private static final FrameBuffer PONG = new FrameBuffer();
    private static final int[] VIEW = new int[4];
    private static ShaderProgram down;
    private static ShaderProgram up;
    private static boolean ready;
    private static boolean loadFailed;
    private static int boundFbo;
    private static int boundTexture;
    private static int boundProgram;

    private KawaseBlurModule() {
    }

    public static void init() {
        WorldRenderBus.register(KawaseBlurModule::onWorld);
    }

    public static void shutdown() {
        PING.destroy();
        PONG.destroy();
        if (down != null) {
            down.destroy();
        }
        if (up != null) {
            up.destroy();
        }
        ScreenQuad.destroy();
        ready = false;
        loadFailed = false;
    }

    private static void onWorld(float tickDelta, float frameDt) {
        if (!VisualSettings.SHARED.blurEnabled()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        Framebuffer main = mc.getFramebuffer();
        if (main == null || loadFailed) {
            return;
        }
        if (!ready && !load(mc)) {
            return;
        }
        int fullW = main.textureWidth;
        int fullH = main.textureHeight;
        int halfW = Math.max(1, fullW / 2);
        int halfH = Math.max(1, fullH / 2);
        PING.ensureSize(halfW, halfH);
        PONG.ensureSize(halfW, halfH);
        readBinding();
        boolean depthOn = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
        boolean depthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        try {
            if (!(main.getColorAttachment() instanceof GlTexture glTexture)) {
                return;
            }
            PING.bind();
            down.bind();
            down.setInt("uSource", 0);
            down.setVec2("uTexel", 1.0f / fullW, 1.0f / fullH);
            bindTexture(glTexture.getGlId());
            ScreenQuad.draw(-1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);

            PONG.bind();
            down.setVec2("uTexel", 1.0f / halfW, 1.0f / halfH);
            bindTexture(PING.colorTexture());
            ScreenQuad.draw(-1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);

            PING.bind();
            up.bind();
            up.setInt("uSource", 0);
            up.setVec2("uTexel", 1.0f / halfW, 1.0f / halfH);
            up.setFloat("uOffset", 1.5f);
            bindTexture(PONG.colorTexture());
            ScreenQuad.draw(-1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);

            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, boundFbo);
            GL30.glViewport(VIEW[0], VIEW[1], VIEW[2], VIEW[3]);
            up.setVec2("uTexel", 1.0f / halfW, 1.0f / halfH);
            up.setFloat("uOffset", 0.5f);
            bindTexture(PING.colorTexture());
            ScreenQuad.draw(-1.0f, -1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f);
        } finally {
            if (depthOn) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDepthMask(depthMask);
            GL30.glUseProgram(boundProgram);
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, boundFbo);
            GL30.glViewport(VIEW[0], VIEW[1], VIEW[2], VIEW[3]);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexture);
        }
    }

    /**
     * Context state for the composite: framebuffer, viewport, texture, program.
     * How long these reads take was not measured. Do not publish a microsecond cost.
     * A size cache is wrong, because the same dimensions can be a different binding.
     */
    private static void readBinding() {
        boundFbo = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        GL30.glGetIntegerv(GL30.GL_VIEWPORT, VIEW);
        boundTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        boundProgram = GL30.glGetInteger(GL30.GL_CURRENT_PROGRAM);
    }

    private static void bindTexture(int texture) {
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
    }

    private static boolean load(MinecraftClient mc) {
        try {
            String vert = ShaderSources.read(mc, "shaders/screen_quad.vsh");
            down = new ShaderProgram(vert, ShaderSources.read(mc, "shaders/kawase_down.fsh"));
            up = new ShaderProgram(vert, ShaderSources.read(mc, "shaders/kawase_up.fsh"));
            ready = true;
            return true;
        } catch (Exception ex) {
            ready = false;
            loadFailed = true;
            LOG.warn("Kawase shaders did not compile; blur stays off", ex);
            return false;
        }
    }

}
