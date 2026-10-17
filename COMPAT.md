<!--
Fabric visual mods reference
Module: COMPAT.md
SPDX-License-Identifier: MIT
-->

# Other renderers in the same client

This page is the interaction, not a copy of those mods.

Sodium replaces the chunk mesh. The lines in this tree are a buffer submitted at the tail of `GameRenderer.renderWorld`, after that mesh. They are not quads inside a Sodium section. If a later Sodium build stops calling `renderWorld`, the mixin target has to be read again from that build. Do not guess the new name.

Entity Model Features and Entity Texture Features replace the entity model and its texture. The hat and the trail here are line segments around `getLerpedPos`. They do not follow a replaced bone. A box is the moved bounding box, twelve edges, not the model mesh.

Entity culling skips entity draws that fail its test. On 1.21.11 the box pass walks `ClientWorld.getPlayers()`, then the view cone and the cap of 48. It does not ask the culling mod, so a player can still get a box after the model was skipped. That pass is `RenderStateGuard.forOverlay`: the depth test is off and the depth mask is off, then `capture()` writes the previous bits back. A line is not hidden by terrain. The hat, the trail, and the jump rings use `forTranslucentWorld`, which leaves the depth test on. On 26.2 the same pictures are `Gizmos` calls inside `collectPerFrameRenderThreadGizmos`. That file does not call `glDisable` for the depth test. Whether a gizmo is hidden by terrain was not checked in a launched game. A culling mod that removes a player from `Level.entitiesForRendering()` also removes the box.

Iris and similar shader packs replace the world framebuffer. The blur pass reads the color attachment only when it is a `GlTexture`, and it reads the bound framebuffer, viewport, texture, and program at the start of that pass. A size cache is not used: the same dimensions can be a different binding.

## Driver reads

`forTranslucentWorld` and `forOverlay` each read three `glIsEnabled` values and one `glGetBoolean` (depth mask), then write a known depth bit. The trail, the hat, and the jump rings take the first. Tracers take the second, which forces the depth test off.

The blur pass, only while the setting is on, also reads the bound framebuffer, the viewport, the bound texture, and the current program. Those four are context state. This tree did not time them. Do not publish a microsecond figure, and do not describe them as a measured pipeline flush. They stay because `Framebuffer` has no public GL id: a cache keyed by size restored the wrong target when the size did not change. The depth test is also read and forced off around the four quads, because their clip-space `z` is 0, then that bit is restored.
