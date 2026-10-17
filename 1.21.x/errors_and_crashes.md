<!--
Fabric visual mods reference
Module: errors_and_crashes.md
SPDX-License-Identifier: MIT
-->

# 1.21.11 failures that actually happened

| Symptom | What was wrong |
| --- | --- |
| `class, interface, enum, or record expected` on line 1 | The file started with `*` because `/*` was missing. |
| `cannot find symbol: BufferRenderer`, `ShaderProgramKeys`, `RenderSystem.enableBlend` | Those names are not in 1.21.11 Yarn `1.21.11+build.6`. |
| `RenderLayer.LINES` / `DEBUG_QUADS` missing | The public line layer is `RenderLayers.LINES`. `DEBUG_QUADS` is private. Quads go through `RenderLayers.debugQuads()`. |
| `VertexFormat` not in `client.render` | It is `com.mojang.blaze3d.vertex.VertexFormat`. |
| `getColorAttachment()` is not an `int` | It returns `GpuTexture`. Bind `GlTexture.getGlId()` only after an `instanceof` check. |
| `getPlayerListEntry()` inaccessible | It is protected on the player entity. Use the network handler. |
| `getSkinTextures()` missing on the player | The skin id is `getSkinTextures().body().texturePath()` on the player-list entry. |

`RenderLayer.getLines()` and `BufferRenderer.drawWithGlobalProgram` are not the fix. They are the names that failed.
