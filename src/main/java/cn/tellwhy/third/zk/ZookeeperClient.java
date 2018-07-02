package cn.tellwhy.third.zk;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * Created by aaa on 18-4-16.
 */
public class ZookeeperClient {
    public static final String PATH_SEPARATOR = "/";
    private static final CountDownLatch CONNECTED = new CountDownLatch(1);
    
    private final Watcher watcher;
    private final String servers;
    private final int sessionTimeOut;
    private final String rootNode;
    
    private ZooKeeper zooKeeper;
    private List<ACL> authorities;
    
    static ZookeeperClient newClient(final ZookeeperConfiguration zkConfig){
        ZookeeperClient client = new ZookeeperClient(zkConfig);
        client.newZookeeper();
        return client;
    }
    
    private ZookeeperClient(final ZookeeperConfiguration zkConfig) {
        watcher = buildWatcher(zkConfig);
        servers = zkConfig.getServers();
        sessionTimeOut = zkConfig.getSessionTimeoutMilliseconds();
        rootNode = zkConfig.getNamespace();
        setAuthorities(zkConfig);
    }
    
    private void newZookeeper(){
        try {
            zooKeeper = new ZooKeeper(servers, sessionTimeOut, watcher);
            CONNECTED.wait();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
    
    private Watcher buildWatcher(final ZookeeperConfiguration zkConfig) {
        return new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                if(Event.KeeperState.SyncConnected == event.getState()){
                    if(Event.EventType.None == event.getType()){
                        CONNECTED.countDown();
                    }
                }
            }
        };
    }
    
    private void setAuthorities(ZookeeperConfiguration zkConfig){
        if (Strings.isNullOrEmpty(zkConfig.getDigest())) {
            return;
        }
        zooKeeper.addAuthInfo("digest", zkConfig.getDigest().getBytes(Charsets.UTF_8));
        authorities = ZooDefs.Ids.CREATOR_ALL_ACL;
    }
    
    public byte[] getData(String key) throws KeeperException, InterruptedException {
        return zooKeeper.getData(getRealPath(key), false, null);
    }
    
    private String getRealPath(String path){
        if (path.equals(rootNode)){
            return new StringBuilder().append(PATH_SEPARATOR).append(rootNode).toString();
        }
        return new StringBuilder().append(PATH_SEPARATOR).append(rootNode).append(PATH_SEPARATOR).append(path).toString();
    }
    
    public boolean checkExists(String key) throws KeeperException, InterruptedException {
        return null != zooKeeper.exists(getRealPath(key), false);
    }
    
    public List<String> getChildren(String key) throws KeeperException, InterruptedException {
        return zooKeeper.getChildren(getRealPath(key), false);
    }
    
    public void create(String path, byte[] data, CreateMode createMode) throws KeeperException, InterruptedException {
        // TODO: 18-4-12
        if (path.indexOf("/") > -1){
            System.out.println("exist / need op ..=============================================");
        }
        zooKeeper.create(path, data, authorities, createMode);
    }
    
    private void createRootNode() throws KeeperException, InterruptedException {
        if (checkExists(rootNode)){
            return;
        }
        zooKeeper.create(getRealPath(rootNode), new byte[0], authorities, CreateMode.PERSISTENT);
    }
    
    public void update(String key, byte[] data) throws KeeperException, InterruptedException {
        zooKeeper.transaction().setData(getRealPath(key), data, -1).commit();
    }
}
