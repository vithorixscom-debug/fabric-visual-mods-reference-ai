/*
 * Fabric visual mods reference
 * Module: TrailSettings.java
 * SPDX-License-Identifier: MIT
 */
package visual.primer.mc26.module;

public final class TrailSettings {
    public static final TrailSettings SHARED = new TrailSettings();

    private boolean trailsEnabled = true;
    private int trailColor = 0xFFD8C7A8;

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


