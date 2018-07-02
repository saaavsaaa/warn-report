package cn.tellwhy.code.astar;

/**
 * Created by aaa on 18-3-12.
 */
public class Data {
    Point point;
    double g;
    double h;
    Data parent;
    
    public Data(Point p, double g, double h, Data parent)
    {
        this.point = p;
        this.g = g;
        this.h = h;
        this.parent = parent;
    }
    
    double f()
    {
        return g + h;
    }
}
