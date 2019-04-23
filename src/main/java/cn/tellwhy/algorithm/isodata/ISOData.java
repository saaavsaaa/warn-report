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
    int K = 15;  // 预期的聚类中心数目；
    int theta_N = 1; //θN 每一聚类域中最少的样本数目，若少于此数即不作为一个独立的聚类；
    double theta_S = 1; //θS 一个类中样本距离分布的标准差阈值。类内最大标准差分量应小于 θs
    double theta_c = 3; //θc 两个聚类中心间的最小距离，若小于此数，两个聚类需进行合并；
    int L = 100; // 在一次迭代运算中可以合并的聚类中心的最多对数；
    int I = 10000; // 迭代运算的次数。

    List<Cluster> initClusters = new ArrayList<>();

    // 商品小分类
    // 客户价值高且avg_interval与最后一次购买时间的差值

    public void start(List<Point> points) {
        //1
        init(7, points);
        boolean update = false;
        //2

        for (int loop = 0; loop < I; loop++) {
            if (update) {
                initClusters.forEach(cluster -> cluster.getPoints().clear());
                update = false;
            }
            initClusterDistribution(points);
            cancelTinyClusters();
            updateClusterCenter();
            double totalAverage = calculatePointsDistance();

            //3
            if (initClusters.size() <= K/2) {
                boolean splitted = splitCluster(totalAverage);
                if (splitted) {
                    update = true;
                    continue;
                }
            }

            if (loop % 2 == 0 || initClusters.size() > 2*K) {
                calculateCenterDistance();
                update = true;
            }
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
        for (int i = 0; i < initClusters.size(); i++) { //n! 新前面中心索引
            Point center1 = initClusters.get(i).getCenter();
            for (int j = i + 1; j < initClusters.size(); j++) { // 新后面中心索引
                Point center2 = initClusters.get(i).getCenter();
                double currentDistance = center1.distanceEuclidean(center2);
                if (currentDistance < theta_c) {
                    //判断节点是否已经入选，入选则根据距离大小保留小的
                    //如果center1存在，判断center2是否存在，同时存在则三取其一
                    //三个的情况还包括center1或2中同时有i和j
                    //如新的较小，去掉原来的，三个集合同时修改
                    //只有两个就二取一
                    //不存在直接加
                    if (!clusterIndex1.contains(i) && !clusterIndex1.contains(j) && !clusterIndex2.contains(j)) {
                        clusterIndex1.add(index, i);
                        clusterIndex2.add(index, j);
                        centerDistances.put(index, currentDistance);
                        index++;
                        continue;
                    }
                    //先判断两个都存在的
                    //因为循环顺序，还需要判断j是否在clusterIndex1的情况
                    boolean needContinue = checkCenterDistanceExistTwo(currentDistance, clusterIndex1, clusterIndex2, i, j, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    needContinue = checkCenterDistanceExistTwo(currentDistance, clusterIndex1, clusterIndex2, j, i, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    //同一列即包含i又包含j
                    needContinue = checkCenterDistanceExistTwo(currentDistance, clusterIndex1, clusterIndex1, i, j, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    needContinue = checkCenterDistanceExistTwo(currentDistance, clusterIndex2, clusterIndex2, i, j, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    //再判断只存在一个的
                    needContinue = checkCenterDistanceExistOne(currentDistance, clusterIndex1, clusterIndex2, i, j, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    needContinue = checkCenterDistanceExistOne(currentDistance, clusterIndex2, clusterIndex1, i, j, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    needContinue = checkCenterDistanceExistOne(currentDistance, clusterIndex1, clusterIndex2, j, i, centerDistances);
                    if (needContinue) {
                        continue;
                    }
                    checkCenterDistanceExistOne(currentDistance, clusterIndex2, clusterIndex1, j, i, centerDistances);
                }
            }
        }
        //合并
        for (Integer eachKey : centerDistances.keySet()) {
            int i1 = clusterIndex1.get(eachKey); // 保存了前面的节点索引
            int i2 = clusterIndex2.get(eachKey); // 保存了后面的节点索引
            Point center1 = initClusters.get(i1).getCenter();
            Point center2 = initClusters.get(i2).getCenter();
            Point newCenter = new Point();
            for (String eachProperty : ISODataConstants.Data_Value_Title) {
                double p = center1.getValues().get(eachProperty) * center1.getValues().size();
                double s = center2.getValues().get(eachProperty) * center2.getValues().size();
                newCenter.getValues().put(eachProperty, (p+s) / (center1.getValues().size()+center2.getValues().size()));
            }
            initClusters.get(i1).setCenter(newCenter);
            initClusters.get(i1).setPoints(initClusters.get(i2).getPoints());
            initClusters.get(i2).clear();
            initClusters.remove(i2);
        }
    }

    //调用四次传相反参数
    private boolean checkCenterDistanceExistOne(final double currentDistance,
                                             final List<Integer> clusterIndex1, final List<Integer> clusterIndex2,
                                             final int i1, final int i2, final Map<Integer, Double> centerDistances) {
        // 两个都存在的已经判断过了，只剩下存在一个的了
        if (clusterIndex1.contains(i1)) {
            int keyI1 = clusterIndex1.indexOf(i1);
            double existDistanceI1 = centerDistances.get(keyI1);
            if (existDistanceI1 > currentDistance) {
                clusterIndex2.set(keyI1, i2); // 更新key1对应的后面中心的序号
                centerDistances.put(keyI1, currentDistance);
                return true;
            }
        }
        return false;
    }

    //调用四次传，两次传相反参数，两次传两个List相同
    // added 是否加入新距离
    // result 大范围处理后不再需要继续判断
    private boolean checkCenterDistanceExistTwo(final double currentDistance,
                                             final List<Integer> clusterIndex1, final List<Integer> clusterIndex2,
                                             final int i1, final int i2, final Map<Integer, Double> centerDistances) {
        boolean result = false;
        if (clusterIndex1.contains(i1) && clusterIndex2.contains(i2)) { // 新的距离的前面点用过，同时后面点再另外一个距离中用过
            int keyI1 = clusterIndex1.indexOf(i1); // 保存了前面的节点索引
            int keyI2 = clusterIndex2.indexOf(i2); // 保存了后面的节点索引
            double existDistanceI1 = centerDistances.get(keyI1);
            double existDistanceI2 = centerDistances.get(keyI2);

            // clusterIndex1       clusterIndex2
            // key1 i1             key1 other_value
            // key2 other_value    key2 i2
            if (existDistanceI1 > existDistanceI2) { //如果前面中心的距离大，则删掉前面点
                deleteCenterDistance(clusterIndex1, clusterIndex2, keyI1, centerDistances);
                if (existDistanceI2 > currentDistance) { // 新加入的距离最短，更新key2的距离为新距离，中心索引为新中心索引
                    clusterIndex1.set(keyI2, i1); // 更新key2对应的前面中心的序号
                    centerDistances.put(keyI2, currentDistance);//替换key2为新距离
                    result = true;
                }
            } else {
                deleteCenterDistance(clusterIndex1, clusterIndex2, keyI2, centerDistances);
                if (existDistanceI1 > currentDistance) { // 新加入的距离最短
                    clusterIndex2.set(keyI1, i2); // 更新key1对应的后面中心的序号
                    centerDistances.put(keyI1, currentDistance);
                    result = true;
                }
            }
        }
        return result;
    }

    private void deleteCenterDistance(final List<Integer> clusterIndex1, final List<Integer> clusterIndex2,
                                      final int keyI, final Map<Integer, Double> centerDistances) {
        centerDistances.remove(keyI);
        clusterIndex1.remove(keyI);
        if (clusterIndex1 == clusterIndex2) {
            return;
        }
        clusterIndex2.remove(keyI);
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
            eachCluster.clear();
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
            currentCluster.setPoint(eachPoint);
        });
    }

    /*
    * 1.先随便选K个中心
    * */
    private void init(final int initK, final List<Point> points) {
        int size = points.size() - 3; //随便减一下，第一步不需要准确的结果
        for (int i = 0; i < initK; i++) {
            Point centerCurrent = points.get(i % (size / K));
            initClusters.add(new Cluster().setCenter(centerCurrent).setPoint(centerCurrent));
        }
    }
}

