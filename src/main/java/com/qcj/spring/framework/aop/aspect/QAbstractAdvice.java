package com.qcj.spring.framework.aop.aspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by qcj on 2019-09-19
 */
public abstract class QAbstractAdvice implements QAdvice{

    private Method aspectMethod;
    private Object target;

    public QAbstractAdvice(Method aspectMethod, Object target) {
        this.aspectMethod = aspectMethod;
        this.target = target;
    }

    protected Object invokeAdviceMethod(JoinPoint joinPoint, Object returnValue, Throwable tx) throws InvocationTargetException, IllegalAccessException {
        Class<?> [] paramTypes = this.aspectMethod.getParameterTypes();
        if(paramTypes.length == 0){
             this.aspectMethod.invoke(target);
        }else{
            Object[] args = new Object[paramTypes.length];
            for(int i = 0 ; i<paramTypes.length ;i++){
                if(paramTypes[i] == JoinPoint.class){
                    args[i] = joinPoint;
                }else if(paramTypes[i] == Throwable.class){
                    args[i] = tx;
                }else if(paramTypes[i] == Object.class){
                    args[i] = returnValue;
                }
            }
            return this.aspectMethod.invoke(target,args);
        }
        return returnValue;
    }
}
