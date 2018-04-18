package server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.*;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by aaa on 18-4-9.
 */
public class ZookeeperTest {
    /** zookeeper地址 */
    static final String CONNECT_ADDR = "192.168.2.44:2181";
    /** session超时时间 */
    static final int SESSION_OUTTIME = 200000;//ms
    /** 信号量，阻塞程序执行，用于等待zookeeper连接成功，发送成功信号 */
    static final CountDownLatch connectedSemaphore = new CountDownLatch(1);
    
    static ZooKeeper zk = null;
    
    @BeforeClass
    public static void start() throws IOException {
        zk = new ZooKeeper(CONNECT_ADDR, SESSION_OUTTIME, new Watcher(){
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
                        System.out.println("已连接");
                    }
                }
            }
        });
    }
    
    @Test
    public void createRoot() throws KeeperException, InterruptedException {
        createRoot("/config", "data", zk);
    }
    
    @Test
    public void createChildNode() throws KeeperException, InterruptedException {
        createChildNode("/config/datasource", "children data", zk);
    }
    
    @Test
    public void createNodes() throws KeeperException, InterruptedException {
        createChildNode("/config/datasource/a/aa", "children data", zk);
        createChildNode("/config/datasource/b/bb", "children data", zk);
    }
    
    @Test
    public void existNode() throws KeeperException, InterruptedException {
        String path = "/config";
        existNode(path, zk);
    }
    
    @Test
    public void getNode() throws KeeperException, InterruptedException {
        String path = "/orchestration-yaml-test/demo_ds_ms";
        List<String> childList = zk.getChildren(path, false);
        childList.forEach(c -> System.out.println(c));
    }
    
    @Test
    public void deleteNode() throws KeeperException, InterruptedException {
        String path = "/config";
        //同步
        zk.delete(path, -1);
    
        //异步
        /*zk.delete(path, -1, (rc, p, ctx) -> {
            System.out.println("rc=====" + rc);
            System.out.println("path======" + p);
            System.out.println("ctc======" + p);
        }, "回调值");*/
    
        System.out.println("删除 :" + path);
    }
    
    @Test
    public void deleteAllNode() throws KeeperException, InterruptedException, ExecutionException {
        String path = "/orchestration-yaml-test/demo_ds_ms/state/datasources";
        Transaction transaction = zk.transaction();
        //取全部节点，锁缓存
        
        Stack<String> pathStack = getPaths(path);
        while (!pathStack.empty()){
            String node = pathStack.pop();
            if (null != existNode(node, zk)){
                
            }
            delete(node);
        }
    
        transaction.commit();
        System.out.println("删除 :" + path);
    }
    
    private Stack<String> getPaths(String path){
        Stack<String> pathStack = new Stack<>();
        int index = 1;
        int position = path.indexOf('/', index);
    
        do{
            pathStack.push(path.substring(0, position));
            index = position + 1;
            position = path.indexOf('/', index);
        }
        while (position > -1);
        pathStack.push(path);
        return pathStack;
    }
    

    
    private String delete(String path) {
        try {
            zk.delete(path, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }
    
    public static void main(String[] args) throws Exception{
    
        start();
        
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
    private static Object existNode(String path, ZooKeeper zk) throws KeeperException, InterruptedException {
        Stat result = zk.exists(path, false);
        System.out.println("判断节点是否存在:" + result);
        return result;
    }
}

/**
 * 生产者线程
 */
class Producer implements Runnable{
    private final ArrayBlockingQueue<String> nodes;
    Producer(ArrayBlockingQueue<String> arrayBlockingQueue){
        this.nodes = arrayBlockingQueue;
    }
    
    @Override
    public void run() {
        while (true) {
            Produce();
        }
    }
    
    private void Produce(){
        try {
            nodes.put("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

/**
 * 消费者线程
 */
class Consumer implements Runnable{
    
    private ArrayBlockingQueue<String> nodes;
    private final ZooKeeper zk;
    Consumer(ArrayBlockingQueue<String> arrayBlockingQueue, ZooKeeper zk){
        this.nodes = arrayBlockingQueue;
        this.zk = zk;
    }
    
    @Override
    public void run() {
        while (true){
            try {
                zk.delete(nodes.take(), -1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (KeeperException e) {
                e.printStackTrace();
            }
        }
    }
}
