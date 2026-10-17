/*
 * Fabric visual mods reference
 * Module: round_rect.fsh
 * SPDX-License-Identifier: MIT
 */
#version 150

// Rounded box SDF, Inigo Quilez (public domain / MIT-style article):
// https://iquilezles.org/articles/distfunctions2d/
uniform vec4 uColor;
uniform vec2 uHalfSize;
uniform float uRadius;

in vec2 vUv;
out vec4 fragColor;

float sdRoundBox(vec2 p, vec2 halfSize, float radius) {
    vec2 q = abs(p) - halfSize + radius;
    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;
}

void main() {
    float dist = sdRoundBox(vUv, uHalfSize, uRadius);
    float edge = fwidth(dist);
    float alpha = (1.0 - smoothstep(-edge, edge, dist)) * uColor.a;
    fragColor = vec4(uColor.rgb, alpha);
}
