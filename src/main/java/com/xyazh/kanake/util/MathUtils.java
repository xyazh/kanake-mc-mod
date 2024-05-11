package com.xyazh.kanake.util;

public class MathUtils {
    public static int[] range(int min, int max){
        if(min>=max){
            return new int[]{};
        }
        int l = max-min;
        int[] r = new int[l];
        for(int i=0;i<l;i++){
            r[i] = min + i;
        }
        return r;
    }

    public static int[] range(int n){
        return range(0,n);
    }

    public static double bezier(double a0,double a1,double a2,double a3,double t){
        return a0*Math.pow(1-t,3) + 3*a1*t*Math.pow(1-t,2) + 3*a2*Math.pow(t,2)*(1-t) + a3*Math.pow(t,3);
    }

    public static double dBezier(double a,double b,double c,double d,double x){
        double x2 = Math.pow(x,2);
        double pox = 1-x;
        double pox2 = Math.pow(pox,2);
        return -3*a*pox2 + 3*b*(3*x2-4*x+1) + 3*c*(2*x-3*x2) + 3*d*x2;
    }

    public static double dBezierQuick(double a, double b, double c, double d, double x) {
        double x2 = x * x;
        double pox = 1 - x;
        double pox2 = pox * pox;
        double term1 = -3 * a * pox2;
        double term2 = 3 * b * (3 * x2 - 4 * x + 1);
        double term3 = 3 * c * (2 * x - 3 * x2);
        double term4 = 3 * d * x2;
        return term1 + term2 + term3 + term4;
    }

    public static double bezierQuick(double a0, double a1, double a2, double a3, double t) {
        double oneMinusT = 1 - t;
        double t2 = t * t;
        double oneMinusT2 = oneMinusT * oneMinusT;
        double t3 = t * t * t;
        double oneMinusT3 = oneMinusT * oneMinusT * oneMinusT;
        double term1 = a0 * oneMinusT3;
        double term2 = 3 * a1 * t * oneMinusT2;
        double term3 = 3 * a2 * t2 * oneMinusT;
        double term4 = a3 * t3;
        return term1 + term2 + term3 + term4;
    }
}
