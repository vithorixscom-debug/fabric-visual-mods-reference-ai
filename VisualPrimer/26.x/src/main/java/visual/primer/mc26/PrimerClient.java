/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: PrimerClient.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26;

import net.fabricmc.api.ClientModInitializer;
import visual.primer.mc26.module.JumpMarks;

public final class PrimerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        JumpMarks.bind();
    }
}


