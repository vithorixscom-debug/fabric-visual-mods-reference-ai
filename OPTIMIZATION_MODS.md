<!--
Fabric visual mods reference
Module: OPTIMIZATION_MODS.md
SPDX-License-Identifier: MIT
-->

# The optimization ecosystem, mod by mod

What each public optimization mod does to the pipeline this repository draws into. These are all open-source; names are safe. ZakoOpt and the repack problem are in `OPTIMIZATION.md`. None of these numbers were measured in this tree.

## Chunk and terrain

**Sodium** replaces chunk meshing and submission: terrain is baked into large batches with fewer driver state changes. Consequences for a line pass: the depth buffer still works the same, so `LEQUAL` lines behave; but a mixin on a vanilla chunk method is dead code under Sodium. The world hook this tree uses (`GameRenderer.renderWorld` tail / `LevelRenderer.render` head) is after the terrain, which is the correct side. If a Sodium update stops calling that method, the target has to be re-read from that build.

**Sodium Extra** adds toggles on top of Sodium (particles, fog, animations). It can hide things a visual pass assumes exist; it does not change the line API.

**Lithium** patches game logic (mob AI, block entities, hoppers). It does not touch rendering, but it changes tick timing, so anything aged in ticks (the 26.x jump marks) lives in a slightly different frame rhythm.

**FerriteCore** compresses blockstate palettes and deduplicates model data. Memory only. No render interaction.

## Immediate mode and HUD

**ImmediatelyFast** batches and caches immediate-mode rendering: text, HUD quads, map, sign text. A HUD pass that submits its own quads after `InGameHud.render` runs after ImmediatelyFast's batching of the vanilla HUD; both can coexist, but the custom pass should not assume the vanilla batch is still open.

**BadOptimizations** skips redundant client work: lightmap updates when nothing changed, sky rendering behind bedrock, debug renderers nobody looks at, F3-only computations. Low risk to visuals; it can skip the exact debug path an AI copies as an example.

**Dynamic FPS** lowers the frame cap when the window is unfocused. Frame time (`FrameClock.seconds()` equivalent) stretches; anything aged in frame time pauses visually, which is correct — anything aged in ticks does not.

## Culling

**EntityCulling** raycasts (async, on a grid) whether an entity or block entity is visible and skips the draw and part of the tick when it is not. A box pass that walks the player list itself still draws a box for a culled player — that is usually the intent for a box, but the AI should know the model under the box may be absent. On 26.2, a culling mod that removes a player from `entitiesForRendering()` also removes the box.

**MoreCulling** culls block-model faces, leaves, item frames, and entity geometry parts. Same interaction class as EntityCulling, on the block side.

## Entity models

**Entity Model Features (EMF)** swaps entity models for custom JSON-defined parts. **Entity Texture Features (ETF)** adds texture properties (emissive, random variants, player skin features). EMF depends on ETF. A hat or trail anchored to `getLerpedPos` does not follow a replaced bone; a box is the bounding box, not the model mesh. An ESP that assumes vanilla `ModelPart` bounds misplaces its lines under EMF.

## Shaders and render pipelines

**Iris** replaces the world pipeline with shader packs. The world framebuffer may not be the default framebuffer, and its color attachment may not be a plain `GlTexture`. The blur pass in this tree reads the attachment only after an `instanceof` check and reads the bound framebuffer fresh each pass — both are Iris-driven decisions, not caution.

**VulkanMod** replaces OpenGL with Vulkan. Every `GL11` call in this tree is invalid there. The gizmo path on 26.x goes through Minecraft's own abstraction and survives; the 1.21 GL path does not. That is a known boundary, not a bug to patch blindly.

## UI smoothness

**Smooth Scrolling** (`io.github.smajloslovakian.smoothscroll`, open source) animates the chat, the creative inventory picker, the hotbar selector, and the command-suggestion list instead of snapping them. Two details worth copying: the animation is driven by `mc.getDeltaTracker().getGameTimeDeltaTicks()` — the same frame-age clock the 26.x jump marks use — and the mod checks `FabricLoader.isModLoaded` for a known alternative before enabling, so two smooth-scroll mods do not fight. It builds on JDK 25 with Loom `1.15.5` against `26.3-snapshot-9` with no `mappings` line in the build script (official Mojmap is implicit). One bug in the source worth not copying: `System.getProperty("os.name") == "Windows"` compares strings by reference and is always false.

## Config and library mods

**Mod Menu** (mod list screen), **Cloth Config**, **YACL**, **owo-lib**, **malilib** (config/keybind frameworks), **fabric-api** (events, renderer API), **fabric-language-kotlin**. They do not draw. A module that registers a config screen should use the user's installed framework rather than bundling a second one.

## Overlay optimizers

**ZakoOpt** sits on Sodium and hooks entity submission, HUD timing, a few particles, and the buffer builder — see `OPTIMIZATION.md` for the mixin groups and the Sodium version floors. It expects ImmediatelyFast to be present. An AI adding an optimization module should hook a measurable bottleneck, not re-implement Sodium.

## The rule for this tree

Optimization is subtractive: fewer entities, fewer faces, fewer state changes, lower frame rate when nobody watches. A rendering reference is additive: it submits a few hundred vertices a frame. The two meet only at the state boundary — restore GL state, do not cache framebuffer identity, and do not assume a vanilla method still runs under a replacement engine.
