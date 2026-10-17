<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: architecture.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Minecraft 26.2 rendering

Mojmap names are already on the merged jar. Yarn mappings for 26.2 and 26.3 are an empty list on Fabric meta. The 26.3 version JSON from Mojang has no `client_mappings` file. Loom 1.17.17 and 1.17.21 both stop with `Failed to find official mojang mappings for 26.3`. This project is pinned to 26.2, loader 0.19.3, fabric-api `0.154.0+26.2`, Java 25.

## Confirmed methods

Read from `javap` on `minecraft-merged-*-26.2.jar`.

| Need | Method |
| --- | --- |
| World entry | `GameRenderer.renderLevel(DeltaTracker)`. There is no `renderWorld`. |
| Level draw | `LevelRenderer.render(...)`. `finalizeGizmoCollection` runs inside that method, so gizmos are added at `HEAD`, before the drain. |
| Block outline | `Gizmos.cuboid(BlockPos, GizmoStyle.stroke(argb, width))`. `CuboidGizmo` is one box. |
| Segment | `Gizmos.line(Vec3, Vec3, argb, width)`. |
| Ring | `Gizmos.circle(Vec3, radius, style)`. `CircleGizmo` uses 20 segments (`bipush 20`). |
| Collector | `LevelRenderer.collectPerFrameRenderThreadGizmos()` returns an `AutoCloseable`. Without it, `Gizmos.addGizmo` throws "No GizmoCollector has been registered". |
| Camera | `Camera.position()`. |
| Partial tick | `DeltaTracker.getGameTimeDeltaPartialTick(boolean)`. |
| Hand | `ItemInHandRenderer.submitHandsWithItems(...)`. `renderFirstPersonItem` is absent. |
| HUD | `Hud.extractRenderState(GuiGraphicsExtractor, DeltaTracker)`. Fills go through `GuiGraphicsExtractor.fill`. |

## What this project draws

One cuboid on the targeted block (`GizmoStyle.stroke(0xFF66CCFF, 2)`). Other players, at most 48, inside 64 blocks: the bounding box moved by `getPosition(partial) - position()`, stroked `0xFF55FF88`, plus one line from `Camera.position()` to the lerped body center. The local hat is 24 slices, 48 lines, apex at feet plus height plus 0.35, rim at feet plus height plus 0.05, radius 0.55, color `0xFFFFCC66`. Jump rings: 32 slots, age in game-time delta ticks, gone after 18, radius `0.4 + t * 1.2`, `Gizmos.circle`. HUD bar plus a target panel when `hitResult` is an `EntityHitResult` on a living entity. Hands: `submitHandsWithItems` at `HEAD` translates `(0.10, -0.04, -0.12)` and scales `0.92` on the shared pose. Both hands move together. There is no Kawase pass. Channel order of the gizmo ints was not checked in a running game. `remapJar` is not a task on this loom pin. The jar is the compile output. The client was not launched.
