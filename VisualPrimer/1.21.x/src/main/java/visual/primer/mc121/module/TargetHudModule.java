/*
 * Fabric visual mods reference
 * Module: TargetHudModule.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import visual.primer.mc121.render.ScreenQuad;
import visual.primer.mc121.render.ShaderProgram;
import visual.primer.mc121.render.ShaderSources;
import visual.primer.shared.ColorUtility;
import visual.primer.shared.Interpolation;

public final class TargetHudModule implements HudPass {
    private static final TargetHudModule INSTANCE = new TargetHudModule();
    private static final long TIMEOUT_NS = 3_000_000_000L;
    private static ShaderProgram panel;
    private LivingEntity target;
    private Identifier skin;
    private int targetId = Integer.MIN_VALUE;
    private float shownHealth;
    private long lastSeenNs;

    private TargetHudModule() {
    }

    public static void init() {
        HudRenderBus.register(INSTANCE);
    }

    @Override
    public void render(DrawContext context, float frameDt) {
        if (!VisualSettings.SHARED.hudEnabled()) {
            return;
        }
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null || mc.player == null) {
            target = null;
            skin = null;
            targetId = Integer.MIN_VALUE;
            return;
        }
        long now = System.nanoTime();
        if (mc.targetedEntity instanceof LivingEntity living && living.isAlive()) {
            if (living.getId() != targetId) {
                skin = null;
                targetId = living.getId();
                shownHealth = living.getHealth();
            }
            target = living;
            lastSeenNs = now;
            if (living instanceof AbstractClientPlayerEntity player && mc.getNetworkHandler() != null) {
                var entry = mc.getNetworkHandler().getPlayerListEntry(player.getUuid());
                if (entry != null) {
                    skin = entry.getSkinTextures().body().texturePath();
                }
            }
        } else if (now - lastSeenNs > TIMEOUT_NS) {
            target = null;
            skin = null;
            targetId = Integer.MIN_VALUE;
        }
        if (target == null) {
            return;
        }
        shownHealth = Interpolation.exponential(shownHealth, target.getHealth(), 10.0f, frameDt);
        int x = 12;
        int y = 12;
        int w = 130;
        int h = 46;
        drawPanel(mc, x, y, w, h, 6.0f, ColorUtility.argb(230, 15, 15, 18));
        if (skin != null) {
            context.drawTexture(RenderPipelines.GUI_TEXTURED, skin, x + 5, y + 5, 8.0f, 8.0f, 28, 28, 8, 8, 64, 64);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, skin, x + 5, y + 5, 40.0f, 8.0f, 28, 28, 8, 8, 64, 64);
        }
        context.drawText(mc.textRenderer, target.getName(), x + 40, y + 8, 0xFFFFFFFF, false);
        int barW = w - 48;
        int filled = Math.round(barW * (shownHealth / Math.max(1.0f, target.getMaxHealth())));
        context.fill(x + 40, y + 32, x + 40 + barW, y + 36, ColorUtility.argb(220, 21, 18, 28));
        context.fill(x + 40, y + 32, x + 40 + filled, y + 36, ColorUtility.argb(255, 95, 65, 240));
    }

    private static void drawPanel(MinecraftClient mc, int x, int y, int w, int h, float radius, int argb) {
        if (panel == null) {
            try {
                String vert = ShaderSources.read(mc, "shaders/screen_quad.vsh");
                panel = new ShaderProgram(vert, ShaderSources.read(mc, "shaders/round_rect.fsh"));
            } catch (Exception ex) {
                return;
            }
        }
        float sw = mc.getWindow().getScaledWidth();
        float sh = mc.getWindow().getScaledHeight();
        float x0 = (x / sw) * 2.0f - 1.0f;
        float x1 = ((x + w) / sw) * 2.0f - 1.0f;
        float y0 = 1.0f - ((y + h) / sh) * 2.0f;
        float y1 = 1.0f - (y / sh) * 2.0f;
        panel.bind();
        panel.setVec4("uColor", ColorUtility.channel(argb, 16), ColorUtility.channel(argb, 8), ColorUtility.channel(argb, 0), ColorUtility.channel(argb, 24));
        panel.setVec2("uHalfSize", w * 0.5f, h * 0.5f);
        panel.setFloat("uRadius", radius);
        ScreenQuad.draw(x0, y0, x1, y1, -w * 0.5f, -h * 0.5f, w * 0.5f, h * 0.5f);
        panel.unbind();
    }
}


