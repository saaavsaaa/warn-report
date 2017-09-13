package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-13.
 */
@BTrace
public class ApacheBase64Trace {
    @TLS
    private static long startTime = 0;
    
    //    @OnMethod(clazz = "cn.caijingquan.p2p.app.filter.HttpServletRequestCopier", method = "getCopy")
    @OnMethod(clazz = "org.apache.commons.codec.binary.Base64", method = "decodeBase64")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "org.apache.commons.codec.binary.Base64", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("org.apache.commons.codec.binary.Base64.decodeBase64 execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "org.apache.commons.codec.binary.Base64", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, String base64String){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
    }
    
    @OnMethod(clazz = "org.apache.commons.codec.binary.Base64", method = "decodeBase64", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
    
    @OnError
    public static void eRRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
