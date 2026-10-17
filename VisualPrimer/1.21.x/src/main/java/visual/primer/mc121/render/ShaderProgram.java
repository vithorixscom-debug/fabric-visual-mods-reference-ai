/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ShaderProgram.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.render;

import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class ShaderProgram {
    private final int programId;
    private final Map<String, Integer> locations = new HashMap<>();

    public ShaderProgram(String vertexSource, String fragmentSource) {
        int vs = compile(GL20.GL_VERTEX_SHADER, vertexSource);
        int fs = compile(GL20.GL_FRAGMENT_SHADER, fragmentSource);
        programId = GL30.glCreateProgram();
        GL30.glAttachShader(programId, vs);
        GL30.glAttachShader(programId, fs);
        GL30.glBindAttribLocation(programId, 0, "Position");
        GL30.glBindAttribLocation(programId, 1, "UV0");
        GL30.glLinkProgram(programId);
        GL30.glDeleteShader(vs);
        GL30.glDeleteShader(fs);
        if (GL30.glGetProgrami(programId, GL30.GL_LINK_STATUS) == 0) {
            throw new IllegalStateException(GL30.glGetProgramInfoLog(programId));
        }
    }

    private static int compile(int type, String source) {
        int shader = GL30.glCreateShader(type);
        GL30.glShaderSource(shader, source);
        GL30.glCompileShader(shader);
        if (GL30.glGetShaderi(shader, GL30.GL_COMPILE_STATUS) == 0) {
            String log = GL30.glGetShaderInfoLog(shader);
            GL30.glDeleteShader(shader);
            throw new IllegalStateException(log);
        }
        return shader;
    }

    public void bind() {
        GL30.glUseProgram(programId);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    public void setFloat(String name, float value) {
        int loc = loc(name);
        if (loc >= 0) {
            GL30.glUniform1f(loc, value);
        }
    }

    public void setVec2(String name, float x, float y) {
        int loc = loc(name);
        if (loc >= 0) {
            GL30.glUniform2f(loc, x, y);
        }
    }

    public void setVec4(String name, float x, float y, float z, float w) {
        int loc = loc(name);
        if (loc >= 0) {
            GL30.glUniform4f(loc, x, y, z, w);
        }
    }

    public void setInt(String name, int value) {
        int loc = loc(name);
        if (loc >= 0) {
            GL30.glUniform1i(loc, value);
        }
    }

    private int loc(String name) {
        return locations.computeIfAbsent(name, n -> GL30.glGetUniformLocation(programId, n));
    }

    public void destroy() {
        GL30.glDeleteProgram(programId);
    }
}


