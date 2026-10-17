<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: README.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Fabric client reference

Two Fabric client mods for visuals and ordinary client modules — overlays, HUD panels, first-person pose, settings, clocks, and session state — plus the notes for writing another module in the same shape. Input automation, packet construction, and pathfinding executors are out of scope (`CONTRIBUTING.md`). License: MIT (`LICENSE`).

| Path | Role |
| --- | --- |
| `CONTRIBUTING.md` | Editing rules and verification |
| `CRAFT.md` | Entrypoint, mixins, the two clocks, session state, settings, where a picture goes |
| `MODULES.md` | What every class in both mods does |
| `1.21.x/PATTERNS.md` | The pictures this mod submits |
| `VISUALS.md` | Generic reference of visual techniques in client mods |
| `OPTIMIZATION.md` | ImmediatelyFast upstream, repacks, ZakoOpt on Sodium |
| `OPTIMIZATION_MODS.md` | The public optimizer ecosystem, mod by mod |
| `MISTAKES.md` | Symptom, cause, and fix for the failures this tree hit |
| `COMPAT.md` | Sodium, entity models, culling, shader packs, and the blur reads |
| `shared_render/src/main/java/team/vixo/shared/` | Math with no Minecraft imports |
| `shared_render/src/main/resources/assets/vixoteam/shaders/` | `round_rect.fsh`, `screen_quad.vsh`, `kawase_down.fsh`, `kawase_up.fsh` |
| `shared_math/` | The same math in prose. `ballistics_and_raycasting.md` is trajectory lines and a voxel walk for drawing them, not an aim assistant. |
| `1.21.x/` | Yarn `1.21.11+build.6`, Java 21. Contract: `1.21.x/CONTRACT.md`. Patterns: `1.21.x/PATTERNS.md` |
| `26.x/` | Mojmap `26.2`, Java 25. Modules, mixins, and `26.x/CONTRACT.md` |
| `VisualPrimer/` | Second Gradle build. Mod id `visualprimer`, packages `visual.primer.*`, its own default palette. Same versions and the same depth-test rules. License: `VisualPrimer/LICENSE` (the same MIT text). |
| `WINVISUAL.md` | Prompt for another visual module: counts, clocks, HUD, settings, build commands |

## API names that compiled

| Concern | 1.21.11 Yarn | 26.2 Mojmap |
| --- | --- | --- |
| World hook | `GameRenderer.renderWorld` | `LevelRenderer.render` head, before the gizmo drain |
| Block outline | line buffer, 12 edges | `Gizmos.cuboid` inside `collectPerFrameRenderThreadGizmos` |
| Segment | `RenderLayers.LINES` | `Gizmos.line` |
| HUD | `InGameHud.render` | `Hud.extractRenderState` |
| First person | `HeldItemRenderer.renderFirstPersonItem` | `ItemInHandRenderer.submitHandsWithItems` |
| Camera | `Camera.getCameraPos` | `Camera.position()` |
| Partial tick | `RenderTickCounter.getTickProgress(true)` | `DeltaTracker.getGameTimeDeltaPartialTick` |
| Frame age | `FrameClock.seconds()` | `DeltaTracker.getGameTimeDeltaTicks()` |

1.21.11 uses loader `0.19.3`, Fabric API `0.141.6+1.21.11`, Loom `1.17.17`. 26.2 uses loader `0.19.3`, Fabric API `0.154.0+26.2`, Loom `1.17.21` (plugin id `net.fabricmc.fabric-loom`). 26.3 is not in this tree: that Loom release reports no official mappings.

`python scripts/verify_repository.py` checks that the linked files exist, including `VisualPrimer/`. `.github/workflows/verify.yml` runs that script, then `./gradlew build` at the repository root and a second `./gradlew build` inside `VisualPrimer/`. Root `./gradlew build` alone does not compile `VisualPrimer/`; that directory has its own wrapper.

`VisualPrimer/` is not a duplicate to delete. The draw behavior matches this root (same caps, same `forOverlay` / `forTranslucentWorld` split, same two clocks). The difference is the build identity: mod id `visualprimer`, packages `visual.primer.mc121`, `visual.primer.mc26`, and `visual.primer.shared`, entrypoint `PrimerClient`, and the default colors in its `VisualSettings` and its 26.2 trail color. The root mod id is `vixoteam`, packages `team.vixo.*`.

The names in the table are the ones a local `build` accepted. A launched game has not confirmed them («не проверено»). GitHub Actions has not been observed from this machine («не проверено»).
