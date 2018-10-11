package cn.tellwhy.algorithm;

/*
* 简单的计算
*/
public class DirectCalculation {

    //方差s^2 = [(x_1 - x_avg)^2 +...(x_n - x_avg)^2] / n
    public static double calculateVariance(final double[] inputs) {
        return calculateSquareReduce(inputs) / inputs.length;
    }

    //标准差σ=sqrt(s^2)
    public static double calculateStandardDeviation(final double[] inputs) {
        return Math.sqrt(calculateVariance(inputs));
    }

    private static double calculateAverage(final double[] inputs){
        double accumulate = 0;
        for (double each : inputs) {//求和
            accumulate += each;
        }
        return accumulate / inputs.length;//求平均值
    }

    private static double calculateSquareReduce(final double[] inputs){
        double avg = calculateAverage(inputs);
        double squares = 0;

        for (double each : inputs) {//求方差
            squares += (each - avg) * (each - avg);
        }
        return squares;
    }
}
