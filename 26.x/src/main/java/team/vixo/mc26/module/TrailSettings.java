/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: TrailSettings.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package team.vixo.mc26.module;

public final class TrailSettings {
    public static final TrailSettings SHARED = new TrailSettings();

    private boolean trailsEnabled = true;
    private int trailColor = 0xFF88CCFF;

    public boolean trailsEnabled() {
        return trailsEnabled;
    }

    public void setTrailsEnabled(boolean value) {
        trailsEnabled = value;
    }

    public int trailColor() {
        return trailColor;
    }

    public void setTrailColor(int value) {
        trailColor = value;
    }
}
