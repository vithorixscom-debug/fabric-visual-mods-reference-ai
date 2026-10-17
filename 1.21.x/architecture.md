<!--
Fabric visual mods reference
Module: architecture.md
SPDX-License-Identifier: MIT
-->

# Fabric 1.21.11

Yarn `1.21.11+build.6`, Java 21, Loom `1.17.17`, loader `0.19.3`, Fabric API `0.141.6+1.21.11`. Package `team.vixo.mc121`. The method list that compiled is `CONTRACT.md`. If this page and that file disagree, follow the contract.

## What a frame does

1. `GameRenderer.renderWorld` at `TAIL` calls `WorldRenderBus` with `RenderTickCounter.getTickProgress(true)` and `FrameClock.seconds()`.
2. Listeners draw tracers, jump rings, the hat, the trail, and the blur. Blur is off until its setting is on.
3. `InGameHud.render` at `TAIL` calls `HudRenderBus` with the `DrawContext`.
4. `HeldItemRenderer.renderFirstPersonItem` at `HEAD` offsets the hand pose.
5. `ClientPlayerEntity.tickMovement` at `TAIL` notices a jump and stores a ring.

`tickDelta` is the partial tick, a 0..1 sawtooth. `FrameClock.seconds()` is the gap since the previous world hook, in seconds, and returns 0.016 when that gap is negative or above 0.1. Do not divide `tickDelta` by 20 and call the result a frame duration.

## Draw calls that compiled

`Tessellator.getInstance().begin(VertexFormat.DrawMode, VertexFormat)`, then `buffer.end()`, then `RenderLayers.LINES.draw` or `RenderLayers.debugQuads().draw`.

`VertexFormat` is `com.mojang.blaze3d.vertex.VertexFormat`. `RenderLayers.DEBUG_QUADS` is private. `RenderSystem.enableBlend`, `ShaderProgramKeys`, and `BufferRenderer.drawWithGlobalProgram` are not in this Yarn build. Depth, blend, and cull go through `GL11` in `RenderStateGuard`. `GL11` does not update Minecraft's render-state cache.

Lines use `VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH`. The normal is the unit direction of the segment.

## State

`forOverlay` (tracers) turns the depth test off. `forTranslucentWorld` (hat, trail, jump rings) leaves it on. Both restore the bits `capture()` read. The matrix stack is `WorldMatrices.identity()`. A listener that `push()`es must `pop()` before it returns.

Camera-relative positions subtract `Camera.getCameraPos()`. Player positions use `getLerpedPos(tickDelta)`.
