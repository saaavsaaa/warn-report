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
        //1
        init(points);
        //2
        initClusterDistribution(points);
        cancelTinyClusters();
        updateClusterCenter();
        double totalAverage = calculatePointsDistance();

        //此处判断迭代次数，回头再写 5|6|7
        //3
        calculateClusterStandardDeviation(totalAverage);
    }

    /*
    * 3.1 类分裂
    * */
    private void calculateClusterStandardDeviation(double totalAverage) {
        for (Cluster eachCluster : initClusters) {
            double standardDeviation = eachCluster.maxStandardDeviation();
            if (standardDeviation > theta_S &&
                    ((eachCluster.getAverageDistance() > totalAverage) || (initClusters.size() < K / 2))) {
                //分裂

                //initClusterDistribution

                //跳转到2
            }
        }
    }

    /*
    * 2.4 计算样本到各聚类中心的距离
    * 计算聚类内平均值，再将平均值加总取平均
    * */
    private double calculatePointsDistance() {
        List<Point> allPoints = new ArrayList<>();
        double eachDistanceTotal = 0D;
        for (Cluster eachCluster : initClusters) {
            allPoints.addAll(eachCluster.getPoints());
            eachDistanceTotal += eachCluster.getAverageDistance();
        }
        double eachDistanceAverage = eachDistanceTotal / initClusters.size();

        //计算所有距离的平均值，先留着
        /*List<Double> allDistances = new ArrayList<>();
        allPoints.forEach(point -> allDistances.addAll(point.calculateCenterDistances(initClusters)));
        double allDistanceValue = 0;
        for (Double eachDistance : allDistances) {
            allDistanceValue += eachDistance;
        }
        double averageDistance = allDistanceValue / allDistances.size();*/
        return eachDistanceAverage;
    }

    /*
    * 2.3 更新每个类别的中心位置
    * */
    private void updateClusterCenter() {
        initClusters.forEach(Cluster::updateCenterValue);
    }

    /*
    * 2.2 取消很小的分类，业务关系暂时不需要，先不实现
    * */
    private void cancelTinyClusters() {

    }

    /*
     * 2.1 循环所有节点，取距离最近的中心加入
     * */
    private void initClusterDistribution(final List<Point> points) {
        initClusterDistribution(initClusters, points);
    }

    private void initClusterDistribution(final List<Cluster> clusters, final List<Point> points) {
        points.forEach(eachPoint -> {
            double distance = Double.MAX_VALUE;
            Cluster currentCluster = clusters.get(0);//初始化
            // 将当前点加入到所有聚类中欧式距离最短的
            for (Cluster eachCluster : clusters) {
                Point currentCenter = eachCluster.getCenter();
                if (eachPoint.equals(currentCenter)) {
                    return; //如果已经是当前类的中心，则循环下一个样本点
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

    /*
    * 1.先随便选K个中心
    * */
    private void init(final List<Point> points) {
        int size = points.size() - 3; //随便减一下，第一步不需要准确的结果
        // TODO: 这里不是K，这里应该是K近邻聚类数
        for (int i = 0; i < K; i++) {
            Point centerCurrent = points.get(i % (size / K));
            initClusters.add(new Cluster().setCenter(centerCurrent).setPoints(centerCurrent));
        }
    }
}

