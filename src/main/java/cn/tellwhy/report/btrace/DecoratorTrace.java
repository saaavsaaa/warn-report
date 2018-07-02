package cn.tellwhy.report.btrace;

import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;

/**
 * Created by aaa on 17-9-13.
 */
@BTrace
public class DecoratorTrace {
    
    // <editor-fold desc="over">
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name, @ProbeMethodName String method, AnyType rq, AnyType rs, AnyType chain){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
//        println(get(field("org.apache.shiro.web.servlet.ShiroHttpServletRequest","request"), rq));
//        println(get(field("javax.servlet.ServletRequestWrapper","request"), rq));
//        println(strcat("trace class rq ============", str(rq)));
//        println(strcat("trace class rs ============", str(rs)));
//        println(strcat("trace class FilterChain ============", str(chain)));
//        printFields(rq);
//        printFields(get(field("javax.servlet.ServletRequestWrapper","request"), rq));
        printFields(get(field("org.apache.catalina.connector.RequestFacade","request"), get(field("javax.servlet.ServletRequestWrapper","request"), rq)));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
        println(strcat("Thread Id: ", str(Threads.threadId(Threads.currentThread()))));
//        printFields(get(field("org.apache.catalina.connector.RequestFacade","request"), get(field("javax.servlet.ServletRequestWrapper","request"), rq)));
//        println(jstackAllStr());
    }
    // </editor-fold>
    
    // <editor-fold desc="useless">
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "doGet", location = @Location(Kind.RETURN))
    public static void doGetR(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("RETURN class name ============", name));
        println(strcat("RETURN class method ============", method));
        println(strcat("RETURN time==", str(Time.timestamp())));
    }
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "createImage", location = @Location(Kind.ERROR))
    public static void createImageR(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("RETURN class name ============", name));
        println(strcat("RETURN class method ============", method));
        println(strcat("RETURN time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "doPost", location = @Location(Kind.ERROR))
    public static void doPostR(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("RETURN class name ============", name));
        println(strcat("RETURN class method ============", method));
        println(strcat("RETURN time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.utils.excel.ExportExcel", method = "write", location = @Location(Kind.ERROR))
    public static void writeR(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("RETURN class name ============", name));
        println(strcat("RETURN class method ============", method));
        println(strcat("RETURN time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.sms.hxrt.HttpSender", method = "post", location = @Location(Kind.ERROR))
    public static void postR(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("RETURN class name ============", name));
        println(strcat("RETURN class method ============", method));
        println(strcat("RETURN time==", str(Time.timestamp())));
    }
    // </editor-fold>
    
    // <editor-fold desc="nothting">
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "doGet", location = @Location(Kind.ERROR))
    public static void doGet(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "createImage", location = @Location(Kind.ERROR))
    public static void createImage(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.servlet.ValidateCodeServlet", method = "doPost", location = @Location(Kind.ERROR))
    public static void doPost(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.utils.excel.ExportExcel", method = "write", location = @Location(Kind.ERROR))
    public static void write(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    
    @OnMethod(clazz = "com.thinkgem.jeesite.common.sms.hxrt.HttpSender", method = "post", location = @Location(Kind.ERROR))
    public static void post(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
        println(strcat("ERROR time==", str(Time.timestamp())));
    }
    // </editor-fold>
    
    // <editor-fold desc="throw">
    // store current exception in a thread local
    // variable (@TLS annotation). Note that we can't
    // store it in a global variable!
   /* @TLS static Throwable currentException;
    
    // introduce probe into every constructor of java.lang.Throwable
    // class and store "this" in the thread local variable.
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void onthrow(@Self Throwable self) {
        currentException = self;
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void onthrow1(@Self Throwable self, String s) {
        currentException = self;
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void onthrow1(@Self Throwable self, String s, Throwable cause) {
        currentException = self;
    }
    
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter")
    public static void onthrow2(@Self Throwable self, Throwable cause) {
        currentException = self;
    }
    
    // when any constructor of java.lang.Throwable returns
    // print the currentException's stack trace.
    @OnMethod(clazz = "com.opensymphony.sitemesh.webapp.SiteMeshFilter", method = "doFilter", location=@Location(Kind.RETURN))
    public static void onthrowreturn() {
        if (currentException != null) {
            Threads.jstack(currentException);
            println("=====================");
            currentException = null;
        }
    }*/
    //</editor-fold>
    
    // <editor-fold desc="not">
    /*private static long startDTime = 0;
    
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
        jstackAllStr();
    }*/
    // </editor-fold>
}
