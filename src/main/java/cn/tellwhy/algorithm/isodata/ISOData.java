package cn.tellwhy.algorithm.isodata;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* 代码未做异常处理，为了获得错误
* */
public class ISOData {
    int K = 5;  // 预期的聚类中心数目；
    int theta_N = 1; //θN 每一聚类域中最少的样本数目，若少于此数即不作为一个独立的聚类；
    double theta_S = 1; //θS 一个类中样本距离分布的标准差阈值。类内最大标准差分量应小于 θs
    double theta_c = 3; //θc 两个聚类中心间的最小距离，若小于此数，两个聚类需进行合并；
    int L = 100; // 在一次迭代运算中可以合并的聚类中心的最多对数；
    int I = 10000; // 迭代运算的次数。

    int alreadyRunCount = 0;

    List<Cluster> initClusters = new ArrayList<>();

    // 商品小分类
    // 客户价值高且avg_interval与最后一次购买时间的差值

    public void start() {
        List<Point> points = new ArrayList<>();
        //1
        init(points);
        initClusterDistribution(points);
        //2
        cancelTinyClusters();
        updateClusterCenter();
        double totalAverage = calculatePointsDistance();

        //此处判断迭代次数，回头再写 5|6|7
        //3
        boolean splitted = splitCluster(totalAverage);
        if (splitted) {
            alreadyRunCount ++;
            // 跳2，改递归
        }
    }

    /*
    * 3.2 合并
    * */
    private void calculateCenterDistance() {
        Map<Integer, Double> centerDistances = new HashMap<>();// index,distance
        List<Integer> clusterIndex1 = new ArrayList<>();//保存center1
        List<Integer> clusterIndex2 = new ArrayList<>();//保存center2
        //小于规定距离的合并
        //取任意两个中心的距离，小于规定距离，保存下来
        //判断是否有重合点，如果有重合，合并其中较小的或先来的
        int index = 0;//保存入选在列表中的索引
        for (int i = 0; i < initClusters.size(); i++) { //n!
            Point center1 = initClusters.get(i).getCenter();
            for (int j = i + 1; j < initClusters.size(); j++) {
                Point center2 = initClusters.get(i).getCenter();
                double currentDistance = center1.distanceEuclidean(center2);
                if (currentDistance < theta_c) {
                    //判断节点是否已经入选，入选则根据距离大小保留小的
                    //如果center1存在，判断center2是否存在，同时存在则三取其一
                    //三个的情况还包括center1或2中同时有i和j
                    //如新的较小，去掉原来的，三个集合同时修改
                    //只有两个就二取一
                    //不存在直接加
                    //因为循环顺序，还需要判断j是否在clusterIndex1的情况
                    if (!clusterIndex1.contains(i) && !clusterIndex1.contains(j) && !clusterIndex1.contains(j)) {
                        clusterIndex1.add(index, i);
                        clusterIndex2.add(index, j);
                        centerDistances.put(index, currentDistance);
                        index++;
                    }
                    //先判断三个的
                    //再判断两个的

                    if (clusterIndex1.contains(i)) {
                        int keyI = clusterIndex1.indexOf(i);
                        if (keyI > -1) {
                            double existDistanceI = centerDistances.get(keyI);
                            if (existDistanceI > currentDistance) {
                                if (clusterIndex2.contains(j)) {
                                    int keyJ = clusterIndex1.indexOf(j);
                                    if (keyJ > -1) {
                                        double existDistanceJ = centerDistances.get(keyJ);

                                    }
                                }
                            } else {

                            }
                        } else {

                        }
                    }
                }
            }
        }
    }

    //调用四次传，两次传相反参数，两次传两个List相同
    private void checkCenterDistanceExistTwo(final List<Integer> clusterIndex1, final List<Integer> clusterIndex2,
                                             final int i1, final int i2, final Map<Integer, Double> centerDistances) {
        if (clusterIndex1.contains(i1) && clusterIndex2.contains(i2)) {
            //统一列即包含i又包含j，放到三个里处理
        }
    }

    //调用两次传相反参数
    private void checkCenterDistanceExistOne(final List<Integer> clusterIndex1, final List<Integer> clusterIndex2,
                                             final int i1, final int i2, final Map<Integer, Double> centerDistances) {
        if (clusterIndex1.contains(i1) && !clusterIndex2.contains(i2)) {
            //统一列即包含i又包含j，放到三个里处理
        }
    }

    /*
    * 3.1 类分裂
    * */
    private boolean splitCluster(double totalAverage) {
        boolean result = false;
        List<Cluster> deleteCluster = new ArrayList<>();
        for (Cluster eachCluster : initClusters) {
            Pair<Double, String> top = eachCluster.maxStandardDeviation();
            if (top.getLeft() > theta_S &&
                    ((eachCluster.getAverageDistance() > totalAverage) || (initClusters.size() < K / 2))) {
                //分裂 似乎都是用的超参数做分裂系数，我打算在最大分量和最小分量间连线，按线的左和非左分，不过目前时间紧，先0.5回头再说
                Point newCenter1 = eachCluster.getCenter().Copy().displaceAxisOpposite(top.getRight(), 0.5 * top.getLeft());
                Point newCenter2 = eachCluster.getCenter().Copy().displaceAxisOpposite(top.getRight(), - 0.5 * top.getLeft());
                List<Cluster> newClusters = new ArrayList<>();
                newClusters.add(new Cluster().setCenter(newCenter1));
                newClusters.add(new Cluster().setCenter(newCenter2));
                initClusterDistribution(newClusters, eachCluster.getPoints());
                deleteCluster.add(eachCluster);
                initClusters.addAll(newClusters);
                result = true;
            }
        }

        for (Cluster eachCluster : deleteCluster) {
            eachCluster.Clear();
            initClusters.remove(eachCluster);
        }
        deleteCluster.clear();

        return result;
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
        // TODO: 这里可以不是K，可以随便选个值做初始聚类数
        for (int i = 0; i < K; i++) {
            Point centerCurrent = points.get(i % (size / K));
            initClusters.add(new Cluster().setCenter(centerCurrent).setPoints(centerCurrent));
        }
    }
}

