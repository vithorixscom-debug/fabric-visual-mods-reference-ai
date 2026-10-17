<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: MISTAKES.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Troubleshooting

Symptom, cause, fix. Each entry is a failure this tree actually hit while being written.

## Compile errors

**Symptom:** `BufferRenderer`, `ShaderProgramKeys`, `RenderSystem.enableBlend`, or `RenderLayer.getLines()` does not resolve on Yarn `1.21.11+build.6`.
**Cause:** those names are absent on that Yarn build. A name that was right on another version is not evidence.
**Fix:** `Tessellator.getInstance().begin(...)`, then `RenderLayers.LINES.draw` or `RenderLayers.debugQuads().draw`. `VertexFormat` is `com.mojang.blaze3d.vertex.VertexFormat`. `DEBUG_QUADS` is private.

**Symptom:** `GameRenderer.renderWorld` or `HeldItemRenderer.renderFirstPersonItem` does not resolve on 26.2.
**Cause:** 26.2 has neither method.
**Fix:** the world hook is `LevelRenderer.render` at `HEAD`, inside `collectPerFrameRenderThreadGizmos`. Hands are `ItemInHandRenderer.submitHandsWithItems`. Both hands share one pose.

**Symptom:** a 26.3 setup reports no mappings.
**Cause:** Loom `1.17.17` and `1.17.21` have no official mappings for 26.3.
**Fix:** stay on 26.2 until mappings exist.

**Symptom:** `Camera.getCameraPos()` is missing on 26.2, or `Camera.position()` is missing on 1.21.11.
**Cause:** the camera accessor was renamed between the two mappings.
**Fix:** `Camera.getCameraPos()` on 1.21.11, `Camera.position()` on 26.2. `options.getFov()` is the slider. The sprint and zoom factor on 1.21.11 is `AbstractClientPlayerEntity.getFovMultiplier(true, tickDelta)`.

## Motion and timing on screen

**Symptom:** easing runs at the wrong speed.
**Cause:** `tickDelta` divided by 20 was treated as a frame duration. `tickDelta` is a partial tick in `0..1`.
**Fix:** age and health easing use `FrameClock.seconds()` on 1.21.11 and `DeltaTracker.getGameTimeDeltaTicks()` on 26.2.

**Symptom:** a drawn body steps between ticks.
**Cause:** it was drawn at `getX()`.
**Fix:** draw at `getLerpedPos(tickDelta)`.

**Symptom:** a marker appears to orbit the player when the camera moves.
**Cause:** the drawn position still includes the camera position.
**Fix:** subtract the camera position before projecting.

**Symptom:** the trail draws a line across a teleport.
**Cause:** samples were not cleared on world change, death, or a gap over 64 blocks.
**Fix:** clear in all three cases, and when the setting is disabled (`TrailModule`).

**Symptom:** every 26.2 gizmo segment ends at the same point.
**Cause:** one reused `Vec3` pair. `Gizmos.line` keeps the instances, and `Vec3` fields are final.
**Fix:** a fresh `Vec3` per segment.

## GL state

**Symptom:** the line pass renders with a wrong direction.
**Cause:** the normal was hard-coded to `(0, 1, 0)`.
**Fix:** the normal is the unit direction of that segment. `(0, 1, 0)` is only the fallback for a segment of about zero length. `RenderLayers.LINES` uses that normal as the line direction.

**Symptom:** a comment claims the tracer depth test stays on.
**Cause:** the comment is stale.
**Fix:** tracers call `forOverlay` (depth test off, restored on close). The hat, the trail, and the jump rings call `forTranslucentWorld` (depth test on).

**Symptom:** the blur draws nothing on screen.
**Cause:** the last upsample left the image in `PONG`.
**Fix:** the final draw must target the framebuffer that was bound at the start of the pass.

**Symptom:** the blur composite is invisible against the world.
**Cause:** the quad's clip-space `z` is 0, so a depth test that is still on rejects it against the world depth buffer.
**Fix:** disable the depth test around the composite quads and restore it after.

**Symptom:** the blur reads are quoted with a microsecond cost, or the passes are called a PCIe transfer.
**Cause:** the four binding reads were never timed, and the passes run on the GPU.
**Fix:** do not publish a number that was not measured.

**Symptom:** the wrong framebuffer is restored after a pass.
**Cause:** the framebuffer cache was keyed by width and height. `Framebuffer` has no public GL id, and a different framebuffer can share the size.
**Fix:** restore the binding captured at the start of the pass, not a size-keyed entry.

**Symptom:** a new `MatrixStack` appears per frame, or the shared stack leaks a push.
**Cause:** the listener allocated its own stack, or pushed the shared one and returned.
**Fix:** use the shared stack from `WorldMatrices.identity()`. Pop before the listener returns.

**Symptom:** GL state does not match Minecraft's render-state cache after a pass.
**Cause:** `GL11` does not update that cache.
**Fix:** `RenderStateGuard` reads depth, cull, blend, and the depth mask and restores all four. A launched client has not yet confirmed the cache matches the driver after the guard returns.

## Scope checks

**Symptom:** a change documents another client's class list, file hashes, or a pathfinding bot.
**Fix:** this repository does not. A screen rectangle is not a block outline. An empty mixin package is not a port.

**Symptom:** the 1.21.11 hat is described as the local player only, while a launched picture would also sit on other players.
**Cause:** `ChinaHatModule` walks `ClientWorld.getPlayers()`. `26.x` `WorldGizmos.hat` uses `mc.player` only.
**Fix:** `1.21.x/PATTERNS.md` and `MODULES.md` say every client player. `26.x/CONTRACT.md` stays local-only. A launched game has not confirmed either picture («не проверено»).

**Symptom:** a verify script is treated as proof the mod runs.
**Cause:** the script only checks file names. A compile is not a launched game.
**Fix:** say what was actually observed. GitHub Actions on this repo has not been observed from this machine.
