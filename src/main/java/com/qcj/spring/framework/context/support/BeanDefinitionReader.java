package com.qcj.spring.framework.context.support;

import com.qcj.spring.framework.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by qcj on 2019-09-10
 * 对配置文件查找，读取，解析
 */
public class BeanDefinitionReader {

    private Properties config = new Properties();

    private List<String> registryBeanClasses = new ArrayList<String>();

    private final String SCAN_PACKAGE = "scanPackage";

    public BeanDefinitionReader(String... locations) {
        //应该循环locations，暂时取第一个
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:",""));

        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        doScanner(config.getProperty(SCAN_PACKAGE));

    }

    //每注册一个className就返回一个beanDefinition
    //只是为了对配置信息进行一层包装
    public BeanDefinition registerBean(String className){
        if(this.registryBeanClasses.contains(className)){
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf(".")+1)));
            return beanDefinition;
        }
        return null;
    }

    public List<String> loadBeanDefinitions(){
        return registryBeanClasses;
    }
    public Properties getConfig(){
        return config;
    }


    //递归扫描所有相关联的class，并且保存到一个list中
    private void doScanner(String packageName) {
        URL url = this.getClass().getClassLoader().getResource("/"+packageName.replaceAll("\\.","/"));
        File classDir = null;
        if (url != null) {
            classDir = new File(url.getFile());
        }
        if (classDir != null) {
            File[] files = classDir.listFiles();
            if(files != null) {
                for (File file : files) {
                    if (file != null) {
                        if (file.isDirectory()) {
                            doScanner(packageName + "." + file.getName());
                        } else {
                            registryBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
                        }
                    }
                }
            }
        }
    }



    private String lowerFirstCase(String str){
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
