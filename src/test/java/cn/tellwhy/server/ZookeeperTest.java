package cn.tellwhy.server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

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
    public void delete() throws KeeperException, InterruptedException {
        String path = "/config";
        zk.delete(path, -1);
        System.out.println("删除 :" + path);
    }
    
    @Test
    public void check() throws KeeperException, InterruptedException {
        String path = "/config";
        zk.transaction().check(path, -1).setData(path, "aaa".getBytes(), -1).commit();
//        byte[] data = zk.getData(path, false, null);
//        System.out.println(path + " data :" + new String(data));
    }
    
    @Test
    public void createNodes() throws KeeperException, InterruptedException {
        Transaction transaction = zk.transaction();
        createAll("/config/datasource/a/aa", "children data", transaction);
//        createAll("/config/datasource/b/bb", "children data", transaction);
        transaction.commit();
//        useAsync = true;
//        commit(transaction);
    }
    
    private void createAll(String path, String data, Transaction transaction) throws KeeperException, InterruptedException {
        //todo sync cache
        List<String> paths = getPathNodes(path);
        Iterator<String> nodes = paths.iterator();
        while (nodes.hasNext()){
            String node = nodes.next();
            transaction.check(node, -1);
            // contrast cache
            if (null == existNode(node, zk)){
                System.out.println(node);
                transaction.create(node, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        }
    }
    
    private List<String> getPathNodes(String path){
        List<String> paths = new LinkedList<>();
        int index = 1;
        int position = path.indexOf('/', index);
        
        do{
            paths.add(path.substring(0, position));
            index = position + 1;
            position = path.indexOf('/', index);
        }
        while (position > -1);
        paths.add(path);
        return paths;
    }
    
    private boolean useAsync = false;
    static class MultiResult {
        int rc;
        List<OpResult> results;
        boolean finished = false;
    }
    private List<OpResult> commit(Transaction txn) throws KeeperException, InterruptedException {
        if (useAsync) {
            final MultiResult res = new MultiResult();
            txn.commit(new AsyncCallback.MultiCallback() {
                @Override
                public void processResult(int rc, String path, Object ctx,
                                          List<OpResult> opResults) {
                    synchronized (res) {
                        res.rc = rc;
                        res.results = opResults;
                        res.finished = true;
                        res.notifyAll();
                    }
                }
            }, null);
            synchronized (res) {
                while (!res.finished) {
                    res.wait();
                }
            }
            if (KeeperException.Code.OK.intValue() != res.rc) {
                KeeperException ke = KeeperException.create(KeeperException.Code.get(res.rc));
                throw ke;
            }
            return res.results;
        } else {
            return txn.commit();
        }
    }
    
    @Test
    public void existNode() throws KeeperException, InterruptedException {
        String path = "/test";
        existNode(path, zk);
    }
    
    @Test
    public void getNode() throws KeeperException, InterruptedException {
        String path = "/config"; // "/orchestration-yaml-test/demo_ds_ms";
        List<String> childList = zk.getChildren(path, false);
        childList.forEach(c -> System.out.println(c));
    }
    
    public static List<String> getPathOrderNodes(String path){
        List<String> paths = new ArrayList<>();
        char[] chars = path.toCharArray();
        StringBuilder builder = new StringBuilder('/');
        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == "/".charAt(0)){
                paths.add(builder.toString());
                builder = new StringBuilder('/');
                continue;
            }
            builder.append(chars[i]);
            if (i == chars.length - 1){
                paths.add(builder.toString());
            }
        }
        return paths;
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
    public void deleteCurrentBranch() {
        String key = "";
    }
    
    @Test
    public void deleteAllChildren() {
        String key = "";
    }
    
    @Test
    public void deleteAllNode() throws KeeperException, InterruptedException, ExecutionException {
        String path = "/config/datasource/a/aa";
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
        System.out.println("判断节点" + path + "是否存在:" + result);
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
