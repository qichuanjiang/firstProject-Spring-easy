package com.qcj.spring.demo.action;

import com.qcj.spring.demo.service.IDemoService;
import com.qcj.spring.framework.annotation.Autowired;
import com.qcj.spring.framework.annotation.Controller;
import com.qcj.spring.framework.annotation.RequestParam;
import com.qcj.spring.framework.annotation.RequestMapping;
import com.qcj.spring.framework.webmvc.QModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by qcj on 2019-09-09
 */
@Controller
@RequestMapping("/web")
public class MyAction {
    @Autowired
    private IDemoService demoService;

    @RequestMapping("/query.json")
    public QModelAndView query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(demoService.get(name));
        return out(response,result);
    }
    @RequestMapping("/add*.json")
    public QModelAndView add(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") Integer name){
        System.out.println(String.valueOf(name));
        return out(response,String.valueOf(name));
    }
    @RequestMapping("/edit.json")
    public QModelAndView edit(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(demoService.get(name));
        return out(response,result);
    }
    @RequestMapping("/remove.json")
    public QModelAndView remove(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
        String result = demoService.get(name);
        System.out.println(demoService.get(name));
        return out(response,result);
    }

    @RequestMapping("/first.html")
    public QModelAndView first(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){
        Map<String,Object> model = new HashMap<String, Object>();
        model.put("name",name);
        return new QModelAndView("first.html",model);
    }


    private QModelAndView out(HttpServletResponse response, String result) {
        try {
            response.getWriter().write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
