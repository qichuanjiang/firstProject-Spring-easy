package com.qcj.spring.framework.context;


/**
 * Created by qcj on 2019-09-10
 */
public abstract class AbstractApplicationContext{
    //提供给子类重写的
   protected void onRefresh(){

   }
   protected abstract void refreshBeanFactory();

}
