package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;

/**
 * Created by aaa on 17-9-11.
 */
@BTrace
public class TraceHelloWorld {
    
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "report.btrace.HelloWorld", method = "execute")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "report.btrace.HelloWorld", method = "execute", location = @Location(Kind.RETURN))
    public static void endMethod(){
        println(strcat("the class method execute time=>", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "report.btrace.HelloWorld", method = "execute", location = @Location(Kind.RETURN))
    public static void traceExecute(@ProbeClassName String name,@ProbeMethodName String method,int sleepTime){
        println(strcat("the class name=>", name));
        println(strcat("the class method=>", method));
        println(strcat("the class method params=>", str(sleepTime)));
    }
}
