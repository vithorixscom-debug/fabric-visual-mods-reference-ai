<!--
Fabric visual mods reference
Module: CONTRACT.md
SPDX-License-Identifier: MIT
-->

# 26.2 contract

Another implementation must match this behavior. Names below are the ones `javap` showed on the merged 26.2 jar and that `:26.x:compileJava` accepted. Do not rename them back to 1.21 Yarn names.

## Pin

Minecraft `26.2`. Loader `0.19.3`. Fabric API `0.154.0+26.2`. Java release 25. Loom plugin id `net.fabricmc.fabric-loom` version `1.17.21`. Dependencies are `implementation`, not `modImplementation`. There is no mappings line: the merged jar is already Mojmap-named.

26.3 is not this project. Loom reports `Failed to find official mojang mappings for 26.3`. The Mojang 26.3 version JSON has download keys `client` and `server` only.

## When to draw

`LevelRenderer.render` is injected at `HEAD`, before `finalizeGizmoCollection`. Open `collectPerFrameRenderThreadGizmos()` and close it before the method continues. Inside that scope call `Gizmos`. Outside it, `Gizmos` throws because no collector is registered.

`GameRenderer.renderLevel(DeltaTracker)` exists. This project does not inject there. The gizmo drain lives in `LevelRenderer.render`.

## Block under the crosshair

If `Minecraft.hitResult` is a `BlockHitResult` and `getType()` is `HitResult.Type.BLOCK`, submit one `Gizmos.cuboid(blockPos, GizmoStyle.stroke(0xFF66CCFF, 2))`. One cuboid. Not a loop of points across the block face.

## Other players

Walk `ClientLevel.entitiesForRendering`. Count only `Player` instances that are not the local player and are inside 64 blocks (`distanceToSqr` greater than `64*64` is skipped). At 48, `break`. For each accepted player:

1. `position()` is the tick position. `getPosition(partial)` is the render position. `partial` is `DeltaTracker.getGameTimeDeltaPartialTick(false)`.
2. Move `getBoundingBox()` by the difference `lerped - position` on x, y, and z. Submit that box with `Gizmos.cuboid(box, GizmoStyle.stroke(0xFF55FF88, 2), false)`.
3. One `Gizmos.line` from `GameRenderer.mainCamera().position()` to the lerped position plus half of `getBbHeight()`.

## Hat

On the local player only. Apex is feet plus `getBbHeight() + 0.35`. Rim is feet plus `getBbHeight() + 0.05`, radius `0.55`. 24 slices. Each slice is two lines: apex to rim point, and rim point to the next rim point. 48 lines. Feet come from `getPosition(partial)`.

## Trail

24 samples of the local player's `getPosition(partial)`. A sample is stored when it is more than 0.2 blocks from the previous one. `Vec3.x`, `y`, and `z` are final, and `LineGizmo` keeps the two `Vec3` references, so the ring stores the `Vec3` created with the sample. The draw loop passes those objects to `Gizmos.line`. `TrailSettings.SHARED.trailsEnabled()` and `trailColor()` (default `0xFF88CCFF`) control it. `count` and `cursor` go back to 0 when the setting is off, the player is not alive, `ClientLevel` changes, or the new sample is more than 64 blocks from the previous one.

## Jump rings

`LivingEntity.jumpFromGround` at `HEAD`. Record only when the instance is a `LocalPlayer`. Store x, y+0.015, z in a 32-slot ring. The next jump overwrites the oldest slot.

Each level render adds `DeltaTracker.getGameTimeDeltaTicks()` to the age. Drop the ring after 18 ticks. Radius is `0.4 + (age/18) * 1.2`. Submit `Gizmos.circle(pos, radius, GizmoStyle.stroke(0x66CCFFFF, 2))`. `CircleGizmo` itself tessellates 20 segments. Do not add a second point loop.

## HUD

`Hud.extractRenderState(GuiGraphicsExtractor, DeltaTracker)` at `TAIL`.

A bar at pixels `(8,8)-(132,28)`, fill `0xC0101820`, text `Visual` at `(14,12)` in white with shadow off.

If the crosshair hit is an `EntityHitResult` whose entity is a `LivingEntity` and not the local player: panel `(8,36)-(168,72)`, the entity name, a track `(14,56)-(154,64)` in `0xFF202028`, and a health fill whose width is `140 * clamp(health/maxHealth, 0, 1)`. Max health is at least 1.

## Hands

`ItemInHandRenderer.submitHandsWithItems(float, PoseStack, SubmitNodeCollector, LocalPlayer, int)` at `HEAD`. Translate `(0.10, -0.04, -0.12)` then scale `0.92` on all axes. Both hands share this stack. Do not invent a per-hand method; the public class does not have one.

## Not in this build

No Kawase pass. No skin blit. No separate off-hand sign. `genSourcesWithVineflower` fails because `syncTask` is unset. The game client was not launched.

## Проверка в игре

A local `build` is not a launched client. Do not mark a row «проверено» without a real launch. This branch has no Kawase pass, so there is no blur picture to start.

| Check | Status |
| --- | --- |
| Запуск клиента | не проверено |
| ESP | не проверено |
| Blur | не проверено (прохода Kawase в этой ветке нет) |
| HUD | не проверено |
| Рука | не проверено |
