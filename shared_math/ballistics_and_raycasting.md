<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: ballistics_and_raycasting.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Ballistics and voxel traversal

## Projectile with gravity

State \(\mathbf{r}, \mathbf{v}\), gravity \(g\) along \(-Y\), optional drag \(k\):

\[
\mathbf{v}_{t+\Delta t} = \mathbf{v}_t + (0, -g, 0)\Delta t - k\|\mathbf{v}_t\|\mathbf{v}_t \Delta t
\]
\[
\mathbf{r}_{t+\Delta t} = \mathbf{r}_t + \mathbf{v}_t \Delta t
\]

Integrate with fixed \(\Delta t = 1/20\) s for prediction lines; render polyline in world space with camera-relative translation.

## Pearl / trident lead (visual debug line)

Target velocity \(\mathbf{v}_T\). Solve scalar \(t\) such that \(\|\mathbf{r}_0 + \mathbf{v}_P t - (\mathbf{r}_T + \mathbf{v}_T t)\|\) is minimal under constant \(\|\mathbf{v}_P\|\). Use iterative Newton on \(t\) for HUD tracer endpoint.

## Amanatides–Woo voxel ray

Ray \(\mathbf{o} + t\mathbf{d}\) through grid. Step axis with smallest \(t_\text{max}\) increment; update `BlockPos` integer cell. Terminate on solid block or max steps. Used for block-highlight ESP without scanning AABB brute force.

## A* (path overlay)

Heuristic Manhattan on block grid; open set as binary heap. Visualize path as `VertexConsumer` line strip in translucent pass. CPU cost bounded by `maxNodes` per frame.

## Ray–AABB intersection (entity pick)

Slab method on entity `Box` in double precision before float matrix multiply.
