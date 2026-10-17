<!--
VixoTeam Graphics Architecture & Rendering Pipeline
Module: errors_and_crashes.md
Author / Maintainer: VixoTeam Systems
SPDX-License-Identifier: MIT
-->

# 26.2 failures that actually happened

| Symptom | What was wrong |
| --- | --- |
| `Failed to find official mojang mappings for 26.3` | The 26.3 version JSON has `client` and `server` only. Yarn meta for 26.2 and 26.3 is `[]`. This tree stays on 26.2. |
| `Configuration 'mappings' has no dependencies` | The merged 26.2 jar is already Mojmap. Do not add an empty mappings line. |
| `Could not find method modImplementation()` | On Loom `1.17.21` this project uses `implementation` for loader and Fabric API. |
| `syncTask has no value` on `genSourcesWithVineflower` | Sources were not generated. Compile against the merged jar. |
| `release version 25 not supported` | The compile JDK was older than 25. JDK 25.0.4 compiles this tree. |
| No `remapJar` task | The jar in `26.x/build/libs` is the compile output. The game was not launched. |

Do not replace `Gizmos` with a 1.21 `RenderSystem` blur path. This tree has no blur pass.
