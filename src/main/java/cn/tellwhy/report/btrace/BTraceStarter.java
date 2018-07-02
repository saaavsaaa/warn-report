package cn.tellwhy.report.btrace;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * Created by aaa on 17-9-27.
 */
public class BTraceStarter {
    public static void main(String[] args) throws Exception {
        
        String classpath = System.getProperty("java.class.path");
        if (!classpath.contains("tools.jar")) {
            throw new RuntimeException("请在类路径中设置tools.jar!");
        }
        
        System.setProperty("com.sun.btrace.probeDescPath", ".");
        System.setProperty("com.sun.btrace.dumpClasses", "true");
        System.setProperty("com.sun.btrace.dumpDir", "./dump");
        System.setProperty("com.sun.btrace.debug", "false");
        System.setProperty("com.sun.btrace.unsafe", "true");
        
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("请输入目标进程PID： ");
        String pid = in.readLine();
        
        String script = new File(BTraceStarter.class.getResource("BTraceHttpServletRequestCopier.class").getFile()).getCanonicalPath();
        com.sun.btrace.client.Main.main(new String[] { pid, script });
    }
}
