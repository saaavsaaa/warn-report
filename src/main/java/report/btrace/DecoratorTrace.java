package report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;

/**
 * Created by aaa on 17-9-13.
 */
@BTrace
public class DecoratorTrace {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("com.opensymphony.sitemesh.webapp.SiteMeshFilter execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, Object rq, Object rs, Object chain){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class rq ============", str(rq)));
        println(strcat("trace class rs ============", str(rs)));
        println(strcat("trace class FilterChain ============", str(chain)));
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    
    private static long startDTime = 0;
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.decorator.NoDecorator", method = "render")
    public static void startDMethod(){
        startDTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.decorator.NoDecorator", method = "render", location = @Location(Kind.RETURN))
    public static void endDMethod(){
        println(strcat("com.opensymphony.sitemesh.webapp.decorator.NoDecorator execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.decorator.NoDecorator", method = "render", location = @Location(Kind.RETURN))
    public static void traceDExecute(@ProbeClassName String name,@ProbeMethodName String method, Object content, Object request, Object response, Object servletContext, Object webAppContext){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class content ============", str(content)));
        println(strcat("trace class request ============", str(request)));
        println(strcat("trace class servletContext ============", str(response)));
        println(strcat("trace class servletContext ============", str(servletContext)));
        println(strcat("trace class webAppContext ============", str(webAppContext)));
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.ERROR))
    public static void traceERRORDExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
}
