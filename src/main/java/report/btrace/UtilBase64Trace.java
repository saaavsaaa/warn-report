package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-13.
 */
@BTrace
public class UtilBase64Trace {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.Base64", method = "decodeBase64")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.Base64", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void endMethod(){
        println(strcat("com.***.common.bos.reapel.client.utils.decodeBase64 execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.Base64", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void traceExecute(@ProbeClassName String name,@ProbeMethodName String method, byte[] base64Data){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class base64Data ============", Strings.str(base64Data)));
    }
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.Base64", method = "decodeBase64", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
