package com.xyazh.kanake.util;

import java.util.Random;

public class PerlinNoise2d {
    private static final int PERMUTATION_SIZE = 256;
    private final int[] p;
    public double frequency;
    public double scale;
    public double magic = 2955481d/246971d;

    public PerlinNoise2d(long seed, double frequency,double scale) {
        this(new Random(seed), frequency, scale);
    }

    public PerlinNoise2d(Random random,double frequency,double scale) {
        this.frequency = frequency;
        this.scale = scale;
        int[] permutation = new int[PERMUTATION_SIZE];
        p = new int[PERMUTATION_SIZE * 2];
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            permutation[i] = i;
        }
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            int j = random.nextInt(PERMUTATION_SIZE);
            int tmp = permutation[i];
            permutation[i] = permutation[j];
            permutation[j] = tmp;
        }
        for (int i = 0; i < PERMUTATION_SIZE; i++) {
            p[PERMUTATION_SIZE + i] = p[i] = permutation[i];
        }
    }

    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = (h & 1) == 0 ? x : -x;
        double v = (h & 2) == 0 ? y : -y;
        return u + v;
    }

    public double noise(double x, double y) {
        x *= frequency/magic;
        y *= frequency/magic;

        int xi = (int) Math.floor(x) & 255;
        int yi = (int) Math.floor(y) & 255;
        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        double u = fade(xf);
        double v = fade(yf);

        int aa = p[p[xi] + yi];
        int ab = p[p[xi] + yi + 1];
        int ba = p[p[xi + 1] + yi];
        int bb = p[p[xi + 1] + yi + 1];

        double x1 = lerp(u, grad(aa, xf, yf), grad(ba, xf - 1, yf));
        double x2 = lerp(u, grad(ab, xf, yf - 1), grad(bb, xf - 1, yf - 1));
        return lerp(v, x1, x2) * scale;
    }
}
