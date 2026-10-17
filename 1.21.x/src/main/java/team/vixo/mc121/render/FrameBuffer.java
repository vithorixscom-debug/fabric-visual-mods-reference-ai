/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: FrameBuffer.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.render;

import org.lwjgl.opengl.GL30;

public final class FrameBuffer {
    private int fbo = -1;
    private int color = -1;
    private int width;
    private int height;

    public void ensureSize(int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        if (fbo != -1 && width == w && height == h) {
            return;
        }
        destroy();
        width = w;
        height = h;
        int previous = GL30.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        fbo = GL30.glGenFramebuffers();
        color = GL30.glGenTextures();
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, color);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_LINEAR);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_T, GL30.GL_CLAMP_TO_EDGE);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA8, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL30.GL_TEXTURE_2D, color, 0);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, previous);
    }

    public void bind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
        GL30.glViewport(0, 0, width, height);
    }

    public int colorTexture() {
        return color;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void destroy() {
        if (color != -1) {
            GL30.glDeleteTextures(color);
            color = -1;
        }
        if (fbo != -1) {
            GL30.glDeleteFramebuffers(fbo);
            fbo = -1;
        }
        width = 0;
        height = 0;
    }
}
