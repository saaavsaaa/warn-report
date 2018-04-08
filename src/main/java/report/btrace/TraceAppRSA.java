package report.btrace;

import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;

import java.util.Vector;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-14.
 */
@BTrace
public class TraceAppRSA {
    /*@TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.RETURN))
    public static void endMethod(){
        println(strcat("execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.RETURN))
    public static void traceExecute(@ProbeClassName String name, @ProbeMethodName String method, String source, String publicKey, @Return String result){
        println(strcat("trace class source ============", str(source)));
        println(strcat("trace class source ============", str(publicKey)));
        println(strcat("trace class return ============", str(result)));
    }
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@Self Object self, @Duration long dur, Throwable err){
        println(strcat("ERROR throwable ============", str(err)));
        println(strcat("ERROR throwable ============", str(self)));
        println(strcat("ERROR throwable ============", str(dur)));
    }
    
    */
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.ERROR))
    public static void traceERRORExecuteW(@ProbeClassName String name, @ProbeMethodName String method, String source, String publicKey, @TargetInstance Throwable cause){
        println(strcat("ERROR name ============", name));
        println(strcat("ERROR name ============", method));
        println(strcat("ERROR source ============", source));
        println(strcat("ERROR publicKey ============", publicKey));
    }
    
    @OnMethod(clazz = "com.xxx.xxx.app.controller.filter.Test", method = "encryptA", location = @Location(Kind.ERROR))
    public static void traceERRORExecuteT(@Self Object self, @Duration long dur, String source, String publicKey, @TargetInstance Throwable cause){
//        println(strcat("ERROR with ============", name));
//        println(strcat("ERROR with ============", method));
        println(strcat("ERROR with source ============", source));
        println(strcat("ERROR with publicKey ============", publicKey));
        println(strcat("ERROR with throwable ============", str(cause)));
    }
    
    /*private static long initStartTime = 0;
    private static long contrast = 100;
    
    @OnMethod(clazz = "com.***.common.util.cipher.AppRSA", method = "decryptBASE64")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.***.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethod(){
        println(strcat("com.***.common.util.cipher.AppRSA.decodeBase64 execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "com.***.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecute(@ProbeClassName String name,@ProbeMethodName String method, String key){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class base64Data ============", key));
    }
    
    @OnMethod(clazz = "com.***.common.util.cipher.AppRSA", method = "decryptBASE64", location = @Location(Kind.ERROR))
    public static void traceERRORExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
    
    
    //com.***.app.controller.filter.httpobject
    //cn.***.app.filter
    @OnMethod(clazz = "cn.***.app.filter.HttpServletRequestCopier", method = "<init>")
    public static void startMethodinit(){
        initStartTime = timeMillis();
    }
    
    @OnMethod(clazz = "cn.***.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void endHttpServletRequestCopierMethodinit(){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("HttpServletRequestCopier.init execute time==", str(timeMillis() - initStartTime)));
            println("-------------------------------------------");
        }
    }
    
    @OnMethod(clazz = "cn.***.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("the class name=>", name));
            println(strcat("the class method=>", method));
        }
    }
    
    @OnMethod(clazz = "cn.***.app.filter.HttpServletRequestCopier", method = "<init>", location = @Location(Kind.ERROR))
    public static void traceERRORExecuteinit(@ProbeClassName String name,@ProbeMethodName String method){
        if (timeMillis()-initStartTime > contrast) {
            println(strcat("ERROR class name=>", name));
            println(strcat("ERROR class method=>", method));
        }
    }*/
}
