/*
 * Fabric visual mods reference
 * Module: ItemInHandRendererMixin.java
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc26.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.2 submits both hands in one call. The pose shift applies to that shared stack.
 * There is no public per-hand method on ItemInHandRenderer.
 */
@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(method = "submitHandsWithItems", at = @At("HEAD"))
    private void vixo$hands(float partial, PoseStack pose, SubmitNodeCollector collector, LocalPlayer player, int light, CallbackInfo ci) {
        pose.translate(0.10f, -0.04f, -0.12f);
        pose.scale(0.92f, 0.92f, 0.92f);
    }
}
