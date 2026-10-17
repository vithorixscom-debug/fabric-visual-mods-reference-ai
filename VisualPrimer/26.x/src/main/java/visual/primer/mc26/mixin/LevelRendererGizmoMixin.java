/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: LevelRendererGizmoMixin.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26.mixin;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.joml.Matrix4fc;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import visual.primer.mc26.module.WorldGizmos;

@Mixin(LevelRenderer.class)
public class LevelRendererGizmoMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void primer$gizmos(
            GraphicsResourceAllocator allocator,
            DeltaTracker delta,
            boolean renderBlockOutline,
            CameraRenderState cameraState,
            Matrix4fc projection,
            GpuBufferSlice fog,
            Vector4f fogColor,
            boolean renderSky,
            CallbackInfo ci) {
        WorldGizmos.emit((LevelRenderer) (Object) this, delta);
    }
}


