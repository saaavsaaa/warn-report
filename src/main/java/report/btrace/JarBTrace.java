package report.btrace;

import com.sun.btrace.AnyType;
import com.sun.btrace.annotations.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.println;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-10-13.
 */
@BTrace
public class JarBTrace {
    @OnMethod(clazz = "java.util.jar.JarFile", method = "<init>", location = @Location(Kind.RETURN))
    public static void traceHttpServletRequestCopierExecuteInput(@ProbeClassName String name, @ProbeMethodName String method,
                                                                 File file, boolean verify, int mode){
        String input = str(file);
        println(input);
        if (matches("commons-codec-1.10.jar", input)) {
            println(strcat("jar the class name=>", name));
            println(strcat("jar the class method=>", method));
            println(strcat("jar class file=>", input));
            println(strcat("jar class verify=>", str(verify)));
            println(strcat("jar class mode=>", str(mode)));
            println(strcat("commons time==", str(Time.timestamp())));
        }
        
        if (matches("http-1.1.0.jar", input)) {
            println(strcat("jar the class name=>", name));
            println(strcat("jar the class method=>", method));
            println(strcat("jar class file=>", input));
            println(strcat("jar class verify=>", str(verify)));
            println(strcat("jar class mode=>", str(mode)));
            println(strcat("http time==", str(Time.timestamp())));
        }
    }
    
    
    //打印实例属性
    @OnMethod(clazz = "sun.misc.URLClassPath", method = "getLoader", location = @Location(value = Kind.ENTRY))
    public static void bufferMonitor(@Self Object self ){				// @Self 表示监控点实例
        println(self);
        println(get(field(classOf(self), "urls")));									//只能取值static变量
        println(get(field(classOf(self), "urls"),self));							//可以取值当前实例变量,static也可以取到
        Field moneyField = field("sun.misc.URLClassPath", "urls");	//知道class的名称也可以取值
        get(moneyField,self);
        get(field("sun.misc.URLClassPath", "urls"), self);
        Object montmp =get(field(getSuperclass(classOf(self)), "uRLClassPath"), self);							//获取父类变量的方法
        println(str(montmp));
        long userId = (Long)get(field(classOf(montmp),"urls"),montmp);							//获取superClass.Object.变量值
        println(userId);
    }
    
    //打印运行时lineNumber
    @OnMethod(clazz = "sun.misc.URLClassPath", location=@Location(value=Kind.LINE, line=-1))
    public static void online(@ProbeClassName String pcn, @ProbeMethodName String pmn, int line) {
        print(Strings.strcat(pcn, "."));						//className
        print(Strings.strcat(pmn, ":"));					//methodName
        println(line);												//lineNumber
        //结果为:com.gameplus.action.siteLobby.LobbyAction.bankPage:161
    }
    
    //打印传递的参数值
    @OnMethod(clazz="sun.misc.URLClassPath",method="/.*/")
    public static void anyRead(@ProbeClassName String pcn, @ProbeMethodName String pmn, AnyType[] args) {
        println(pcn);
        println(pmn);
        printArray(args);
    }
    //打印所有属性
    @OnMethod(clazz = "sun.misc.URLClassPath", method = "/.*/", location = @Location(value = Kind.RETURN))
    public static void bufferMonitor(@Self Object self,@Return Object command ,@Duration long time){
        printFields(self);
        Object montmp =get(field(getSuperclass(classOf(self)), "urls"), self);
        printFields(montmp);
    }

    //打印系统参数
    static {
        println("System Properties:");
        printProperties();
        println("VM Flags:");
        printVmArguments();
        println("OS Enviroment:");
        printEnv();
        exit(0);
    }
    //打印程序执行关系
    //LobbyAction中所有方法执行的执行顺序
    @OnMethod(clazz="sun.misc.URLClassPath", method="/.*/",
            location=@Location(value=Kind.CALL, clazz="/.*/", method="/.*/"))
    public static void n(@Self Object self, @ProbeClassName String pcm, @ProbeMethodName String pmn,
                         @TargetInstance Object instance, @TargetMethodOrField String method){ // all calls to the methods with signature "(String)"
        println(Strings.strcat("Context: ", Strings.strcat(pcm, Strings.strcat("#", pmn))));
        println(instance);														//被调用目标对象
        println(Strings.strcat("",method));								//被调用方法
    }
}
