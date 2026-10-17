/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: ShaderSources.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121.render;

import java.nio.charset.StandardCharsets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public final class ShaderSources {
    private ShaderSources() {
    }

    public static String read(MinecraftClient mc, String path) throws Exception {
        Identifier id = Identifier.of("vixoteam", path);
        try (var in = mc.getResourceManager().getResourceOrThrow(id).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
