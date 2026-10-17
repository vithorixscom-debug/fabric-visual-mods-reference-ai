<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: VISUALS.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Visual techniques reference

Common techniques in client-side visual mods, described generically. This is background knowledge for evaluating or writing a draw pass — the same shapes appear in many open-source clients. The concrete implementation in this repository is `1.21.x/PATTERNS.md` and `MODULES.md`.

## Boxes and tracers

**Twelve-edge box.** The entity bounding box, moved by `lerped position - current position`, drawn as 12 line segments in camera space. Common variants: full outline, corners only (a fraction of each edge), dashed edges, and a translucent fill under a no-depth-test pipeline. Through-walls variants disable the depth test and depth mask around the draw and restore both afterwards.

**Tracer.** One segment from the camera origin to the body center, in camera space. Usually capped (tens of players) so the buffer stays bounded.

**View-cone culling.** Skipping off-screen entities before building vertices. The weak version is a forward-dot test; the correct version uses the options FOV times the player's public FOV multiplier (sprint and zoom live there), the water/lava scale, and a body-radius margin.

## Hats and rings

**China hat.** A cone over the head, line edges or a triangle mesh (fan disc, strip to the apex, strip around the rim), yaw-rotated to follow the head. Sneak usually lowers it.

**Flat ring.** A band around the body at eye height — fill between two radii or a line loop. Watch for ring classes whose handler is empty while a different module draws the actual mesh; a stored radius slider alone is not a render.

**Jump ring.** A quad or circle at the jump point that grows and fades over a fixed lifetime. Ground rings stay XZ; a 90-degree rotation makes a billboard, which is a different effect. Ring slots should clear on world change, or rings from the previous world linger.

## Trails

**Ribbon trail.** A vertical quad strip between consecutive feet samples, often written twice so both sides show, with modes like solid, fade, and dotted. Trails need a cap and a teleport break; without a break, a teleport draws one long line across the map.

**Footsteps.** A ground quad every fixed distance with a short life. A marker, not a trail.

## Model effects

**Chams.** Recolor the entity model, either by multiplying the shader color inside a begin/end bracket (with depth test and mask toggled for a through-walls variant) or by replacing the model with a shader silhouette. The bracket must restore everything it touched and be a no-op if the begin never ran.

**Target marker.** Highlights the current target: a ring, orbiting shapes, or a shader outline. Orbit effects use camera-facing quads around the body.

**Hit effects.** Particles, bubbles, or ground circles fired on a hit, a death, or a totem pop. A reach circle marks attack range; it does not change the range.

## HUD

**Target panel.** A card for the crosshair entity: face texture, name, eased health bar. Ease with frame time, and forget the entity after a short timeout off-crosshair.

**Screen effects.** Damage vignette, full bright, aspect-ratio override, zoom (save FOV and sensitivity, narrow, restore on release). A real motion blur keeps the previous frame's view and projection; the camera position is reduced modulo a large constant before the float write so the shift vector keeps mantissa bits.

## Camera

**Free-look.** Store the perspective, hold camera yaw and pitch in separate fields, switch to third person. The body yaw and the camera yaw are different values while it runs.

**Freecam.** A detached camera with its own position and rotation; the player input is replaced by a frozen input while it is active, and restored on disable.

## What correct implementations agree on

- Camera-relative coordinates, or the mesh swims when the camera moves.
- Blend `SRC_ALPHA / ONE_MINUS_SRC_ALPHA` for translucent world meshes.
- Restore what you changed: depth test, depth mask, cull, blend, shader color.
- A fixed ring buffer for samples; an uncapped list grows until the GC notices.
- A slider that is never written into a vertex or a uniform is not a feature.
