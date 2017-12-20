import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;
import org.junit.Test;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Properties;

/**
 * Created by aaa on 17-12-19.
 */
public class TestAgentVMAttacher {
    public static void main(String[] args) throws Exception {
        String pid = "6236";
        String agentPath = "/home/aaa/Code/agent/agentest.so";
        VirtualMachine virtualMachine = com.sun.tools.attach.VirtualMachine.attach(pid);
        virtualMachine.loadAgentPath(agentPath, null);
        virtualMachine.detach();
    }
    
    @Test
    public void test() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {
        // args[0]传入的是某个jvm进程的pid
        String targetPid = "9527";
        VirtualMachine virtualmachine = VirtualMachine.attach(targetPid);
//        virtualmachine.loadAgent("/home/aaa/Code/agent-1.0-SNAPSHOT.jar");
    
    
        // 让JVM加载jmx Agent
        String javaHome = virtualmachine.getSystemProperties().getProperty("java.home");
        String jmxAgent = javaHome + File.separator + "lib" + File.separator + "management-agent.jar";
        virtualmachine.loadAgent(jmxAgent, "com.sun.management.jmxremote");
        // 获得连接地址
        Properties properties = virtualmachine.getAgentProperties();
        String address = (String) properties.get("com.sun.management.jmxremote.localConnectorAddress");
    
        // Detach
        virtualmachine.detach();
        // 通过jxm address来获取RuntimeMXBean对象，从而得到虚拟机运行时相关信息
        JMXServiceURL url = new JMXServiceURL(address);
        JMXConnector connector = JMXConnectorFactory.connect(url);
        RuntimeMXBean rmxb = ManagementFactory.newPlatformMXBeanProxy(connector.getMBeanServerConnection(), "java.lang:type=Runtime",
                RuntimeMXBean.class);
        // 得到目标虚拟机占用cpu时间
        System.out.println(rmxb.getUptime());
    }
}
