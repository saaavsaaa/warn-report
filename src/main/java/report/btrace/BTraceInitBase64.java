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
    public static void startMethodHandle(){
        println("sssssssssssssssssssssssss");
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.RETURN))
    public static void endMethodHandle(){
        println(strcat("trace execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceMethodHandlExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "<init>", location = @Location(Kind.ERROR))
    public static void traceERRORMethodHandle(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64")
    public static void startBase64MethodHandle(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64")
    public static void preMethodHandle(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println("pppppppppppppppppppppppppppppppppppppppppppppp");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64")
    public static void prebMethodHandle(@ProbeClassName String name,@ProbeMethodName String method, byte[] base64Data){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class para ============", Strings.str(base64Data)));
        println("bbbbbbbbbbbbbbbbbbbbbbbbytebbbbbbbbbbbbbbbbbbbbbbbbytebbbbbbbbbbbbbbbbbbbbbbbbytebbbbbbbbbbbbbbbbbbbbbbbbyte[]");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64")
    public static void presMethodHandle(@ProbeClassName String name,@ProbeMethodName String method, String base64String){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
        println(strcat("trace class para ============", base64String));
        println("StringStringStringStringStringStringStringStringStringStringStringStringStringStringString");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void endBase64MethodHandl(){
        println(strcat("trace execute time============", str(timeMillis()-startTime)));
        println("-------------------------------------------");
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64", location = @Location(Kind.RETURN))
    public static void traceBase64MethodHandlExecute(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("trace class name ============", name));
        println(strcat("trace class method ============", method));
    }
    
    @OnMethod(clazz = "/.*.Base64/", method = "decodeBase64", location = @Location(Kind.ERROR))
    public static void traceBase64ERRORMethodHandle(@ProbeClassName String name,@ProbeMethodName String method){
        println(strcat("ERROR class name ============", name));
        println(strcat("ERROR class method ============", method));
    }
}
