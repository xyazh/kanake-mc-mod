package com.xyazh.kanake.util;

/**
 * Maps a normalized 0..1 temperature to a black-body-like colour ramp.
 *
 * <p>The ramp deliberately starts at a dark, almost black red so the star is
 * visibly cold when it first forms. As the temperature rises the colour moves
 * through orange, yellow and white before ending in a blue-white core.</p>
 */
public final class TemperatureColor {
    private static final double[] STOPS = {
            0.00D, 0.12D, 0.28D, 0.45D, 0.62D, 0.78D, 0.90D, 1.00D
    };
    private static final float[][] COLORS = {
            {0.16F, 0.008F, 0.003F},
            {0.42F, 0.025F, 0.004F},
            {0.88F, 0.13F, 0.012F},
            {1.00F, 0.38F, 0.035F},
            {1.00F, 0.74F, 0.18F},
            {1.00F, 0.93F, 0.56F},
            {1.00F, 0.99F, 0.90F},
            {0.80F, 0.90F, 1.00F}
    };

    private TemperatureColor() {
    }

    public static void write(double normalized, float[] out) {
        if (out == null || out.length < 3) {
            return;
        }
        double t = Math.max(0.0D, Math.min(1.0D, normalized));
        int index = 0;
        while (index < STOPS.length - 2 && t > STOPS[index + 1]) {
            ++index;
        }
        double span = Math.max(0.00001D, STOPS[index + 1] - STOPS[index]);
        double local = Math.max(0.0D, Math.min(1.0D,
                (t - STOPS[index]) / span));
        float[] a = COLORS[index];
        float[] b = COLORS[index + 1];
        out[0] = a[0] + (b[0] - a[0]) * (float) local;
        out[1] = a[1] + (b[1] - a[1]) * (float) local;
        out[2] = a[2] + (b[2] - a[2]) * (float) local;
    }

    public static float[] spectrum(double normalized) {
        float[] result = new float[3];
        write(normalized, result);
        return result;
    }
}
