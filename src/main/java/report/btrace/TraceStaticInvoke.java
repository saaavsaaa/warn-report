package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-13.
 */
@BTrace
public class TraceStaticInvoke {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.sun.beans.finder.MethodFinder", method = "findStaticMethod")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.sun.beans.finder.MethodFinder", method = "findStaticMethod", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("com.caijinquan.p2p.common.bos.reapel.client.utils.decodeBase64 execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.sun.beans.finder.MethodFinder", method = "findStaticMethod", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, Class<?> var0, String var1){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class var0 ============", Strings.str(var0)));
        println(strcat("trace class var1 ============", var1));
    }
    
    @OnMethod(clazz = "com.sun.beans.finder.MethodFinder", method = "findStaticMethod", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
