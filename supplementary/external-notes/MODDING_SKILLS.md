<!--
Fabric visual mods reference
Module: MODDING_SKILLS.md
SPDX-License-Identifier: MIT
-->

# Notes taken from three skill packs

Local notes only. They do not replace `1.21.x/CONTRACT.md` or `26.x/CONTRACT.md`. If a name here disagrees with a contract, the contract is the one this tree compiled. Claims below that come from the packs were not re-checked with a new build in this change («не проверено»). Scripts, textures, and vendor files from the packs are not copied into this tree.

## Minecraft modding with kids

A Fabric teaching skill: JDK 21, a generated Fabric project, a parent-only prep pass, then a joint build pass. Voice playback is optional and is not part of this repository.

Its 1.21.11 class list is Mojmap, not the Yarn names in `1.21.x/`. Examples of names that must not be pasted into `team.vixo.mc121`: `Level` (this tree uses `ClientWorld` on Yarn), `Item.Properties`, and `Identifier` under `net.minecraft.resources`. On their Mojmap list, `SwordItem`, `TieredItem`, and `DiggerItem` are gone; a tool is an `Item` plus a property method. This repository does not register items or blocks. Those patterns stay out of the client visual modules.

## Four modding skills

| Skill | What it is for here |
| --- | --- |
| `update-deps` | Compare a mod dependency with Modrinth, CurseForge, and Maven for the current Minecraft version and loader. CurseForge needs an API key; without the key that source is skipped. The pins in the two contracts stay as written. |
| `mod-dependency` | Choose `implementation`, `compileOnly`, or `runtimeOnly`, and the repository URL. 26.2 in this tree already uses `implementation` for loader and Fabric API, as `26.x/CONTRACT.md` says. |
| `minecraft-pixel-texture` | Makes a 16, 32, or 64 pixel PNG from a text prompt. It needs an image API. The shaders in this tree are hand-written and do not use it. |
| `java-breakpoint-debug` | JDWP is loaded when the process starts (`--debug-jvm` on a Gradle run). It cannot be attached after launch. The debug helper wants JDK 21 and the modules `jdk.jdi` and `jdk.attach`. A launched game in this tree is still «не проверено». |

## Minecraft agent skills

That pack is MIT. This file is a summary, not a copy of the pack.

Thirteen skills: modding (Fabric, NeoForge, Forge 1.20.1), plugins, datapacks, commands, multiloader, testing, CI release, world generation, resource packs, image generation, server admin, WorldEdit, Essentials. Only the Fabric modding notes below touch this client. Datapacks, commands, WorldEdit, and Essentials are outside `CONTRIBUTING.md`.

Facts that match this tree:

- Fabric on 26.x uses official names and a Loom path that does not remap. Do not paste 1.21.11 Yarn names, the 1.21.11 Loom plugin line, or Java 21 into `26.x/`.
- Java 25 belongs to the 26.x line. Java 21 belongs to the 1.21.x line.
- Read the version from `gradle.properties` or `build.gradle` before writing a call.
- Their Fabric example allows loader `>=0.19.3` and Fabric API `>=0.141.4+1.21.11`. This tree pins loader `0.19.3` and Fabric API `0.141.6+1.21.11` on Yarn, and `0.154.0+26.2` on 26.2. Their sample `environment` is both sides. This tree is `"environment": "client"`.
- Their bundle text also mentions a 26.3 review. This tree stays on 26.2. Loom here has no official mappings for 26.3 (`26.x/CONTRACT.md`).

GameTests and a CI release to Modrinth or CurseForge are not set up in this repository. «не проверено».
