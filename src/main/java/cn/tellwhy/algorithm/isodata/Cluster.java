package cn.tellwhy.algorithm.isodata;

import java.util.List;

/*
* 类别
* */
public class Cluster {

    private Point center;
    private List<Point> points;

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
