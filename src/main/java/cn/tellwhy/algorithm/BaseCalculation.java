package cn.tellwhy.algorithm;

public class BaseCalculation {
    public static double log(final double value, final double base) {
        return Math.log(value) / Math.log(base);
    }


}

/*
* https://www.cnblogs.com/skyivben/archive/2013/02/20/2918900.html
*/
class DecimalExtensions {
    static final double ln10 = 2.3025850929940456840179914547;
    static final double lnr = 0.2002433314278771112016301167;

    public static double Log10(final double x){
        return Log(x) / ln10;
    }

    public static double Log(final double value){
        double x = value;
        if (x <= 0) {
            throw new IllegalArgumentException("Must be positive");
        }

        int k = 0, l = 0;
        while (x <= 1){
            x /= 10;
            k++;
        }
        for (; x > 1; k++) {
        }
        for (; x <= 0.1; k--) x *= 10;        // ( 0.1, 1 ]
        for (; x < 0.9047; l--) x *= 1.2217; // [ 0.9047, 1.10527199 )
        return k * ln10 + l * lnr + Logarithm((x - 1) / (x + 1));
    }

    static double Logarithm(double y) {
        // y in ( -0.05-, 0.05+ ), return ln((1+y)/(1-y))
        double v = 1, y2 = y * y, t = y2, z = t / 3;
        for (double i = 3; z != 0; z = (t *= y2) / (i += 2)) v += z;
        return v * y * 2;
    }
}