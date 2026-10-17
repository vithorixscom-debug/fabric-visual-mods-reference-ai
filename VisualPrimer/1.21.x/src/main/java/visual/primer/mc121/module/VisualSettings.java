/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: VisualSettings.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc121.module;

public final class VisualSettings {
    public static final VisualSettings SHARED = new VisualSettings();

    private boolean tracersEnabled = true;
    private boolean hatEnabled = true;
    private boolean circlesEnabled = true;
    private boolean trailsEnabled = true;
    private boolean blurEnabled = false;
    private boolean hudEnabled = true;
    private int tracerColor = 0xE8E6E0D4;
    private int boxColor = 0xC08C5A42;
    private int hatColor = 0xB0455C48;
    private int circleColor = 0xFFC4A574;
    private float hatRadius = 0.58f;
    private float hatHeight = 0.34f;
    private float circleRadius = 1.0f;
    private float viewX;
    private float viewY;
    private float viewZ;
    private float viewRotX;
    private float viewRotY;
    private float viewScale = 1.0f;
    private boolean viewOffhand = true;

    public boolean tracersEnabled() { return tracersEnabled; }
    public void setTracersEnabled(boolean v) { tracersEnabled = v; }
    public boolean hatEnabled() { return hatEnabled; }
    public void setHatEnabled(boolean v) { hatEnabled = v; }
    public boolean circlesEnabled() { return circlesEnabled; }
    public void setCirclesEnabled(boolean v) { circlesEnabled = v; }
    public boolean trailsEnabled() { return trailsEnabled; }
    public void setTrailsEnabled(boolean v) { trailsEnabled = v; }
    public boolean blurEnabled() { return blurEnabled; }
    public void setBlurEnabled(boolean v) { blurEnabled = v; }
    public boolean hudEnabled() { return hudEnabled; }
    public void setHudEnabled(boolean v) { hudEnabled = v; }
    public int tracerColor() { return tracerColor; }
    public void setTracerColor(int v) { tracerColor = v; }
    public int boxColor() { return boxColor; }
    public void setBoxColor(int v) { boxColor = v; }
    public int hatColor() { return hatColor; }
    public void setHatColor(int v) { hatColor = v; }
    public int circleColor() { return circleColor; }
    public void setCircleColor(int v) { circleColor = v; }
    public float hatRadius() { return hatRadius; }
    public void setHatRadius(float v) { hatRadius = v; }
    public float hatHeight() { return hatHeight; }
    public void setHatHeight(float v) { hatHeight = v; }
    public float circleRadius() { return circleRadius; }
    public void setCircleRadius(float v) { circleRadius = v; }
    public float viewX() { return viewX; }
    public void setViewX(float v) { viewX = v; }
    public float viewY() { return viewY; }
    public void setViewY(float v) { viewY = v; }
    public float viewZ() { return viewZ; }
    public void setViewZ(float v) { viewZ = v; }
    public float viewRotX() { return viewRotX; }
    public void setViewRotX(float v) { viewRotX = v; }
    public float viewRotY() { return viewRotY; }
    public void setViewRotY(float v) { viewRotY = v; }
    public float viewScale() { return viewScale; }
    public void setViewScale(float v) { viewScale = v; }
    public boolean viewOffhand() { return viewOffhand; }
    public void setViewOffhand(boolean v) { viewOffhand = v; }
}


