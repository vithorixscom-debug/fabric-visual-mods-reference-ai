/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: VixoClient.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc26;

import net.fabricmc.api.ClientModInitializer;
import team.vixo.mc26.module.JumpMarks;

public final class VixoClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JumpMarks.bind();
    }
}
