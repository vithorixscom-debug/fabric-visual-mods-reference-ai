/*
 * Fabric visual mods reference
 * Module: kawase_down.fsh
 * SPDX-License-Identifier: MIT
 */
#version 150

uniform sampler2D uSource;
uniform vec2 uTexel;

in vec2 vUv;
out vec4 fragColor;

void main() {
    vec2 h = uTexel * 0.5;
    vec4 c = texture(uSource, vUv + vec2(-h.x, -h.y));
    c += texture(uSource, vUv + vec2(h.x, -h.y));
    c += texture(uSource, vUv + vec2(-h.x, h.y));
    c += texture(uSource, vUv + vec2(h.x, h.y));
    fragColor = c * 0.25;
}
