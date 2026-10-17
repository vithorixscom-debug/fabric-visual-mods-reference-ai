<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: CONTRIBUTING.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# Contributing

## Where to start

Read `README.md`, then the contract for the version you touch (`1.21.x/CONTRACT.md` or `26.x/CONTRACT.md`). If another document disagrees with a contract, the contract is current and the other file is stale — please fix it in the same change.

## Editing rules

- Every Minecraft method name must exist in the mapped jar of the version it compiles against. A name that was right on another version is not evidence.
- `./gradlew build` must pass after the change. A compile is not a launched game; do not claim runtime behavior in comments or docs.
- No per-frame allocations in render paths. Reuse the static buffers.
- GL state changed inside a pass is restored before the pass returns. Use `RenderStateGuard`.
- `team.vixo.shared` and `visual.primer.shared` must not import Minecraft.
- Visual modules and ordinary client mods: overlays, HUD panels, first-person pose, settings, clocks, and session state. No input automation, no packet construction, no pathfinding executors.
- Third-party clients are not named, quoted, or redistributed here. See `THIRD_PARTY.md`.

## Verification

`python scripts/verify_repository.py` checks required files, including `VisualPrimer/`, and markdown links. `.github/workflows/verify.yml` runs that script, then `./gradlew build` on JDK 21 and 25 at the root and again inside `VisualPrimer/`. A compile is not a launched game.
