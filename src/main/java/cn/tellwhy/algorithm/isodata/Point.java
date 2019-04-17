package cn.tellwhy.algorithm.isodata;

import java.util.HashMap;
import java.util.Map;

public class Point {

//    private double y; // 客户价值
//    private double x; // avg_interval与最后一次购买时间的差值

    //字段名和值
    private Map<String, Double> values = new HashMap<>();

    public double distanceEuclidean(final Point anotherPoint) {
        double powDistance = 0;
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
            Double otherValue = anotherPoint.getValues().get(eachTitle);
            Double curretValue = values.get(eachTitle);
            powDistance += Math.pow(Math.abs(otherValue - curretValue), 2);
        }
        return Math.sqrt(powDistance); //勾股定理求斜边
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
