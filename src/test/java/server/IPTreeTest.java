package cn.tellwhy.request;

import com.carrotsearch.sizeof.RamUsageEstimator;
import date.iterator.tools.ip.IPTree;
import date.iterator.tools.ip.IPUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class IPTreeTest {
    @Test
    public void testSearch() throws IOException, InterruptedException {
        List<String> cmds = new LinkedList<String>();
        cmds.add("sh");
        cmds.add("-c");
        cmds.add("");
        String command = "sh -c cat /home/aaa/Downloads/ips | awk  -F ' ' '{print $1}'";
        IPTree ipTree = exec(command);
        System.out.println(RamUsageEstimator.sizeOf(ipTree));
        long start = System.nanoTime();
        String key = ipTree.search("211.139.193.20");
        System.out.println(System.nanoTime() - start);
        System.out.println(key);
    }

    @Test
    public void debugConvertIP() {
        String ip = "211.139.193.20";
        String actual = IPUtil.ip2binaryString(ip);

        System.out.println(actual);

        int[] bits = IPUtil.ip2binary(ip);
        for (int i = 0; i < bits.length; i++) {
            System.out.print(bits[i]);
        }
        System.out.println();
        ip = "211.139.193.0";
        System.out.println(IPUtil.ip2binaryString(ip));
    }

    public static IPTree exec(final String command) throws IOException, InterruptedException {
        IPTree ipTree = new IPTree();

        String output = null;
        Process process = null;
        BufferedReader br = null;
        try {
            List<String> cmds = new LinkedList<String>();
            cmds.add("sh");
            cmds.add("-c");
            cmds.add("cat /home/aaa/Downloads/ips | awk  -F ' ' '{print $1,$5}'");
            ProcessBuilder pb = new ProcessBuilder(cmds);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            //while (p.waitFor(100, TimeUnit.MILLISECONDS));
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                output += line + System.getProperty("line.separator");
                String ip = line.split(" ")[0];
                String key = line.split(" ")[1];
                ipTree.appendIP(ip.split("/")[0], Integer.parseInt(ip.split("/")[1]), key);
            }
//            IPUtil.traversalLevel(ipTree.getRoot());
        } finally {
            if (br != null) {
                br.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        // System.out.println(output);
        return ipTree;
    }

    @Test
    public void testToBinary() {
        // byte[] bytes = InetAddress.getByName("10.0.2.15").getAddress();
        //101.32.104.0 0110 0101 0010 0000 0110 1000 00000000   21
        //101.32.96.0  0110 0101 0010 0000 0110 0000 00000000   20
        String expect = "1100101001000000110100000000000"; // 11011100101101010010011010010110
        String ip = "101.32.96.0"; // "220.181.38.150"
        long start = System.currentTimeMillis();
        String actual = IPUtil.ip2binaryString(ip);
        System.out.println(actual);
        System.out.println(System.currentTimeMillis() - start);
        start = System.currentTimeMillis();
        int[] bits = IPUtil.ip2binary(ip);
        for (int i = 0; i < bits.length; i++) {
            System.out.printf(String.valueOf(bits[i]));
        }
        System.out.println();
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(expect.equals(actual));
    }
}
