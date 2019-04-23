package cn.tellwhy.algorithm.isodata;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 类别
* */
public class Cluster {

    private Point center;
    private Point oldCenter;//不知道有没有用，先存着

    private List<Point> points;

    private double averageDistance;
    private List<Double> squaredErrors;

    public void clear() {
        points.clear();
        squaredErrors.clear();
        center = null;
        oldCenter = null;
    }

    /*
    * 最大标准差的分量
    * */
    public Pair<Double, String> maxStandardDeviation() {
        double resultStandardDeviation = 0;
        String resultProperty = "";
        squaredErrors = new ArrayList<>();
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
            double eachSquaredError = 0;
            for (Point eachPoint : points) {
                Double centerValue = center.getValues().get(eachTitle);
                Double curretValue = eachPoint.getValues().get(eachTitle);
                eachSquaredError += Math.pow(Math.abs(centerValue - curretValue), 2);
            }
            eachSquaredError = Math.sqrt(eachSquaredError / points.size());
            squaredErrors.add(eachSquaredError);//应该没什么用，先存着玩
            if (eachSquaredError > resultStandardDeviation) {
                resultStandardDeviation = eachSquaredError;
                resultProperty = eachTitle;
            }
        }
        System.out.println("maxStandardDeviation:" + resultStandardDeviation);
        return new ImmutablePair<Double, String>(resultStandardDeviation, resultProperty);
    }

    /*
    * 计算各点到中心的平均距离
    * */
    public void calculateAverageDistance() {
        double totalDistance = 0;
        for (Point eachPoint : points) {
            totalDistance += eachPoint.distanceEuclidean(center);
        }
        averageDistance = totalDistance / points.size();
    }

    /*
    * 计算中心位置，并更新值，这个中心位置值不一定有点?
    * */
    public void updateCenterValue() {
        oldCenter = center;
        Map<String, Double> values = new HashMap<>();
        //各维度数值平均
        for (Point eachPoint : points) {
            for (String eachTitle : ISODataConstants.Data_Value_Title) {
                if (values.containsKey(eachTitle)) {
                    double oldValue = values.get(eachTitle);
                    values.put(eachTitle, oldValue + eachPoint.getValues().get(eachTitle));
                } else {
                    values.put(eachTitle, eachPoint.getValues().get(eachTitle));
                }
            }
        }
        // 除以 point个数
        Point possiblePoint = new Point();
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
            double value = values.get(eachTitle);
            possiblePoint.getValues().put(eachTitle, value / points.size());
        }
        this.center = possiblePoint;
        calculateAverageDistance();
    }

    public Cluster setCenter(Point center) {
        this.center = center;
        return this;
    }

    public Point getCenter() {
        return center;
    }

    public Cluster setPoint(Point point) {
        this.points.add(point);
        return this;
    }

    public Cluster setPoints(List<Point> points) {
        this.points.addAll(points);
        return this;
    }

    public List<Point> getPoints() {
        return points;
    }

    public double getAverageDistance() {
        return averageDistance;
    }
}
