package com.qcj.spring.framework.context.support;

import com.qcj.spring.framework.beans.BeanDefinition;
import com.qcj.spring.framework.context.AbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by qcj on 2019-09-18
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {


    //ioc容器，保存配置信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    protected void refreshBeanFactory() {

    }

    @Override
    protected void onRefresh() {
        super.onRefresh();
    }
}
