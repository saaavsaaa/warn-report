package cn.tellwhy.run;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aaa on 17-12-26.
 */
public class ExecOSOrder {
    
    private static String abstractOrderResult(String key, String pattern, String result){
        if (!result.contains(key)){
            return "";
        }
        Stack<String> ns = new Stack<>();
        Pattern p = Pattern.compile(pattern);
        Matcher er = p.matcher(result);
        while(er.find()) {
            ns.push(er.group());
//            System.out.println(er.group());
        }
        return ns.pop();
    }
    
    public static void main(String[] args) {
        try {
//            runProcess("pwd");
//            runProcess("javac -cp src Test.java");
            System.out.println(runProcess("jps", " AppApplication", "\\d+"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String runProcess(String order, String key, String pattern) throws IOException {
        Runtime run = Runtime.getRuntime();
        Process process = run.exec(order);
        return checkResult(process, key, pattern);
    }
    
    private static String checkResult(Process process, String key, String pattern) throws IOException {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = in.readLine()) != null) {
            String tid = abstractOrderResult(key, pattern, line);
            if (tid.isEmpty()){
                continue;
            }
            return tid;
        }
        return "";
    }
    
    private static void printLines(String cmd, InputStream ins) throws Exception {
        String line;
        BufferedReader in = new BufferedReader(new InputStreamReader(ins));
        while ((line = in.readLine()) != null) {
            System.out.println(cmd + " " + line);
        }
    }
    
    private static void runProcess(String command) throws Exception {
        Process pro = Runtime.getRuntime().exec(command);
        printLines(command + " stdout:", pro.getInputStream());
        printLines(command + " stderr:", pro.getErrorStream());
        pro.waitFor();
        System.out.println(command + " exitValue() " + pro.exitValue());
    }
}
