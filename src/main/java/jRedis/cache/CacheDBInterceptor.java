package jRedis.cache;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationInvoker;

import java.lang.reflect.Method;

/**
 * Created by aaa on 17-7-26.
 */
public class CacheDBInterceptor extends CacheInterceptor {
    
    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        return super.invoke(invocation);
    }
    
    @Override
    protected Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        try {
            return super.execute(invoker, target, method, args);
        }catch (Exception e){
            return invokeOperation(invoker);
        }
    }
}
