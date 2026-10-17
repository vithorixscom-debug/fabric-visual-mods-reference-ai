<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: interpolation_and_smoothing.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Interpolation and temporal filtering

## Linear frame interpolation (entity pose)

Given previous tick position \(\mathbf{P}_{\text{prev}}\) and current \(\mathbf{P}_{\text{curr}}\), partial tick \(\Delta t \in [0,1]\):

\[
\mathbf{P}_{\text{interp}} = \mathbf{P}_{\text{prev}} + (\mathbf{P}_{\text{curr}} - \mathbf{P}_{\text{prev}})\,\Delta t
\]

Subtract camera position **before** pushing `MatrixStack` translations:

\[
\mathbf{P}_{\text{cam}} = \mathbf{P}_{\text{interp}} - \mathbf{C}_{\text{pos}}
\]

Failure mode: using only \(\mathbf{P}_{\text{curr}}\) at 20 Hz logic with 240 Hz present doubles visual step size on fast movement.

## Exponential decay (HUD alpha, health bar)

\[
\alpha = 1 - e^{-\lambda \Delta t_{\text{sec}}},\quad v_{k+1} = v_k + \alpha (v^\* - v_k)
\]

`lambda` in units of \(\text{s}^{-1}\). Independent of display refresh when \(\Delta t_{\text{sec}}\) comes from `RenderTickCounter`.

## Cubic Bezier easing (1D)

Control points \(P_0,P_1,P_2,P_3\), parameter \(t\):

\[
B(t) = (1-t)^3 P_0 + 3(1-t)^2 t P_1 + 3(1-t)t^2 P_2 + t^3 P_3
\]

Map UI animation progress through precomputed \(P_1,P_2\) for ease-out HUD slide.

## Spring (second-order)

\[
\ddot{x} = -k(x - x^\*) - c\dot{x}
\]

Semi-implicit Euler per frame. Use single-element `float[]` scratch for velocity to avoid boxing.

## Jump circle radius envelope

For ring radius \(R(t) = R_0 + v_r t\) and alpha \(\alpha(t) = \max(0, 1 - t/T)\):

\[
\text{fill rate} \propto \frac{dR}{dt} \cdot \alpha \quad \Rightarrow \quad \text{fragment overdraw} \propto \pi R^2 \alpha
\]

Cap simultaneous rings to bound GPU fill.

## Reference Java

`shared_render/java/repo/shared/math/Interpolation.java`
