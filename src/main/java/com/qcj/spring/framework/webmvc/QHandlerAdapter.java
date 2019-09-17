package com.qcj.spring.framework.webmvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by qcj on 2019-09-13
 */
public class QHandlerAdapter  {

    private Map<String,Integer> paramMap;

    public QHandlerAdapter(Map<String, Integer> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     *
     * @param req
     * @param resp
     * @param handler 包含了controller，method，url信息
     * @return
     */
    public QModelAndView handle(HttpServletRequest req, HttpServletResponse resp, QHandlerMapping handler) throws InvocationTargetException, IllegalAccessException {
        //根据用户请求的参数信息，跟method的参数信息进行动态匹配
        //resp 传进来的目的只有一个，就是将值赋给方法参数
        //只有当用户传来的ModelAndView为空时，才会new一个默认的
        //1、准备好这个方法的形参列表
        //方法重载：形参的决定因素：参数的个数，参数的类型，参数顺序，方法的名字
        Class<?>[] paramTypes = handler.getMethod().getParameterTypes();
        //2、拿到自定义命名参数所在位置
        Map<String,String[]> paramMap = req.getParameterMap();
        //3、构造实参列表
        Object[] paramValues = new Object[paramTypes.length];
        for(Map.Entry<String,String[]> param : paramMap.entrySet()){
            String value = Arrays.toString(param.getValue()).replaceAll("[\\[\\]]","").replaceAll("\\s","");
            if(!this.paramMap.containsKey(param.getKey())){continue;}
            int index = this.paramMap.get(param.getKey());
            paramValues[index] = caseStringValue(value,paramTypes[index]);

            if(this.paramMap.containsKey(HttpServletRequest.class.getName())) {
                int reqIndex = this.paramMap.get(HttpServletRequest.class.getName());
                paramValues[reqIndex] = req;
            }
            if(this.paramMap.containsKey(HttpServletResponse.class.getName())) {
                int respIndex = this.paramMap.get(HttpServletResponse.class.getName());
                paramValues[respIndex] = resp;
            }

            Object result = handler.getMethod().invoke(handler.getController(),paramValues);
            if(result == null){return null;}
            boolean isMv = handler.getMethod().getReturnType() == QModelAndView.class;
            if(isMv){
                return (QModelAndView)result;
            }else{
                return  null;
            }
        }

        //4、从handler中取出controller,method,然后利用反射机制进行调用
        return null;
    }

    private Object caseStringValue(String value,Class<?> clazz){
        if(clazz == String.class){
            return value;
        }else if(clazz == Integer.class){
            String[] strs = value.split(",");
            if(strs.length>1){
                return Integer.valueOf(strs[0]);
            }
            return Integer.valueOf(value);
        }else if(clazz == int.class){
            String[] strs = value.split(",");
            if(strs.length>1){
                return Integer.valueOf(strs[0]);
            }
            return Integer.valueOf(value);
        }else{
            return null;
        }
    }
}
