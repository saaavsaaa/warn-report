package cn.tellwhy.algorithm.isodata;

import java.util.List;

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
        Point possiblePoint = new Point();
        //各维度数值平均
        for (String eachTitle : ISODataConstants.Data_Value_Title) {
        }
        for (Point eachPoint : points) {

        }
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
