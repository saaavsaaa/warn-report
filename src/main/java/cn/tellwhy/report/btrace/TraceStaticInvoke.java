package cn.tellwhy.report.btrace;

import com.sun.btrace.annotations.*;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-9-13.
 *
 * http://blog.csdn.net/caipeichao2/article/details/42109429
 常用注解
 名称	                                          作用域	           作用
 @BTrace	                                       类	          声明跟踪脚本
 @OnMethod(clazz,method,location)	              方法	         当指定方法被调用时
 @OnMethod(method="<init>")	                      方法	        当构造函数被调用时
 @OnMethod(clazz="/java\\.io\\..*Input/"))	      方法	       方法名称正则匹配
 @Location(kind)	                           @OnMethod	指定监控方法调用前还是调用后
 @Location(value=Kind.NEWARRAY, clazz="char")  @OnMethod	监控新增数组
 @Self	                                       参数	         表示被监控的对象
 @ProbeMethodName	                           参数	        被监控的方法名称
 @ProbeClassName	                           参数	     被监控的类名
 @OnTimer(interval)                          方法     	定时调用某个方法
 @OnLowMemory(pool,threshold)	             方法    	当内存不足时
 @OnExit	                                  方法	          当程序退出时
 @OnProbe(namespace="java.net.socket",name="bind")	方法	监控socket中的bind方法
 常用方法
 方法	        作用
 println	在本地控制台输出一行
 print	在本地控制台输出
 printArray	在本地控制台输出数组
 jstack	打印远程方法的调用调用栈
 jstackAll	输出所有线程的调用栈
 exit	退出跟踪脚本
 Strings.strcat	连接字符串
 Reflactive.name	获取类名
 Threads.name	线程名
 Threads.currentThread	当前线程
 deadlocks	打出死锁线程
 sizeof	获取对象的大小，比如List对象就返回List.size()
 Sys.Env.property	获取系统变量

 *
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
        println(strcat(" execute time============", str(timeMillis()-startTime)));
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
