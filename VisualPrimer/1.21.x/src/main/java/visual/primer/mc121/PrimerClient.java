/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: PrimerClient.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121;

import net.fabricmc.api.ClientModInitializer;
import visual.primer.mc121.module.ChinaHatModule;
import visual.primer.mc121.module.EspTracersModule;
import visual.primer.mc121.module.JumpCirclesModule;
import visual.primer.mc121.module.KawaseBlurModule;
import visual.primer.mc121.module.TargetHudModule;
import visual.primer.mc121.module.TrailModule;
import visual.primer.mc121.module.ViewModelModule;

public final class PrimerClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EspTracersModule.init();
        JumpCirclesModule.init();
        ChinaHatModule.init();
        TrailModule.init();
        KawaseBlurModule.init();
        TargetHudModule.init();
        ViewModelModule.init();
    }
}


