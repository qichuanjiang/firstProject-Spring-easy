package com.qcj.spring.demo.action;

import com.qcj.spring.demo.service.IDemoService;
import com.qcj.spring.framework.annotation.Autowired;
import com.qcj.spring.framework.annotation.Controller;
import com.qcj.spring.framework.annotation.RequestParam;
import com.qcj.spring.framework.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by qcj on 2019-09-09
 */
@Controller
@RequestMapping("/demo")
public class DemoAction {
    @Autowired
    private IDemoService demoService;

    @RequestMapping("/query.json")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("name") String name){

        System.out.println(demoService.get(name));
       /* try{
            response.getWriter().println(name);
        }catch (Exception e){
            e.printStackTrace();
        }*/
    }
    @RequestMapping("/edit.json")
    public void query(HttpServletRequest request, HttpServletResponse response, @RequestParam("id") Integer id){

    }
}
