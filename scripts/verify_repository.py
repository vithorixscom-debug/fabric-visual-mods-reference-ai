#!/usr/bin/env python3
"""Check required files, banned leftovers, and markdown links."""
from pathlib import Path
import re
import sys

ROOT = Path(__file__).resolve().parents[1]

REQUIRED = [
    "README.md",
    "COMPAT.md",
    "LICENSE",
    "THIRD_PARTY.md",
    "AI_REVIEW_GUIDE.md",
    "1.21.x/CONTRACT.md",
    "26.x/CONTRACT.md",
    "CRAFT.md",
    "CONTRIBUTING.md",
    "MODULES.md",
    "VISUALS.md",
    "OPTIMIZATION.md",
    "OPTIMIZATION_MODS.md",
    "MISTAKES.md",
    "1.21.x/PATTERNS.md",
    "shared_render/src/main/resources/assets/vixoteam/shaders/round_rect.fsh",
    "shared_render/src/main/resources/assets/vixoteam/shaders/screen_quad.vsh",
    "shared_render/src/main/resources/assets/vixoteam/shaders/kawase_down.fsh",
    "shared_render/src/main/resources/assets/vixoteam/shaders/kawase_up.fsh",
    "1.21.x/src/main/java/team/vixo/mc121/module/KawaseBlurModule.java",
    "1.21.x/src/main/java/team/vixo/mc121/module/EspTracersModule.java",
    "gradle/wrapper/gradle-wrapper.jar",
    "gradle/wrapper/gradle-wrapper.jar.sha256",
    "WINVISUAL.md",
    "RIGHTS.txt",
    "VisualPrimer/LICENSE",
    "VisualPrimer/RIGHTS.txt",
    "VisualPrimer/VISUALPRIMER.md",
    "VisualPrimer/WINVISUAL.md",
    "VisualPrimer/gradlew.bat",
    "VisualPrimer/settings.gradle",
    "VisualPrimer/1.21.x/src/main/java/visual/primer/mc121/PrimerClient.java",
    "VisualPrimer/26.x/src/main/java/visual/primer/mc26/PrimerClient.java",
    "VisualPrimer/shared_render/src/main/java/visual/primer/shared/MathUtility.java",
    "VisualPrimer/shared_render/src/main/resources/assets/visualprimer/shaders/round_rect.fsh",
    "VisualPrimer/shared_render/src/main/resources/assets/visualprimer/shaders/screen_quad.vsh",
    "VisualPrimer/shared_render/src/main/resources/assets/visualprimer/shaders/kawase_down.fsh",
    "VisualPrimer/shared_render/src/main/resources/assets/visualprimer/shaders/kawase_up.fsh",
]

ABSENT = [
    "PROGRAMS.md",
    "AI_LICENSE.md",
    "PROMPT.md",
    "KERNEL.md",
    "INPUT_CLIENT.md",
    "BREAKAGE.md",
    "1.21.x/Visual",
    "supplementary/donutsmp_mod_metadata",
    "supplementary/MOD_ECOSYSTEM_CATALOG.md",
    "supplementary/WORKSPACE_BUILT_JARS.md",
    "shared_render/glsl",
    "26.x/mixins",
]

LINK = re.compile(r"\[[^\]]+\]\(([^)]+)\)")


def markdown_links():
    broken = []
    for path in ROOT.rglob("*.md"):
        if "build" in path.parts or ".gradle" in path.parts:
            continue
        text = path.read_text(encoding="utf-8", errors="replace")
        for match in LINK.finditer(text):
            target = match.group(1).split("#", 1)[0].strip()
            if not target or target.startswith(("http://", "https://", "mailto:")):
                continue
            resolved = (path.parent / target).resolve()
            if not resolved.exists():
                broken.append(f"{path.relative_to(ROOT)} -> {target}")
    return broken


def main() -> int:
    missing = [rel for rel in REQUIRED if not (ROOT / rel).is_file()]
    present = [rel for rel in ABSENT if (ROOT / rel).exists()]
    broken = markdown_links()
    if missing or present or broken:
        for rel in missing:
            print("MISSING", rel)
        for rel in present:
            print("SHOULD BE ABSENT", rel)
        for item in broken:
            print("BROKEN LINK", item)
        return 1
    print("verify_repository: OK", len(REQUIRED), "files")
    return 0


if __name__ == "__main__":
    sys.exit(main())
