package report.btrace;

import com.sun.btrace.annotations.*;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

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
}
