package com.qcj.spring.framework.aop.support;

import com.qcj.spring.framework.aop.aspect.QAfterAdvice;
import com.qcj.spring.framework.aop.aspect.QAfterThrowAdvice;
import com.qcj.spring.framework.aop.aspect.QBeforeAdvice;
import com.qcj.spring.framework.aop.config.QAopConfig;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qcj on 2019-09-18
 */
public class QAdvisedSupport {

    private Class<?> targetClass;

    private Object target;

    private QAopConfig config;

    private Pattern pointCutPattern;

    private Map<Method,List<Object>> methodCache;

    public QAdvisedSupport(QAopConfig config) {
        this.config = config;
    }

    public Class<?> getTargetClass(){
        return this.targetClass;
    }

    public Object getTarget(){
        return this.target;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws NoSuchMethodException {
        List<Object> cached = methodCache.get(method);
        if(cached == null){
            Method m = targetClass.getMethod(method.getName(),method.getParameterTypes());
            cached = new ArrayList<Object>();
            cached.add(m);
            this.methodCache.put(m,cached);
        }
        return cached;
    }

    public void setTargetClass(Class<?> targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    private void parse() {
        String pointCut = config.getPointCut()
                .replaceAll("\\.","\\\\.")
                .replaceAll("\\\\.\\*",".*")
                .replaceAll("\\(","\\\\(")
                .replaceAll("\\)","\\\\)");
        String pointCutForClassRegex = pointCut.substring(0,pointCut.lastIndexOf("\\("));
        pointCutPattern = Pattern.compile("class "+pointCutForClassRegex.substring(pointCutForClassRegex.lastIndexOf(" ")+1));
        Pattern pattern = Pattern.compile(pointCut);

        try {
            methodCache = new HashMap<Method, List<Object>>();
            Class aspectClass = Class.forName(this.config.getAspectClass());
            Map<String,Method> aspectMethods = new HashMap<String, Method>();
            for(Method m : aspectClass.getMethods()){
                aspectMethods.put(m.getName(),m);
            }
            for(Method method : this.getTargetClass().getMethods()){
                String methodString = method.toString();
                if(methodString.contains("throws")){
                    methodString = methodString.substring(0,methodString.lastIndexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodString);
                if(matcher.matches()){
                    //如果符合切面原则，则包装成QMethodIntercepter;
                    List<Object> chain = new ArrayList<Object>();
                    if(StringUtils.isNotBlank(config.getAspectBefore())){
                        QBeforeAdvice beforeAdvice = new QBeforeAdvice(aspectMethods.get(config.getAspectBefore()),aspectClass.newInstance());
                        chain.add(beforeAdvice);
                    }
                    if(StringUtils.isNotBlank(config.getAspectAfter())){
                        QAfterAdvice afterAdvice = new QAfterAdvice(aspectMethods.get(config.getAspectAfter()),aspectClass.newInstance());
                        chain.add(afterAdvice);
                    }
                    if(StringUtils.isNotBlank(config.getAspectAfterThrow())){
                        QAfterThrowAdvice afterThrowAdvice = new QAfterThrowAdvice(aspectMethods.get(config.getAspectAfterThrow()),aspectClass.newInstance());
                        afterThrowAdvice.setThrowName(config.getAspectAfterThrowName());
                        chain.add(afterThrowAdvice);
                    }
                    methodCache.put(method,chain);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public boolean pointCutMatch() {
        String str = this.targetClass.toString();
        boolean b = pointCutPattern.matcher(this.targetClass.toString()).matches();
        return b;
    }
}
