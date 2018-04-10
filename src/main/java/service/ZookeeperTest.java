package service;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

/**
 * Created by aaa on 18-4-9.
 * https://www.cnblogs.com/520playboy/p/6384594.html?utm_source=itdadao&utm_medium=referral
 */
public class ZookeeperTest {
    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.2.44:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 2000;//ms
    /** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    
    public static void main(String[] args) throws Exception{
    
        ZooKeeper zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                //获取事件的状态
                Event.KeeperState keeperState = event.getState();
                Event.EventType eventType = event.getType();
                //如果是建立连接
                if(Event.KeeperState.SyncConnected == keeperState){
                    if(Event.EventType.None == eventType){
                        //如果建立连接成功，则发送信号量，让后续阻塞程序向下执行
                        connectedSemaphore.countDown();
                        System.out.println("zk 建立连接");
                    }
                }
            }
        });
        
        //进行阻塞
        connectedSemaphore.await();
        
        System.out.println("--------------------------------------------------");
    
        createRoot("/config", "data", zk);
    
        createChildNode("/config/datasource", "children data", zk);
        getNodeData("/config/datasource", zk);
    
        changeChildNode("/config/datasource", "change data", zk);
        getNodeData("/config/datasource", zk);
    
        existNode("/config/datasource", zk);
    
        deleteChildNode("/config/datasource", zk);
        existNode("/config/datasource", zk);
        
        deleteChildNode("/config", zk);
        existNode("/config", zk);

        zk.close();
    }
    
    private static void createRoot(String path, String data, ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }
    
    //创建子节点
    private static void createChildNode(String path, String data, ZooKeeper zk) throws KeeperException, InterruptedException {
        String ret = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println("创建子节点"+ret);
    }
    
    //修改节点的值
    private static void changeChildNode(String path, String data, ZooKeeper zk) throws KeeperException, InterruptedException {
        zk.setData(path, data.getBytes(), -1);
        byte[] result = zk.getData(path, false, null);
        System.out.println("修改节点" + path + "的值" + new String(result));
    }
    
    //删除节点
    private static void deleteChildNode(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        //同步
        //zk.delete(path, -1);
        
        //异步
        zk.delete(path, -1, (rc, p, ctx) -> {
            System.out.println("rc=====" + rc);
            System.out.println("path======" + p);
            System.out.println("ctc======" + p);
        }, "回调值");
        
        System.out.println("删除 :" + path);
    }
    
    //获取节点洗信息
    private static void getNodeData(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        byte[] data = zk.getData(path, false, null);
        System.out.println(path + " data :" + new String(data));
    }
    
    //判断节点是否存在
    private static void existNode(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        Stat result = zk.exists(path, false);
        System.out.println("判断节点是否存在:" + result);
    }
}
