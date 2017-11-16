package report.btrace;

import com.sun.btrace.annotations.*;

import java.io.File;
import java.util.Map;

import static com.sun.btrace.BTraceUtils.*;
import static com.sun.btrace.BTraceUtils.strcat;

/**
 * Created by aaa on 17-11-14.
 */
@BTrace
public class HttpUtilBTrace {
    @TLS
    private static long startTime = 0;
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.HttpClientUtil", method = "post")
    public static void startMethod(){
        startTime = timeMillis();
    }
    
    @OnMethod(clazz = "com.***.common.bos.reapel.client.utils.HttpClientUtil", method = "post", location = @Location(Kind.RETURN))
    public static void tracePost(@ProbeClassName String name, @ProbeMethodName String method,
                                 String url, Map<String, String> paramMap){
        println(strcat(url + " HttpClientUtil.post execute time : ", str(timeMillis()-startTime)));
        println(strcat("merchant_id : ", get(paramMap, "merchant_id")) + strcat(" ,data : ", get(paramMap, "data")) + strcat(" ,encryptkey : ", get(paramMap, "encryptkey")));
    }
}
