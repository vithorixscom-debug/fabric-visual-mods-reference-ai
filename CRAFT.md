<!--
Fabric visual mods reference
Module: CRAFT.md
SPDX-License-Identifier: MIT
-->

# Writing a client module in this tree

Two Fabric client mods: Yarn 1.21.11 and Mojmap 26.2. Use the contract for the version you compile. A name that is right on one version is often absent on the other. Shared math lives in `team.vixo.shared` and must not import Minecraft.

## Entrypoint

`fabric.mod.json` sets `"environment": "client"` and a `client` entrypoint. `VixoClient.onInitializeClient` only registers listeners. Do not create a GL texture, a shader, or a framebuffer there. The GL context is not ready, and a resource reload would leak the object. `KawaseBlurModule` loads shaders on the first enabled world pass and drops them from the HUD bus when the client stops.

## Mixins

Every hook in this tree is `@Inject`. There is no `@Overwrite`. The method string is the mapped name for that Minecraft version. Loom remaps it. Do not write an intermediary `method_` string.

| Job | 1.21.11 Yarn | 26.2 Mojmap |
| --- | --- | --- |
| After the world | `GameRenderer.renderWorld` `TAIL` | `LevelRenderer.render` `HEAD`, then `collectPerFrameRenderThreadGizmos` |
| HUD | `InGameHud.render` `TAIL` | `Hud.extractRenderState` |
| Hands | `HeldItemRenderer.renderFirstPersonItem` `HEAD` | `ItemInHandRenderer.submitHandsWithItems` `HEAD` |
| Jump | `ClientPlayerEntity.tickMovement` `TAIL` | `LivingEntity.jumpFromGround` |

On 26.2 both hands share one `PoseStack` in `submitHandsWithItems`. A translation applied once moves both hands. On 1.21.11 the off-hand can be skipped.

Copy the parameter list from the mixin that already compiles. Adding a parameter from memory is how those hooks break.

## Two clocks

`tickDelta` / `getGameTimeDeltaPartialTick` places a body between ticks. `FrameClock.seconds()` on 1.21.11, and `DeltaTracker.getGameTimeDeltaTicks()` on 26.2, age a ring or ease a health bar. A trail point taken once per tick looks stepped. A ring aged with `tickDelta` freezes whenever the partial tick sits still.

## Session state

Anything stored from the world must notice that the world changed.

- The trail stores `ClientWorld` / `ClientLevel`. A different world, a dead player, a disabled setting, or a gap over 64 blocks sets the count back to 0.
- Jump rings clear when `ClientWorld` is null or is a different instance. Otherwise a ring from the previous world draws for the rest of its 0.9 seconds.
- The target panel drops its entity, skin, and id when the world or the local player is missing. While playing, it forgets the entity 3 seconds after the crosshair leaves it, and it clears the skin when the entity id changes.

A static list that survives `onInitializeClient` is fine. A static list that survives a server change is a bug.

## Settings

`VisualSettings.SHARED` is one object, read during render. Blur defaults off. Changing a color or a radius affects the next frame, because the vertex reads the field while it is built. Do not copy the color into a vertex at enable time and leave it there.

## Where a picture goes

World geometry uses the world bus and camera-relative coordinates. HUD text and the face texture use the HUD bus and `DrawContext`. A world line submitted from `InGameHud.render` has the GUI matrix, not the camera. A `DrawContext.fill` submitted from `renderWorld` is not a block outline.

`PATTERNS.md` is the shape of each picture in this repository. It does not describe anyone else's mod.

## GL and allocations

Restore depth, blend, cull, the framebuffer, the viewport, the texture, and the program before the listener returns. `GL11` does not update Minecraft's render-state cache. A launched client has not checked that the cache matches the driver afterwards.

Do not allocate a `MatrixStack` per frame. Reuse `WorldMatrices.identity()`. Do not allocate a `Vec3` per trail segment per frame on 26.2: `Gizmos.line` keeps the `Vec3` until the gizmo is emitted, and `Vec3`'s fields are final, so the ring stores the instance created with the sample.

The blur's four binding reads exist only while blur is on. Their duration was not measured.

## A new listener

Register it from `VixoClient`. Give it a setting, a fixed buffer, and a world-change clear if it keeps samples. Hook the bus that matches the picture. Check the new Minecraft call against the mapped jar for that version, then `./gradlew build`. A green compile is not a launched game.
