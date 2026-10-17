<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: vector_and_matrix_math.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Vectors, quaternions, world-to-screen

## Camera rotation (view space)

Minecraft `Camera` supplies unit quaternion \(q = (x,y,z,w)\) (JOML). Conjugate \(q^{-1}\) rotates world offsets into camera space:

\[
\mathbf{v}_\text{cam} = q^{-1} \otimes \mathbf{v}_\text{world} \otimes q
\]

For manual path (see legacy `HitboxRenderer.project`): allocate `Vector3f rel`, `Quaternionf.conjugate()`, `rel.rotate(rot)`.

## Perspective projection to pixels (analytic fallback)

After camera-space \(\mathbf{v} = (v_x, v_y, v_z)\) with \(v_z < 0\) (forward):

\[
s = \frac{H/2}{\tan(\text{fov}/2) \cdot (-v_z)},\quad
x_\text{px} = \frac{W}{2} + v_x s,\quad
y_\text{px} = \frac{H}{2} - v_y s
\]

\(W,H\) = GUI-scaled framebuffer dimensions. This ignores aspect from non-default projection matrices (zoom, aspect-ratio modules).

## Full MVP path

1. Build \(P\) = perspective(fov, aspect, near, far).
2. Build \(V\) = rotation from camera quaternion (translation already subtracted in model matrix).
3. \(MVP = P \cdot V \cdot M_\text{model}\).
4. Homogeneous clip: \((x,y,z,w) = MVP \cdot (x,y,z,1)^T\).
5. NDC: \((x/w, y/w, z/w)\). Reject if \(w \le 0\) or any component outside \([-1,1]\) for strict culling.

Store matrices in column-major `float[16]` scratch; see `MathUtility.multiply4x4`.

## AABB screen bounds (2D ESP hull)

For axis-aligned box corners \(\mathbf{c}_i\), project each; take componentwise min/max of successful projections. Draw four `GuiGraphics.fill` lines. Cost: 8 projections × N entities per frame (CPU), zero extra draw calls vs full 3D line loop if only outline needed.

## Frustum plane extraction

From \(M = MVP\), plane \(i\): combine rows of \(M\) with signs \((+,+,+)\), \((-,+,+)\), etc. Normalize plane normal. Cull if all 8 corners on positive side.

## Large coordinates

At \(|\mathbf{P}| > 10^5\), subtract camera **first** in double precision, cast to float for matrix multiply:

\[
\mathbf{P}_\text{rel} = (\mathbf{P}_\text{double} - \mathbf{C}_\text{double})\text{.floatValue()}
\]

## Reference Java

`shared_render/java/repo/shared/math/MathUtility.java`
