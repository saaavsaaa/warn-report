
package cn.tellwhy.third.zk;

public final class ZookeeperConfiguration {
    private String servers;
    private String namespace;
    private int sessionTimeoutMilliseconds;
    private String digest;
    
    public String getNamespace() {
        return namespace;
    }
    
    public int getSessionTimeoutMilliseconds() {
        return sessionTimeoutMilliseconds;
    }
    
    public String getServers() {
        return servers;
    }
    
    public void setServers(String servers) {
        this.servers = servers;
    }
    
    public String getDigest() {
        return digest;
    }
    
    public void setDigest(String digest) {
        this.digest = digest;
    }
}
