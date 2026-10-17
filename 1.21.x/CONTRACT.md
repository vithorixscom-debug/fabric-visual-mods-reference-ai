<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: CONTRACT.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# 1.21.11 contract

Yarn `1.21.11+build.6`. Loader `0.19.3`. Fabric API `0.141.6+1.21.11`. Java 21. Loom `1.17.17`. Package `team.vixo.mc121`. Shared math is `team.vixo.shared` and has no Minecraft imports.

`:1.21.x:build` produced a remapped jar in an earlier run (`BUILD SUCCESSFUL in 8s`). Re-run that task after any edit and trust only the new log.

## Draw path that compiled

`Tessellator.getInstance().begin(VertexFormat.DrawMode, VertexFormat)` then `buffer.end()`, then a public layer draw.

`VertexFormat` is `com.mojang.blaze3d.vertex.VertexFormat`. It is not under `client.render`.

`RenderLayers.LINES` is public. `RenderLayers.DEBUG_QUADS` is private. The quad path that compiled is `RenderLayers.debugQuads().draw(buffer.end())`.

Lines use `VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH`, `vertex(MatrixStack.Entry, x, y, z)`, `color`, `normal`, `lineWidth(2)`. The normal is the unit direction of that segment from `MathUtility.direction`. It is not `(0,1,0)` unless the segment actually points up.

`RenderSystem.enableBlend`, `depthMask`, `disableCull`, `defaultBlendFunc`, `ShaderProgramKeys`, and `BufferRenderer.drawWithGlobalProgram` do not exist. Blend, depth, and cull go through `GL11` inside `RenderStateGuard`. On entry it reads `glIsEnabled` for depth, cull, and blend, and `glGetBoolean` for the depth mask, then writes those four values back. `GL11` still does not update Minecraft's render-state cache. A launched client has not checked that the cache matches the driver after the guard returns.

## World hook

Mixin `GameRenderer.renderWorld` passes the bus `tickDelta` from `RenderTickCounter.getTickProgress` and `frameDt` from `FrameClock.seconds()`. `FrameClock` uses `nanoTime`. If the gap is negative or above 0.1 seconds it returns 0.016. Do not divide `tickDelta` by 20 and call that a frame duration.

## Tracers and boxes

Players only, not the local player. Cap 48, then `break`. Skip a player whose bounding sphere is outside the view cone. The vertical angle is `options.getFov()` times `AbstractClientPlayerEntity.getFovMultiplier(true, tickDelta)` on the focused player, then the water or lava scale `lerp(fovEffectScale, 1, 0.85714287)`. The margin is `atan2(sphere radius, distance)` added to both half-angles before `tan`. `forOverlay` turns the depth test off for this pass and `capture()` writes the previous depth bit back. A line is not hidden by terrain. The normal on each `LINES` vertex is the unit direction of that segment. It is `(0, 1, 0)` only when the segment length is about zero.

Position is `getLerpedPos(tickDelta)`. Camera is `getCameraPos()`. The box is the bounding box shifted by `lerped - getX/getY/getZ`. One tracer from the camera origin in camera space to the body center. Coordinates in the line buffer are already camera-relative. The line pass calls `RenderStateGuard.forOverlay`, which calls `capture()` and then disables the depth test. The matrix stack is the one `WorldMatrices.identity()` instance. A new `MatrixStack` is not allocated for the frame.

## Jump rings

32 slots, oldest overwritten. Cleared when `ClientWorld` is null or changes. Y is the jump Y plus 0.015. Age uses `frameDt`, death at 0.9 seconds. `t` is age divided by 0.9. Radius is `circleRadius * (0.3 + t)`. Alpha is `(1 - t)` times the color's alpha channel. The quad is XZ: the four vertices are `(±r, 0, ±r)` after translating by `position - camera`. No 90-degree rotation around X. Submit with `debugQuads()` and `POSITION_COLOR`. Whether that layer's vertex format matches at runtime was not checked in a launched game.

## Hat

24 slices. Cone edge plus rim edge, camera-relative, same line drawer as the tracers. Gated by the hat setting.

## Hands

`HeldItemRenderer.renderFirstPersonItem` at `HEAD`. The compiled parameter list is player, tick delta, pitch, hand, swing, stack, equip progress, `MatrixStack`, `OrderedRenderCommandQueue`, light. Off-hand negates X translation and yaw. Scale is uniform. If the off-hand setting is off, the off hand is left unchanged.

## Target panel

Crosshair entity is `MinecraftClient.targetedEntity`. Forget it 3 seconds after it stops being the target, and clear the skin when the entity id changes. Health eases with `frameDt`. Skin identifier is `getNetworkHandler().getPlayerListEntry(uuid).getSkinTextures().body().texturePath()`. `getPlayerListEntry()` on the player entity is protected. Draw with `RenderPipelines.GUI_TEXTURED` from `net.minecraft.client.gl`, not `client.render`. The face sample is `drawTexture(pipeline, id, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight)`.

The rounded panel is the shared `round_rect` shader: pixel offset from center, `sdRoundBox`, `fwidth` for the edge. Do not build the rounded rectangle from a point loop or from an AWT image.

## Blur

Half-resolution ping-pong. The downsample shader samples at half a texel, not one texel past the pixel. The vertex shader writes clip-space `Position` and does not multiply by `ProjMat`. The color attachment is a `GpuTexture`; bind `GlTexture.getGlId()` only when the attachment is a `GlTexture`. `Framebuffer` has no public GL id, so each enabled pass reads the bound framebuffer, viewport, texture, and program and restores them in `finally`, including when the size did not change. Those four reads were not timed. Do not attach a microsecond cost to them. The fourth draw binds that saved framebuffer again and upsamples `PING` onto it. The result is not left in `PONG`. The quad is clip-space `z = 0`, so the depth test is off for the four draws and restored afterwards. Otherwise the world depth buffer rejects the composite. Bandwidth of the blur is local video memory, not the PCIe bus. The blur setting defaults off.

## Trail

24 feet samples of the local player from `getLerpedPos`. A new sample is stored when it is more than 0.2 blocks from the previous one. The segments are camera-relative lines. `count` and `cursor` go back to 0 when the trail setting is off, the player is not alive, `ClientWorld` changes, or the new sample is more than 64 blocks from the previous one.

## Mixins

There is no `@Overwrite` in this tree. Each hook is `@Inject` on a Yarn method name. Loom remaps that name. Do not switch the target to an intermediary `method_` string.

`ClientPlayerEntity.tickMovement` at `TAIL` detects a jump: the player was on the ground, is not on the ground now, and `getVelocity().y` is positive. That calls `JumpCirclesModule.onPlayerJump` with `getX/getY/getZ`. It is not an inject on a `jump` method.

`GameRenderer.renderWorld` at `TAIL` passes `RenderTickCounter.getTickProgress(true)` and `FrameClock.seconds()` to the world bus.

`InGameHud.render` at `TAIL` passes the `DrawContext` and the same frame clock to the HUD bus.

`HeldItemRenderer.renderFirstPersonItem` at `HEAD` is the hand pose, described above.

## What fails if copied from an older version

Screen-space min/max of eight projected corners is a rectangle around the projection, not the twelve edges of the block. A million points is a vertex upload, not an outline. `getSkinTextures()` is not a method on `AbstractClientPlayerEntity`. `Framebuffer.getColorAttachment()` is not an `int`.

## Проверка в игре

A local `build` is not a launched client. Do not mark a row «проверено» without a real launch.

| Check | Status |
| --- | --- |
| Запуск клиента | не проверено |
| ESP | не проверено |
| Blur | не проверено |
| HUD | не проверено |
| Рука | не проверено |
