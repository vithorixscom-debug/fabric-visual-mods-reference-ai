<!--
Fabric visual mods reference
Module: PATTERNS.md
SPDX-License-Identifier: MIT
-->

# Draw patterns in this repository

These are the pictures this mod submits. Counts and colors are the fields in `VisualSettings` and the modules under `team.vixo.mc121`. The same ideas on 26.2 go through `Gizmos` and are listed in `26.x/CONTRACT.md`.

## Box and tracer

Up to 48 other players, then `break`. Skip the local player. Skip a player whose bounding sphere is outside the view cone. The cone uses `options.getFov()` times `AbstractClientPlayerEntity.getFovMultiplier(true, tickDelta)` on the focused player, then the water or lava scale.

The box is the bounding box shifted by `getLerpedPos(tickDelta) - getX/getY/getZ`. Twelve edges. One tracer from the camera origin in camera space to the body center. Positions in the buffer are already camera-relative. `forOverlay` disables the depth test for this draw and restores it after. The line normal is the unit direction of that segment.

## Hat

24 slices around every player from `ClientWorld.getPlayers()`, including the local player. Each slice is a cone edge plus a rim edge. Same line drawer as the tracer. `forTranslucentWorld` leaves the depth test on. On 26.2 the cone is the local player only; that version is `26.x/CONTRACT.md`.

## Trail

24 feet samples. A sample is stored when the feet move more than 0.2 blocks. The mesh is camera-relative lines, not a vertical ribbon. The count returns to 0 when the setting is off, the player is dead, the world changes, or the gap exceeds 64 blocks. Depth test stays on.

## Jump ring

32 slots, oldest overwritten. Y is the jump Y plus 0.015. Age uses the frame clock and ends at 0.9 seconds. The quad is XZ: `(±r, 0, ±r)` after translating by `position - camera`. It is not rotated 90 degrees around X. Submitted with `debugQuads()` and `POSITION_COLOR`. Depth test stays on. The slots clear when the world changes.

## Blur

Off by default. Downsample the main color into `PING`, downsample `PING` into `PONG`, upsample `PONG` into `PING`, then upsample `PING` into the framebuffer that was bound when the pass started. The last target is not `PONG`. The quad is clip-space `z = 0`, so the depth test is off for those four draws. The four binding reads were not timed.

## HUD panel

A rounded rectangle from `round_rect.fsh` (`sdRoundBox` and `fwidth`), then the player face from the player-list skin, then the name and an eased health bar. The panel is GUI space. It is not a world quad.

## Hands

A translation and a scale on the first-person pose, applied at the head of the hand renderer. The off-hand can be left unchanged.
