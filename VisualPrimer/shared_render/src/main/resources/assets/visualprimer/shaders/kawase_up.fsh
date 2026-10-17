/*
 * Fabric visual mods reference
 * Module: kawase_up.fsh
 * SPDX-License-Identifier: MIT
 */
#version 150

uniform sampler2D uSource;
uniform vec2 uTexel;
uniform float uOffset;

in vec2 vUv;
out vec4 fragColor;

void main() {
    vec2 o = uTexel * uOffset;
    vec4 c = texture(uSource, vUv) * 4.0;
    c += texture(uSource, vUv + vec2(-o.x, -o.y));
    c += texture(uSource, vUv + vec2(o.x, -o.y));
    c += texture(uSource, vUv + vec2(-o.x, o.y));
    c += texture(uSource, vUv + vec2(o.x, o.y));
    fragColor = c / 8.0;
}


