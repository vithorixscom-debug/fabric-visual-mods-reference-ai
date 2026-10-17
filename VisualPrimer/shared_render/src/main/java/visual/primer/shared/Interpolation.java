/*
 * VixoTeam Graphics Architecture & Rendering Pipeline
 * Module: Interpolation.java
 * Author / Maintainer: VixoTeam Systems
 * SPDX-License-Identifier: MIT
 */
package visual.primer.shared;

public final class Interpolation {
    private Interpolation() {
    }

    public static float lerp(float from, float to, float t) {
        return from + (to - from) * t;
    }

    public static double lerp(double from, double to, double t) {
        return from + (to - from) * t;
    }

    /** alpha = 1 - exp(-lambda * dtSeconds). lambda is per second, dt is frame duration. */
    public static float exponential(float current, float target, float lambda, float dtSeconds) {
        float alpha = 1.0f - (float) Math.exp(-lambda * dtSeconds);
        return lerp(current, target, alpha);
    }

    /**
     * Semi-implicit spring. Critically damped only when damping = 2 * sqrt(stiffness).
     * outVel[0] receives the updated velocity.
     */
    public static float spring(float current, float target, float velocity, float stiffness, float damping, float dtSeconds, float[] outVel) {
        float accel = -stiffness * (current - target) - damping * velocity;
        float newVel = velocity + accel * dtSeconds;
        outVel[0] = newVel;
        return current + newVel * dtSeconds;
    }

    /** Polynomial Bezier through four values. This is not CSS cubic-bezier timing. */
    public static float cubicBezier1D(float t, float p0, float p1, float p2, float p3) {
        float u = 1.0f - t;
        return u * u * u * p0 + 3.0f * u * u * t * p1 + 3.0f * u * t * t * p2 + t * t * t * p3;
    }
}


