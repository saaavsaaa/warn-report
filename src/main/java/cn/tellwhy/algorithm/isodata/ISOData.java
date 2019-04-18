package cn.tellwhy.algorithm.isodata;

import java.util.ArrayList;
import java.util.List;

/*
* 代码未做异常处理，为了获得错误
* */
public class ISOData {
    int K = 5;  // 预期的聚类中心数目；
    int theta_N = 1; //θN 每一聚类域中最少的样本数目，若少于此数即不作为一个独立的聚类；
    double theta_S = 1; //θS 一个聚类域中样本距离分布的标准差；
    double theta_c = 3; //θc 两个聚类中心间的最小距离，若小于此数，两个聚类需进行合并；
    int L = 100; // 在一次迭代运算中可以合并的聚类中心的最多对数；
    int I = 10000; // 迭代运算的次数。

    List<Cluster> initClusters = new ArrayList<>();

    // 商品小分类
    // 客户价值高且avg_interval与最后一次购买时间的差值

    public void start() {
        List<Point> points = new ArrayList<>();
        init(points);
        cancelTinyClusters();
        updateClusterCenter();
    }

    /*
    * 5.计算样本到各聚类中心的距离
    * */
    private void calculatePointsDistance() {
        List<Point> allPoints = new ArrayList<>();
        initClusters.forEach(cluster -> allPoints.addAll(cluster.getPoints()));

        allPoints.forEach(point -> point.calculateCenterDistances(initClusters));
    }

    /*
    * 4.更新每个类别的中心位置
    * */
    private void updateClusterCenter() {
        initClusters.forEach(Cluster::updateCenterValue);
    }

    /*
    * 3.取消很小的分类，业务关系暂时不需要，先不实现
    * */
    private void cancelTinyClusters() {

    }

    /*
    * 1.先随便选K个中心
    * */
    private void init(final List<Point> points) {
        int size = points.size() - 3; //随便减一下，第一步不需要准确的结果
        for (int i = 0; i < K; i++) {
            Point centerCurrent = points.get(i % (size / K));
            initClusters.add(new Cluster().setCenter(centerCurrent).setPoints(centerCurrent));
        }
        initClusterDistribution(points);
    }

    /*
    * 2.循环所有节点，取距离最近的中心加入
    * */
    private void initClusterDistribution(final List<Point> points) {
        points.forEach(eachPoint -> {
            double distance = Double.MAX_VALUE;
            Cluster currentCluster = initClusters.get(0);
            for (Cluster eachCluster : initClusters) {
                Point currentCenter = eachCluster.getCenter();
                if (eachPoint.equals(currentCenter)) {
                    return;
                }
                double currentDistance = currentCenter.distanceEuclidean(eachPoint);
                if (currentDistance < 0) {
                    throw new ArithmeticException("欧式距离值溢出:" + eachPoint.toString());
                }
                if (distance > currentDistance) {
                    distance = currentDistance;
                    currentCluster = eachCluster;
                }
            }
            currentCluster.setPoints(eachPoint);
        });
    }
}

