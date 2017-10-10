package report.btrace;

import com.sun.btrace.annotations.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;

import static com.sun.btrace.BTraceUtils.*;

/**
 * Created by aaa on 17-9-8.
 */
@BTrace
public class BTraceHttpServletRequestCopier {
    @TLS
    private static long startTime = 0;
    private static long initStartTime = 0;
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge")
//    @OnMethod(clazz = "com.caijinquan.p2p.common.controller.httpobject.HttpServletRequestCopier", method = "getCopy")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        if (timeMillis()-startTime > 1000) {
            println(strcat("copyLarge execute time==", str(timeMillis() - startTime)));
            println(strcat("copyLarge time==", str(Time.timestamp())));
            println("-------------------------------------------");
        }
    }
    
    @OnLowMemory(pool = "pool", threshold = 75)
    public static void low(){
        println(strcat("OnLowMemory time==", str(Time.timestamp())));
    }
//    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
//    public static void traceExecute(@Self Object  copier){
//        Object reader = get(field("cn.caijingquan.p2p.app.filter.HttpServletRequestCopier","reader"), copier);
//        println(strcat("the class name=>", str(reader)));
//    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, @Return long result){
        if (timeMillis()-startTime > 1000) {
            println(strcat("traceHttpServletRequestCopierExecute the class name=>", name));
            println(strcat("traceHttpServletRequestCopierExecute the class method=>", method));
            println(strcat("traceHttpServletRequestCopierExecute result class method=>", str(result)));
        }
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteInput(@ProbeClassName String name, @ProbeMethodName String method,
                                                                 InputStream input, OutputStream output, byte[] buffer){
        if (timeMillis()-startTime > 1000) {
            println(strcat("traceHttpServletRequestCopierExecuteInput the class name=>", name));
            println(strcat("traceHttpServletRequestCopierExecuteInput the class method=>", method));
            println(strcat("traceHttpServletRequestCopierExecuteInput class input=>", str(input)));
            println(strcat("traceHttpServletRequestCopierExecuteInput class output=>", str(output)));
            println(strcat("traceHttpServletRequestCopierExecuteInput class buffer=>", str(buffer)));
        }
    }
    
    @OnMethod(clazz = "org.apache.commons.io.IOUtils", method = "copyLarge", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-startTime > 1000) {
            println(strcat("ERROR class name=>", name));
            println(strcat("ERROR class method=>", method));
        }
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>")
    public static void startMethodinit(){
        initStartTime = timeMillis();
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethodinit(){
        if (timeMillis()-initStartTime > 1000) {
            println(strcat("HttpServletRequestCopier.init execute time==", str(timeMillis() - initStartTime)));
            println("-------------------------------------------");
        }
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > 1000) {
            println(strcat("the class name=>", name));
            println(strcat("the class method=>", method));
        }
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.ERROR))
    public static void traceERRORExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > 1000) {
            println(strcat("ERROR class name=>", name));
            println(strcat("ERROR class method=>", method));
        }
    }
}
