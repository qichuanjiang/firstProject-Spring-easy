package com.qcj.spring.framework.context;

import com.qcj.spring.demo.action.DemoAction;
import com.qcj.spring.framework.annotation.Autowired;
import com.qcj.spring.framework.annotation.Controller;
import com.qcj.spring.framework.annotation.Service;
import com.qcj.spring.framework.beans.BeanDefinition;
import com.qcj.spring.framework.beans.BeanPostProcessor;
import com.qcj.spring.framework.beans.BeanWrapper;
import com.qcj.spring.framework.context.support.BeanDefinitionReader;
import com.qcj.spring.framework.core.BeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by qcj on 2019-09-10
 */
public class ApplicationContext implements BeanFactory {

    private String[] configLocations;

    private BeanDefinitionReader beanDefinitionReader;

    //ioc容器，保存配置信息
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

    //用来保证bean单例模式
    private Map<String, Object> beanCacheMap = new ConcurrentHashMap<String, Object>();

    //用来存储所有的被代理过的对象
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    //
    public ApplicationContext(String... locations) {
        this.configLocations = locations;
        this.refresh();
    }

    public void refresh(){
        //定位
        this.beanDefinitionReader = new BeanDefinitionReader(configLocations);

        //加载
        List<String> beanDefinitions = beanDefinitionReader.loadBeanDefinitions();

        //注册
        doRegistry(beanDefinitions);

        //依赖注入
        doAutowired();

 /*       DemoAction demoAction = (DemoAction) getBean("demoAction");
        demoAction.query(null,null,"我叫祁川江");*/
    }

    private void doAutowired() {
        for(Map.Entry<String,BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()){
            String beanName = beanDefinitionEntry.getKey();
            if(!beanDefinitionEntry.getValue().isLazyInit()){
                getBean(beanName);
            }
        }
        /*for(Map.Entry<String,BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()){
            populateBean(beanWrapperEntry.getKey(),beanWrapperEntry.getValue().getWrapperInstance());
        }*/
    }

    private void populateBean(String beanName,Object instance){
        Class clazz = instance.getClass();
        if(!(clazz.isAnnotationPresent(Controller.class)||clazz.isAnnotationPresent(Service.class))){
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for(Field field : fields){
            if(!field.isAnnotationPresent(Autowired.class)){
                continue;
            }
            Autowired autowired = field.getAnnotation(Autowired.class);
            String autowiredBeanName = autowired.value().trim();
            if("".equals(autowiredBeanName)){
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                Object object;
                BeanWrapper beanWrapper = this.beanWrapperMap.get(autowiredBeanName);
                if(beanWrapper == null){
                    object = getBean(autowiredBeanName);
                }else{
                    object = beanWrapper.getWrapperInstance();
                }
                field.set(instance,object);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
    }
    //真正的将beanDefinition注册到ioc容器(beanDefinitionMap)中
    private void doRegistry(List<String> beanDefinitions) {
        try {
            for(String className : beanDefinitions){

                //className有三种情况
                //1、默认首字母小写 2、自定义类名 3、接口注入
                Class<?> beanClass = Class.forName(className);
                //如果是接口，不能直接实例化，需要实例化他的实现接口
                if(beanClass.isInterface()){
                    continue;
                }
                BeanDefinition beanDefinition = beanDefinitionReader.registerBean(className);

                if(beanDefinition != null){
                    beanDefinitionMap.put(beanDefinition.getFactoryBeanName(),beanDefinition);
                }
                Class<?>[] interfaces = beanClass.getInterfaces();
                for(Class<?> i : interfaces){
                    //多个实现类，只能覆盖（sprig会报错），可以通过自定义名称优化
                    beanDefinitionMap.put(i.getName(),beanDefinition);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 通过读取BeanDefinition中的信息
    // 然后通过反射机制，创建一个实例并且返回
    // spring的做法是。不会把最原始的对象返回出去，会用一个beanWrapper来进行一层包装
    // 装饰器模式： 1、保留原来的oop模式 2、我需要对它进行拓展，增强（为之后的aop打基础）
    public Object getBean(String beanName) {

        BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);

        String className = beanDefinition.getBeanClassName();

        try{

            BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
            Object instance = instantionBean(beanDefinition);
            if(null == instance){
                return null;
            }
            beanPostProcessor.postProcessBeforeInitialization(instance,beanName);
            BeanWrapper beanWrapper = new BeanWrapper(instance);
            beanWrapper.setBeanPostProcessor(beanPostProcessor);
            this.beanWrapperMap.put(beanName,beanWrapper);
            beanPostProcessor.postProcessAfterInitialization(instance,beanName);
            populateBean(beanName,instance);
            return this.beanWrapperMap.get(beanName).getWrapperInstance();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //根据beanDefinition返回实例
    private Object instantionBean(BeanDefinition beanDefinition){
        Object instance = null;
        String className = beanDefinition.getBeanClassName();
        try {
            if(this.beanCacheMap.containsKey(className)){
                instance = this.beanCacheMap.get(className);
            }else{
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.beanCacheMap.put(className,instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return instance;
    }


    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }
    public Properties getConfig(){
        return this.beanDefinitionReader.getConfig();
    }
}
