/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ScreenQuad.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.render;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL30;

/** One clip-space quad. Position is attribute 0, UV0 is attribute 1. */
public final class ScreenQuad {
    private static int vao = -1;
    private static int vbo = -1;
    private static final FloatBuffer DATA = BufferUtils.createFloatBuffer(16);

    private ScreenQuad() {
    }

    public static void draw(float x0, float y0, float x1, float y1, float u0, float v0, float u1, float v1) {
        ensure();
        DATA.clear();
        put(x0, y0, u0, v0);
        put(x1, y0, u1, v0);
        put(x1, y1, u1, v1);
        put(x0, y1, u0, v1);
        DATA.flip();
        GL30.glBindVertexArray(vao);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferSubData(GL30.GL_ARRAY_BUFFER, 0, DATA);
        GL30.glDrawArrays(GL30.GL_TRIANGLE_FAN, 0, 4);
        GL30.glBindVertexArray(0);
    }

    private static void put(float x, float y, float u, float v) {
        DATA.put(x).put(y).put(u).put(v);
    }

    private static void ensure() {
        if (vao != -1) {
            return;
        }
        vao = GL30.glGenVertexArrays();
        vbo = GL30.glGenBuffers();
        GL30.glBindVertexArray(vao);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vbo);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, 16 * Float.BYTES, GL30.GL_DYNAMIC_DRAW);
        GL30.glEnableVertexAttribArray(0);
        GL30.glVertexAttribPointer(0, 2, GL30.GL_FLOAT, false, 16, 0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glVertexAttribPointer(1, 2, GL30.GL_FLOAT, false, 16, 8);
        GL30.glBindVertexArray(0);
    }

    public static void destroy() {
        if (vbo != -1) {
            GL30.glDeleteBuffers(vbo);
            vbo = -1;
        }
        if (vao != -1) {
            GL30.glDeleteVertexArrays(vao);
            vao = -1;
        }
    }
}
