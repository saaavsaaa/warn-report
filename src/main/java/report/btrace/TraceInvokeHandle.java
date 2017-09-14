package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-14.
 */
@BTrace
public class TraceInvokeHandle {
    //java.lang.invoke
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "java.lang.invoke.MethodHandleImpl", method = "initStatics")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "java.lang.invoke.MethodHandleImpl", method = "initStatics", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("java.lang.invoke.MethodHandleImpl.initStatics execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "java.lang.invoke.MethodHandleImpl", method = "initStatics", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, byte[] base64Data){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class base64Data ============", Strings.str(base64Data)));
    }
    
    @OnMethod(clazz = "java.lang.invoke.MethodHandleImpl", method = "initStatics", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
