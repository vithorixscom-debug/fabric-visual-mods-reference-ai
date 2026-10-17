<!--
Fabric visual mods reference
Module: OPTIMIZATION.md
SPDX-License-Identifier: MIT
-->

# Optimization next to this client

From public `fabric.mod.json` metadata and mixin-config names. No method bodies, nothing timed in a launched game.

## ImmediatelyFast and repacks

ImmediatelyFast speeds immediate-mode rendering. Upstream is https://github.com/RaphiMC/ImmediatelyFast . Only trust builds from upstream. Repacked jars exist that keep the ImmediatelyFast manifest text while shipping a different class tree and nested jars; a `custom` block in `fabric.mod.json` naming another client is the tell. ZakoOpt declares a hard dependency on mod id `immediatelyfast` — that means the upstream optimizer, not a repack.

## ZakoOpt 1.1.6

Public page: https://github.com/3ako/ZakoOpt . License `LGPL-3.0-only`. Client optimization on top of Sodium; it does not replace Sodium. Sodium below the declared break version is refused.

| Jar | Minecraft | Sodium |
| --- | --- | --- |
| `zakoopt-1.1.6+1.21.11` | `~1.21.11` | breaks `<0.8.0`, recommends `>=0.8.14`, also depends on `fabric-renderer-api-v1` |
| `zakoopt-1.1.6+26.2` | `~26.2` | breaks `<0.9.0`, recommends `>=0.9.2` |

Mixin-config groups (names only): options key, title screen, frame timing, spawner, block entity, display-entity cull, entity animation holder, entity name, entity render state, item cluster, resting item entity, buffer builder, HUD delta tracker, bubble-column particle, client language. The 26.2 config adds item feature renderer, level outline, living-entity skin, game-renderer scale, command-encoder texture buffer, and the HUD path on `GameRenderer`.

The shape of it: hook entity submission, a few particles, HUD timing, and the buffer builder — and let Sodium and ImmediatelyFast own chunk meshing and immediate mode.

## What this repository already avoids

`CRAFT.md` is the allocation and GL-restore list. The blur reads four context bindings only while blur is on. Their duration was not measured. A framebuffer cache keyed by size restored the wrong target. `RenderStateGuard` reads depth, cull, blend, and the depth mask because `GL11` does not update Minecraft's render-state cache.

Sodium, entity-model mods, and entity culling interact with the line pass as written in `COMPAT.md`.
