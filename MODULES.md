<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: MODULES.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Every class in the two mods

What each file does, so an AI does not have to guess from a name.

## 1.21.x — team.vixo.mc121 (Yarn 1.21.11, Java 21)

### Entry and buses

| Class | Job |
| --- | --- |
| `VixoClient` | Client entrypoint. Registers the seven listeners. No GL objects here. |
| `WorldRenderBus` | A list of `(tickDelta, frameDt)` listeners, dispatched from the world mixin. |
| `HudRenderBus` | A list of `HudPass` listeners, dispatched from the HUD mixin with the `DrawContext`. |
| `HudPass` | Interface: `render(DrawContext, float frameDt)`. |
| `FrameClock` | Seconds since the previous world hook. `nanoTime`. Returns 0.016 when the gap is negative or above 0.1. |
| `VisualSettings` | One shared settings object. Colors, radii, the six toggles. Blur defaults off. Read during render, not copied at enable time. |

### Modules

| Class | Job |
| --- | --- |
| `EspTracersModule` | Up to 48 other players inside the view cone. Twelve-edge box plus one tracer per player, camera-relative, in a fixed float buffer. Drawn through `forOverlay` (depth test off, restored after). |
| `JumpCirclesModule` | 32 ring slots. A jump stores x, y + 0.015, z. Each ring is one XZ quad, radius grows and alpha fades over 0.9 s. Cleared when the world changes. Submitted with `debugQuads()`. |
| `ChinaHatModule` | 24-slice cone on every player from `ClientWorld.getPlayers()`, including the local player. Cone edge plus rim edge per slice, same line drawer. Depth test on. The 26.2 hat is the local player only. |
| `TrailModule` | 24 feet samples of the local player. A sample every 0.2 blocks of movement. Camera-relative lines. Clears on world change, death, disabled setting, or a 64-block gap. |
| `KawaseBlurModule` | Off by default. Down-up-down-up half-res ping-pong, final upsample into the framebuffer that was bound at pass start. Depth test off around the quads. Loads shaders lazily, drops them on stop. |
| `TargetHudModule` | Crosshair entity panel. Rounded SDF panel, player face from the player-list skin, eased health bar. Forgets the entity after 3 s off-crosshair; drops everything when the world or player is missing. |
| `ViewModelModule` | First-person hand pose: translate, yaw, uniform scale. Off-hand can be left unchanged. |

### Render helpers (team.vixo.mc121.render)

| Class | Job |
| --- | --- |
| `LineDrawer` | Builds `POSITION_COLOR_NORMAL_LINE_WIDTH` segments. The normal is the unit direction from `MathUtility.direction`; degenerate segments fall back to (0,1,0) and are skipped upstream. |
| `RenderStateGuard` | `capture()` reads depth, cull, blend, depth mask. `forOverlay` forces depth test off (tracers). `forTranslucentWorld` leaves it on (hat, trail, rings). `close()` writes the captured bits back. |
| `WorldMatrices` | One shared `MatrixStack`. No per-frame allocation. A listener that pushes must pop. |
| `ScreenQuad` | One clip-space quad on a persistent VAO. Position attribute 0, UV attribute 1. |
| `FrameBuffer` | Half-res target. `ensureSize` recreates on a size change; `bind` also sets the viewport. |
| `ShaderProgram` | Compile, link, cached uniform locations. Throws with the info log on failure. |
| `ShaderSources` | Reads the four shader resources from the shared jar. |

### Mixins

| Class | Hook |
| --- | --- |
| `GameRendererWorldRenderMixin` | `renderWorld` TAIL → world bus. |
| `InGameHudMixin` | `render` TAIL → HUD bus. |
| `HeldItemRendererMixin` | `renderFirstPersonItem` HEAD → hand pose. |
| `ClientPlayerJumpMixin` | `tickMovement` TAIL → jump detection (was on ground, now not, positive Y velocity). |

## 26.x — team.vixo.mc26 (Mojmap 26.2, Java 25)

| Class | Job |
| --- | --- |
| `VixoClient` | Binds the modules. |
| `WorldGizmos` | The world pass. Inside `collectPerFrameRenderThreadGizmos`: targeted-block cuboid, up to 48 player boxes plus tracers within 64 blocks, the 24-slice hat, the 24-sample trail (`Vec3` ring, one new instance per stored sample because the gizmo holds the reference). |
| `JumpMarks` | 32 jump rings, aged in game-time delta ticks, gone after 18, `Gizmos.circle` (20 segments). |
| `TargetPanel` | HUD bar plus a panel when the crosshair is a living entity. |
| `TrailSettings` | Shared toggles and colors for the 26.x trail. Default color `0xFF88CCFF`. |
| `LevelRendererGizmoMixin` | `LevelRenderer.render` HEAD → `WorldGizmos.emit`, before the gizmo drain. |
| `LivingEntityJumpMixin` | Jump detection for the rings. |
| `HudExtractMixin` | `Hud.extractRenderState` → the panel. |
| `ItemInHandRendererMixin` | `submitHandsWithItems` HEAD → one translation and scale on the shared pose. Both hands move together. |

## Shared (team.vixo.shared)

| Class | Job |
| --- | --- |
| `MathUtility` | `direction()` — unit vector of a segment into a reused float[3]. Degenerate segment writes (0,1,0) and returns false. |
| `ColorUtility` | ARGB channel extraction and packing. |
| `Interpolation` | Exponential easing: `current + (target - current) * (1 - exp(-rate * dt))`. |

No Minecraft imports in the shared package. That is what lets both version modules depend on it.
