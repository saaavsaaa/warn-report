package cn.tellwhy.algorithm.isodata;

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
        Point possiblePoint = new Point();
    }

    private void calculateAverageDistance() {

    }

    public Cluster setCenter(Point center) {
        this.center = center;
        return this;
    }

    public void setPoints(Point point) {
        this.points.add(point);
    }

    public Point getCenter() {
        return center;
    }
}
