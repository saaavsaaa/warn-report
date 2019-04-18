package cn.tellwhy.algorithm.isodata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Point {

//    private double y; // 客户价值
//    private double x; // avg_interval与最后一次购买时间的差值

    //字段名和值
    private Map<String, Double> values = new HashMap<>();

    @Deprecated
    private double standardDeviation(final Point anotherPoint) {
        double squaredError = 0;
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
            Double otherValue = anotherPoint.getValues().get(eachTitle);
            Double curretValue = values.get(eachTitle);
            squaredError += Math.pow(Math.abs(otherValue - curretValue), 2);
        }
        squaredError = squaredError / ISODataConstants.Data_Value_Title.length;
        return Math.sqrt(squaredError);
    }

    /*
    * 两点间欧式距离
    * */
    public double distanceEuclidean(final Point anotherPoint) {
        double powDistance = 0;
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
            Double otherValue = anotherPoint.getValues().get(eachTitle);
            Double curretValue = values.get(eachTitle);
            powDistance += Math.abs(Math.pow(otherValue, 2) + Math.pow(curretValue, 2));
        }
        return Math.sqrt(powDistance); //勾股定理求斜边
    }

    /*
    * 到各聚类中心的欧式距离
    * */
    public List<Double> calculateCenterDistances(final List<Cluster> clusters) {
        List<Double> result = new ArrayList<>();
        for (Cluster eachCluster : clusters) {
            result.add(distanceEuclidean(eachCluster.getCenter()));
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "---";
        for (String eachKey : values.keySet()) {
            result += "|" + eachKey + ":" + values.get(eachKey);
        }
        return result;
    }

    public Map<String, Double> getValues() {
        return values;
    }
}
