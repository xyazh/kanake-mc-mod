package com.xyazh.kanake.util;

import java.util.Random;

public class NoisePerlin {
    protected final Random rand;
    protected final int[] permutation;
    protected final double frequency;
    protected final double amplitude;
    protected final Vec3d[] vec3ds;
    protected int repeat;

    public NoisePerlin(long seed, double frequency, double amplitude){
        this.rand = new Random(seed);
        this.repeat = 32768;
        int repeat2X = this.repeat*2;
        this.permutation = new int[repeat2X];
        for(int i = 0;i < repeat2X;i++){
            int j = this.rand.nextInt(this.repeat);
            this.permutation[i] = j;
        }
        this.frequency = frequency;
        this.amplitude = amplitude;
        this.vec3ds = new Vec3d[]{
                new Vec3d(0,1,0),
                new Vec3d(1,0,0),
                new Vec3d(1,1,0),
                new Vec3d(0,-1,0),
                new Vec3d(-1,0,0),
                new Vec3d(-1,-1,0),
                new Vec3d(1,-1,0),
                new Vec3d(-1,1,0)
        };
        for (Vec3d vec3d : this.vec3ds) {
            vec3d.normalize();
        }
    }

    public double fade(double t){
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    public int inc(int x){
        x += 1;
        if(this.repeat > 0){
            x %= this.repeat;
        }
        return x;
    }

    public double grad(int hash, double x, double y){
        Vec3d v1 = this.vec3ds[hash % 8];
        return v1.x * x + v1.y * y;
    }

    public double lerp(double a,double b,double x){
        return a + x * (b - a);
    }

    public double getValue(double x,double y){
        x = x/1000;
        y = y/1000;
        x *= this.frequency;
        y *= this.frequency;
        int xi = (int)x % this.repeat;
        int yi = (int)y % this.repeat;
        double xf = x - xi;
        double yf = y - yi;
        if(xi<0){
            xi += this.repeat;
        }
        if(yi<0){
            yi += this.repeat;
        }
        if(xf<0){
            xf += 1;
        }
        if(yf<0){
            yf += 1;
        }
        double u = this.fade(xf);
        double v = this.fade(yf);
        int[] permutation = this.permutation;
        int a = permutation[permutation[xi] + yi];
        int b = permutation[permutation[xi]+this.inc(yi)];
        int c = permutation[permutation[this.inc(xi)] + this.inc(yi)];
        int d = permutation[permutation[this.inc(xi)] + yi];
        double g1 = this.grad(a, xf, yf);
        double g2 = this.grad(d, xf - 1, yf);
        double g3 = this.grad(b, xf, yf - 1);
        double g4 = this.grad(c, xf - 1, yf - 1);
        double x1 = this.lerp(g1,g2, u);
        double x2 = this.lerp(g3,g4, u);
        double z = this.lerp(x1, x2, v);
        return z * this.amplitude;
    }
}