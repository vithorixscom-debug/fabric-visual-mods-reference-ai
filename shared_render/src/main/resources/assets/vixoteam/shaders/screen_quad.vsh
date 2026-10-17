/*
 * Fabric visual mods reference
 * Module: screen_quad.vsh
 * SPDX-License-Identifier: MIT
 */
#version 150

in vec2 Position;
in vec2 UV0;

out vec2 vUv;
out vec2 vLocal;

void main() {
    vUv = UV0;
    vLocal = Position;
    gl_Position = vec4(Position, 0.0, 1.0);
}
