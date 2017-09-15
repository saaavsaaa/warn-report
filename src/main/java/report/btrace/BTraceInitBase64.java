package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-15.
 */
@BTrace
public class BTraceInitBase64 {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>")
    public static void startMethodHandl(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.RETURN))
    public static void endMethodHandl(){
        println(strcat("trace execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceMethodHandlExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.ERROR))
    public static void traceERRORMethodHandl(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
