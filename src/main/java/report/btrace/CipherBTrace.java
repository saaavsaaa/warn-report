package report.btrace;

import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

import java.io.InputStream;
import java.io.OutputStream;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;

/**
 * Created by aaa on 17-12-25.
 */
@BTrace
public class CipherBTrace {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.***.p2p.app.controller.filter.CipherFilter", method = "doFilter")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.***.p2p.app.controller.filter.CipherFilter", method = "doFilter", location = @Location(Kind.RETURN))
    public static void traceExecute(@ProbeClassName String name, @ProbeMethodName String method,
                                                                 AnyType servletRequest, AnyType servletResponse, AnyType chain){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
    }
    
    @OnMethod(clazz = "com.***.p2p.app.controller.filter.CipherFilter", method = "doFilter", location = @Location(Kind.ERROR))
    public static void traceErrorExecuteWithoutParas(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR without args class name ============", name));
        println(strcat("ERROR without args class method ============", method));
        println(strcat("ERROR without args time==", str(Time.timestamp())));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
    }
    
    @OnMethod(clazz = "com.***.p2p.app.controller.filter.CipherFilter", method = "doFilter", location = @Location(Kind.ERROR))
    public static void traceErrorExecute(@ProbeClassName String name,@ProbeMethodName String method,
                                         AnyType servletRequest, AnyType servletResponse, AnyType chain){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));

        Object request = get(field("org.apache.catalina.connector.RequestFacade","request"), servletRequest);
        Object coyoteRequest = get(field("org.apache.catalina.connector.Request","coyoteRequest"), request);
        Object decodedUri = get(field("org.apache.coyote.Request","decodedUriMB"), coyoteRequest);
        printFields(decodedUri);
//        Object url = get(field("org.apache.tomcat.util.buf.MessageBytes","strValue"), decodedUri);
//        print(url);
        Object uri = get(field("org.apache.coyote.Request","uriMB"), coyoteRequest);
        println(get(field("org.apache.tomcat.util.buf.MessageBytes","strValue"), uri));
    
        Object path = get(field("org.apache.catalina.connector.Request","requestDispatcherPath"), request);
        println(get(field("org.apache.tomcat.util.buf.MessageBytes","strValue"), path));
        
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
//        println(jstackStr());
    }
}
