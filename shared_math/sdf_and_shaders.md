<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: sdf_and_shaders.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# SDF primitives and multi-pass blur

## Rounded rectangle SDF (fragment stage)

Half-extents \(\mathbf{b}\), corner radius \(r\), local coordinates \(\mathbf{p}\) centered on quad:

\[
\text{roundSDF}(\mathbf{p}, \mathbf{b}, r) = \|\max(|\mathbf{p}| - \mathbf{b}, 0)\| - r
\]

Anti-aliased alpha with screen-space derivative:

\[
\alpha = \bigl(1 - \mathrm{smoothstep}(-w, w, d)\bigr)\cdot a_\text{color},\quad w = \texttt{fwidth}(d)
\]

Single quad (6 indices) vs CPU `TRIANGLE_FAN` with \(N\) segments: vertex bandwidth \(\Delta V \propto N\) vs constant 4 vertices; fragment cost \(\propto\) quad area.

Full GLSL: `shared_render/src/main/resources/assets/vixoteam/shaders/round_rect.fsh`. The distance is `length(max(abs(p) - half, 0)) - radius`, then `fwidth` on that distance.

## Outline (SDF shell)

\[
\alpha_\text{outline} = \mathrm{smoothstep}(w, -w, |d| - t_\text{thick})
\]

## Kawase dual-filter blur

Downsample: `assets/vixoteam/shaders/kawase_down.fsh`, samples at half a texel. Upsample: `kawase_up.fsh`. Ping-pong two half-resolution targets. The vertex shader `screen_quad.vsh` writes clip-space position and does not multiply by a projection matrix.

Memory is the two half-size textures in video memory. The pass does not move that traffic across the bus as a CPU upload.

## Vertex shader

`screen_quad.vsh` passes `Position` and `UV0` in clip space. There is no `ProjMat` uniform in that file.

## Java wrapper requirements

| Step | API |
|------|-----|
| Compile | `glCreateShader`, `glShaderSource`, `glGetShaderInfoLog` on failure |
| Link | `glBindAttribLocation` for `Position` and `UV0` |
| Uniform cache | `HashMap<String,Integer>` + `glGetUniformLocation` |
| Draw | Bind VAO with 4 vertices, `GL_TRIANGLES`, 6 indices |

Implementations: `1.21.x/src/main/java/team/vixo/mc121/render/ShaderProgram.java` and `FrameBuffer.java`.

## Drop shadow (two-pass)

1. Render SDF shape into FBO A with enlarged quad offset.
2. Kawase blur FBO A → B.
3. Composite B under foreground SDF with premultiplied alpha blend `GL_ONE, GL_ONE_MINUS_SRC_ALPHA`.
