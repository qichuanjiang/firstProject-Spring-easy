package com.qcj.spring.demo.action;

import com.qcj.spring.demo.service.IDemoService;
import com.qcj.spring.framework.annotation.Autowired;
import com.qcj.spring.framework.annotation.Controller;
import com.qcj.spring.framework.annotation.ResquestMapping;

/**
 * Created by qcj on 2019-09-09
 */
@Controller
@ResquestMapping("/demo")
public class MyAction {
    @Autowired
    private IDemoService demoService;

    @ResquestMapping("/index.html")
    public void query(){

    }
}
