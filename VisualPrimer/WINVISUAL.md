<!--
Fabric visual mods reference
Module: WINVISUAL.md
SPDX-License-Identifier: MIT
-->

# Prompt: client visuals in this tree

This file repeats the repository prompt so the second Gradle build stays aligned with the root. If the two copies disagree, `../WINVISUAL.md` and the contracts win.

Write ordinary client visuals for Yarn `1.21.11+build.6` and Mojmap `26.2`. Follow `../1.21.x/CONTRACT.md` or `../26.x/CONTRACT.md`. Scope is overlays, HUD panels, first-person pose, settings, clocks, and session state. No input automation, no packet construction, no pathfinding executors.

## Module architecture

The client entrypoint only registers listeners. World pictures use the world bus and camera-relative coordinates. HUD text uses the HUD bus. Partial tick places a body. The frame clock ages a ring: `FrameClock.seconds()` on 1.21.11 (`0.016` when the gap is negative or above `0.1` seconds), `DeltaTracker.getGameTimeDeltaTicks()` on 26.2.

Fixed buffers. Clear samples when the world changes. Restore GL state before the listener returns. `visual.primer.shared` imports no Minecraft types.

## Visual modules

Tracers: other players, cap `48`, twelve edges plus one camera-to-body segment, depth test off for that pass only, then restored. Hat on 1.21.11: `24` slices on every client player, radius `0.58`, height `0.34`, depth test on. Hat on 26.2: local player only, rim radius `0.55`, `24` slices. Trail: `24` samples, step `0.2` blocks, break at `64` blocks. Jump rings: `32` slots, Y plus `0.015`, life `0.9` seconds on 1.21.11 (`t = age / 0.9`, radius `circleRadius * (0.3 + t)`, alpha `(1 - t)`), or `18` ticks and radius `0.4 + (age / 18) * 1.2` on 26.2. Blur exists only on 1.21.11, default off, four half-resolution passes, depth test off around the quads because clip-space `z` is `0`.

## HUD

1.21.11 forgets the crosshair entity after `3` seconds and eases health with the frame clock. 26.2 draws the bar and, for a living crosshair target, the name and a health fill `140 * clamp(health / maxHealth, 0, 1)`.

## Settings

Read the shared settings object during render. Blur defaults off. This build's default palette is the one in `VISUALPRIMER.md`. Do not copy the root mod's default colors into this mod.

## How to build

From this directory:

```
./gradlew :1.21.x:build
./gradlew :26.x:build
```

JDK 21 and JDK 25. A launched client is «не проверено».
