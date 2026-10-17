<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: WINVISUAL.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Prompt: client visuals in this tree

Write ordinary client visuals for the two Fabric mods in this repository. Yarn `1.21.11+build.6` and Mojmap `26.2`. Follow `1.21.x/CONTRACT.md` or `26.x/CONTRACT.md` for the version you compile. If this page and a contract disagree, the contract is current.

Scope is overlays, HUD panels, first-person pose, settings, clocks, and session state. No input automation, no packet construction, no pathfinding executors (`CONTRIBUTING.md`).

A green `build` is not a launched game. Runtime claims stay «не проверено» until a client is actually started.

## Module architecture

`fabric.mod.json` is `"environment": "client"`. The client entrypoint only registers listeners. Do not create a shader, a texture, or a framebuffer there.

World pictures subscribe to the world bus. HUD text and the face texture subscribe to the HUD bus. A world line submitted from the HUD hook has the GUI matrix. A GUI fill submitted from the world hook is not a block outline.

Two clocks. The partial tick places a body between ticks. The frame clock ages a ring and eases a bar. On 1.21.11 that clock is `FrameClock.seconds()` (`nanoTime`, and `0.016` when the gap is negative or above `0.1` seconds). On 26.2 it is `DeltaTracker.getGameTimeDeltaTicks()`. Do not divide the partial tick by 20 and call the result a frame duration.

One shared settings object, read while the vertex or the uniform is built. Do not copy a color into a vertex at enable time and leave it there.

Fixed buffers only. A listener that keeps samples clears them when the client world changes, the player is dead, or the module is off. Restore depth, depth mask, cull, and blend through `RenderStateGuard` before the listener returns. `team.vixo.shared` and `visual.primer.shared` import no Minecraft types.

```java
public final class TrailModule {
    private static final int CAP = 24;

    public static void init() {
        WorldRenderBus.register(TrailModule::onWorld);
    }
}
```

## Visual modules

Counts below are the ones in this tree. Depth test: tracers use `forOverlay` (off, then restored). The hat, the trail, and the jump rings use `forTranslucentWorld` (left on). On 26.2 those pictures are `Gizmos` inside `collectPerFrameRenderThreadGizmos`. That path does not call `glDisable` for the depth test. Whether a gizmo is hidden by terrain is «не проверено».

**Boxes and tracers.** Other players only, cap `48`, then stop. Skip a player outside the view cone. Twelve edges of the bounding box moved by `lerped position - current position`, plus one segment from the camera origin to the body center. Coordinates are already camera-relative. The line normal is the unit direction of that segment.

**Hat, 1.21.11.** `24` slices on every player from the client player list, including the local player. Each slice is one cone edge and one rim edge. Radius `0.58`, height `0.34`, unless the shared settings say otherwise. Depth test stays on.

**Hat, 26.2.** Local player only. Apex is feet plus bounding-box height plus `0.35`. Rim is feet plus bounding-box height plus `0.05`, radius `0.55`. `24` slices, `48` lines.

**Trail.** `24` feet samples of the local player. Store a sample when it is more than `0.2` blocks from the previous one. Clear the count when the setting is off, the player is dead, the world changes, or the gap is more than `64` blocks. The mesh is camera-relative line segments, not a vertical ribbon. On 26.2 the ring stores the `Vec3` created with the sample, because the gizmo keeps that reference and the fields are final.

**Jump rings.** `32` slots, oldest overwritten. Y is the jump Y plus `0.015`. On 1.21.11 age uses the frame clock and the ring dies at `0.9` seconds. Radius is `circleRadius * (0.3 + t)` with `t = age / 0.9`. Alpha is `(1 - t)` times the color alpha. The quad is XZ, `(±r, 0, ±r)` after subtracting the camera. It is not rotated `90` degrees around X. Clear the slots when the world changes. On 26.2 age uses game-time delta ticks, the ring drops after `18` ticks, radius is `0.4 + (age / 18) * 1.2`, and the submit is `Gizmos.circle` (`20` segments inside that call). Do not add a second point loop.

**Blur, 1.21.11 only.** Default off. Half-resolution ping-pong: downsample into `PING`, downsample into `PONG`, upsample into `PING`, upsample `PING` onto the framebuffer that was bound at the start. The result is not left in `PONG`. The quad is clip-space `z = 0`, so the depth test is off for those four draws and restored after. Load shaders on the first enabled pass. 26.2 has no Kawase pass.

## HUD

1.21.11: crosshair entity panel. Rounded rectangle from `round_rect.fsh` (`sdRoundBox`, edge from `fwidth`), then the face from the player-list skin, then the name and a health bar eased with the frame clock. Forget the entity `3` seconds after the crosshair leaves it. Drop the entity, the skin, and the id when the world or the local player is missing. Clear the skin when the entity id changes.

26.2: a bar at pixels `(8, 8)-(132, 28)`. If the crosshair hit is a living entity that is not the local player, a panel at `(8, 36)-(168, 72)`, the entity name, a track `(14, 56)-(154, 64)`, and a health fill of width `140 * clamp(health / maxHealth, 0, 1)`. Max health is at least `1`. No skin blit on 26.2.

## Settings

One object, read during render. Blur defaults off. The other toggles in `VisualSettings` default on. Changing a color or a radius affects the next frame.

`VisualPrimer/` uses a different default palette in its own `VisualSettings` and in its 26.2 trail color. The root defaults stay the ones named in `1.21.x/CONTRACT.md` and `26.x/CONTRACT.md`. Do not mix the two palettes in one mod.

## How to build

From the repository root, JDK 21 for the first and JDK 25 for the second:

```
./gradlew :1.21.x:build
./gradlew :26.x:build
```

From `VisualPrimer/`, the same tasks. That directory is its own Gradle build (`settings.gradle` there, mod id `visualprimer`). Root `./gradlew build` does not compile it.

Loader `0.19.3`. Fabric API `0.141.6+1.21.11` on Yarn, `0.154.0+26.2` on Mojmap. Loom `1.17.17` and `1.17.21` (`net.fabricmc.fabric-loom` on 26.2).

Launched client, ESP, blur, HUD, and the hand pose: «не проверено».
