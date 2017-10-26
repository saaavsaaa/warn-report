package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-14.
 */
@BTrace
public class TraceAppRSA {
    @TLS
    private static long startTime = 0;
    private static long initStartTime = 0;
    private static long contrast = 100;
    
    @OnMethod(clazz = "com.caijinquan.p2p.common.util.cipher.AppRSA", method = "decryptBASE64")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.caijinquan.p2p.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("com.caijinquan.p2p.common.util.cipher.AppRSA.decodeBase64 execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.caijinquan.p2p.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, String key){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class base64Data ============", key));
    }
    
    @OnMethod(clazz = "com.caijinquan.p2p.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
    
    
    //com.caijinquan.p2p.app.controller.filter.httpobject
    //cn.caijingquan.p2p.app.filter
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>")
    public static void startMethodinit(){
        initStartTime = timeMillis();
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethodinit(){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("HttpServletRequestCopier.init execute time==", str(timeMillis() - initStartTime)));
            println("-------------------------------------------");
        }
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("the class name=>", name));
            println(strcat("the class method=>", method));
        }
    }
    
    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.ERROR))
    public static void traceERRORExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("ERROR class name=>", name));
            println(strcat("ERROR class method=>", method));
        }
    }
}
