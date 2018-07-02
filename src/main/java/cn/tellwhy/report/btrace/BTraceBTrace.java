package cn.tellwhy.report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;

/**
 * Created by aaa on 17-12-27.
 */
@BTrace
public class BTraceBTrace {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.sun.btrace.runtime.instr.MethodInstrumentor", method = "constArg")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.sun.btrace.runtime.instr.MethodInstrumentor", method = "constArg", location = @Location(Kind.RETURN))
    public static void traceExecute(@ProbeClassName String name, @ProbeMethodName String method, int index, Object val){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class index ============", str(index)));
        println(strcat("trace class val ============", str(val)));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
    }
    
    @OnMethod(clazz = "com.sun.btrace.runtime.Instrumentor", method = "instrumentorFor", location = @Location(Kind.RETURN))
    public static void traceBExecute(@ProbeClassName String name, @ProbeMethodName String method){
        println(strcat("btrace class name ============", name));
        println(strcat("btrace class method ============", method));
        println(strcat("btrace Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
    }
}
