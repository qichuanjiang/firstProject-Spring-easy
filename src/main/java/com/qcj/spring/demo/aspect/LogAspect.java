package com.qcj.spring.demo.aspect;

/**
 * Created by qcj on 2019-09-18
 */
public class LogAspect {
    //调用方法之前执行
    public void before(){
        System.out.println("我是切入的before方法");
    }
    //调用方法之后执行
    public void after(){
        System.out.println("我是切入的after方法");
    }

    public void afterThrow(){
        System.out.println("我是切入的afterThrow方法");
    }
}
