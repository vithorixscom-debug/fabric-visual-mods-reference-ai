/*
 * Fabric visual mods reference
 * Module: VixoClient.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc121;

import net.fabricmc.api.ClientModInitializer;
import team.vixo.mc121.module.ChinaHatModule;
import team.vixo.mc121.module.EspTracersModule;
import team.vixo.mc121.module.JumpCirclesModule;
import team.vixo.mc121.module.KawaseBlurModule;
import team.vixo.mc121.module.TargetHudModule;
import team.vixo.mc121.module.TrailModule;
import team.vixo.mc121.module.ViewModelModule;

public final class VixoClient implements ClientModInitializer {
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
