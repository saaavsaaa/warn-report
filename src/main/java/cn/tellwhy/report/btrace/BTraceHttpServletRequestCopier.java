package cn.tellwhy.report.btrace;

import com.sun.btrace.annotations.*;

import java.io.InputStream;
import java.io.OutputStream;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-8.
 */
@BTrace
public class BTraceHttpServletRequestCopier {
    @TLS
    private static long startTime = 0;
    private static long contrast = 100;
    private static long count = 0;
    private static long current = 0;
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge")
//    @OnMethod(clazz = "com.***.common.controller.httpobject.HttpServletRequestCopier", method = "getCopy")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        if (timeMillis()-startTime > contrast) {
            println(strcat("copyLarge execute time==", str(timeMillis() - startTime)));
            println(strcat("copyLarge time==", str(Time.timestamp())));
            println("-------------------------------------------");
        }
        count++;
        if (count == current + 100) {
            current = count;
            println(strcat("conunt:::", str(count)));
        }
    }
    
    @OnLowMemory(pool = "pool", threshold = 75)
    public static void low(){
        println(strcat("OnLowMemory time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, @Return long result){
        if (timeMillis()-startTime > contrast) {
            println(strcat("traceHttpServletRequestCopierExecute the class name=>", name));
            println(strcat("traceHttpServletRequestCopierExecute the class method=>", method));
            println(strcat("traceHttpServletRequestCopierExecute result class method=>", str(result)));
        }
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteInput(@ProbeClassName String name, @ProbeMethodName String method,
                                                                 InputStream input, OutputStream output, byte[] buffer){
        if (timeMillis()-startTime > contrast) {
            println(strcat("traceHttpServletRequestCopierExecuteInput the class name=>", name));
            println(strcat("traceHttpServletRequestCopierExecuteInput the class method=>", method));
            println(strcat("traceHttpServletRequestCopierExecuteInput class input=>", str(input)));
            println(strcat("traceHttpServletRequestCopierExecuteInput class output=>", str(output)));
            println(strcat("traceHttpServletRequestCopierExecuteInput class buffer=>", str(buffer)));
        }
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-startTime > contrast) {
            println(strcat("ERROR class name=>", name));
            println(strcat("ERROR class method=>", method));
        }
    }
}
