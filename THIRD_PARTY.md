<!--
Fabric visual mods reference
Module: THIRD_PARTY.md
SPDX-License-Identifier: MIT
-->

# Third-party software

This tree does not vendor other mods' source or jars.

`VisualPrimer/` is part of this repository, not a third-party mod. Its license file `VisualPrimer/LICENSE` repeats `LICENSE`: MIT, Fabric visual mods reference. The copyright notice and the permission notice stay in copies. `RIGHTS.txt` points at those files and does not replace them.

The shaders under `shared_render/src/main/resources/assets/vixoteam/shaders/` are written for this tree. `round_rect.fsh` uses the public rounded-box distance (`length(max(abs(p) - half, 0)) - radius`) and `fwidth`. It is not a copy of another mod's shader file.

Optimization mods that a client might also load (Sodium, Lithium, Entity Model Features, Entity Texture Features) keep their own licenses on their own repositories. Nothing from those repositories is copied here.

`supplementary/external-notes/MODDING_SKILLS.md` summarizes three external skill packs. Their scripts, textures, and vendor files are not in this tree. The summary does not copy those packs.
