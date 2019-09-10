package com.qcj.spring.demo.service.impl;

import com.qcj.spring.demo.service.IDemoService;
import com.qcj.spring.framework.annotation.Service;

/**
 * Created by qcj on 2019-09-09
 */
@Service
public class DemoService implements IDemoService {
    public String get(String name){
        return "My name is "+name;
    }
}
