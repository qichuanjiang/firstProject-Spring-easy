package com.qcj.spring.framework.webmvc.servlet;


import com.qcj.spring.demo.action.DemoAction;
import com.qcj.spring.framework.annotation.*;
import com.qcj.spring.framework.context.ApplicationContext;
import com.qcj.spring.framework.webmvc.QHandlerAdapter;
import com.qcj.spring.framework.webmvc.QHandlerMapping;
import com.qcj.spring.framework.webmvc.QModelAndView;
import com.qcj.spring.framework.webmvc.QViewResolver;
import org.omg.PortableInterceptor.INACTIVE;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qcj on 2019-09-09
 */
public class DispatchServlet extends HttpServlet {

    private Properties contextConfig = new Properties();

    private Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    private List<String> classNames = new ArrayList<String>();

    private final String LOCATION = "contextConfigLocation";

    //最核心的设计
    private List<QHandlerMapping> handlerMappings = new ArrayList<QHandlerMapping>();
    private List<QViewResolver> viewResolvers = new ArrayList<QViewResolver>();
    private Map<QHandlerMapping,QHandlerAdapter> handlerAdapterMap = new HashMap<QHandlerMapping, QHandlerAdapter>();

    @Override
    public void init(ServletConfig config) throws ServletException {
        //相当于初始化IOC容器
        ApplicationContext context = new ApplicationContext(config.getInitParameter(LOCATION));

        initStrategies(context);
    }

    private void initStrategies(ApplicationContext context) {
        
        //九大组件初始化
        initMultipartResolver(context);//文件上传解析
        initLocaleResolver(context);//本地化解析
        initThemeResolver(context);//主题解析
        //自己实现
        initHandlerMappings(context);//请求映射解析
        //自己实现
        initHandlerAdapters(context);//多类型参数动态赋值
        initHandlerExceptionResolvers(context);//异常解析
        initRequestToViewNameTranslator(context);//请求到视图名解析
        //自己实现，动态模版解析
        initViewResolvers(context);//视图解析
        initFlashMapManager(context);//flash映射管理器
    }

    private void initFlashMapManager(ApplicationContext context) {}

    private void initViewResolvers(ApplicationContext context) {
        String templateRoot = context.getConfig().getProperty("templateRoot");
        String templateRootPath = this.getClass().getClassLoader().getResource(templateRoot).getFile();

        File fileDir = new File(templateRootPath);
        File[] files = fileDir.listFiles();
        if(files != null) {
            for (File file : files) {
                this.viewResolvers.add(new QViewResolver(file.getName(), file));
            }
        }
    }

    private void initRequestToViewNameTranslator(ApplicationContext context) {}

    private void initHandlerExceptionResolvers(ApplicationContext context) {}

    private void initHandlerAdapters(ApplicationContext context) {

        for(QHandlerMapping handlerMapping : this.handlerMappings){
            Map<String, Integer> paramMap = new HashMap<String, Integer>();
            Annotation[][] pa = handlerMapping.getMethod().getParameterAnnotations();
            for(int i = 0;i<pa.length;i++){
                for(Annotation annotation : pa[i]){
                    if(annotation instanceof RequestParam){
                        String paramName =((RequestParam) annotation).value();
                        if(!"".equals(paramName.trim())){
                            paramMap.put(paramName,i);
                        }
                    }
                }
            }
            Class<?>[] paramTypes = handlerMapping.getMethod().getParameterTypes();
            for(int i = 0;i<paramTypes.length;i++){
                Class<?> type = paramTypes[i];
                if(type == HttpServletRequest.class || type == HttpServletResponse.class){
                    paramMap.put(type.getName(),i);
                }
            }
            this.handlerAdapterMap.put(handlerMapping,new QHandlerAdapter(paramMap));
        }
    }

    //将controller中配置的requstMapping和Method一一对应
    private void initHandlerMappings(ApplicationContext context) {
        //从容器中取到所有的实例
        String[] beanNames = context.getBeanDefinitionNames();
        for(String beanName : beanNames){
            Object controller = context.getBean(beanName);
            Class<?> clazz = controller.getClass();
            if(!clazz.isAnnotationPresent(Controller.class)){continue;}
            String baseUrl = "";
            if(clazz.isAnnotationPresent(RequestMapping.class)){
                RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
                baseUrl = requestMapping.value();
            }

            Method[] methods = clazz.getMethods();
            for(Method method : methods){
                if(!method.isAnnotationPresent(RequestMapping.class)){continue;}
                RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                String regex = (baseUrl+requestMapping.value().replaceAll("\\*",".*").replaceAll("/+","/"));
                Pattern pattern = Pattern.compile(regex);
                this.handlerMappings.add(new QHandlerMapping(pattern,controller,method));
                System.out.println("Mapping : "+regex+" , "+method);
            }

        }

    }

    private void initThemeResolver(ApplicationContext context) {}

    private void initLocaleResolver(ApplicationContext context) {}

    private void initMultipartResolver(ApplicationContext context) {}


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req,resp);
        } catch (Exception e) {
            try {
                resp.getWriter().write("500 Exception,Details:\r\n"+e.toString()+"\r\n"+Arrays.toString(e.getStackTrace())
                        .replaceAll("\\[|\\]","")
                        .replaceAll("\\s","\r\n")+" @QcjMvc");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
            QHandlerMapping handler = getHandler(req);
            if(handler == null){
                resp.getWriter().write("404 Not Found \r\n @QcjMvc");
                return;
            }


            QHandlerAdapter ha = getHandlerAdapter(handler);


            QModelAndView mv = ha.handle(req,resp,handler);


            processDispatchResult(resp,mv);
    }

    private void processDispatchResult(HttpServletResponse resp, QModelAndView mv) throws IOException {
        //调用viewResolver的resolveViewName方法
        if(null == mv){
            return;
        }
        if(this.viewResolvers.isEmpty()){return;}

        for (QViewResolver viewResolver : viewResolvers) {
            if(!mv.getViewName().equals(viewResolver.getViewName())){
                continue;
            }
            String out = viewResolver.viewResolver(mv);
            if(out!=null){
                resp.setContentType("text/html;charset=utf-8");
                resp.getWriter().write(out);
            }
        }


    }

    private QHandlerAdapter getHandlerAdapter(QHandlerMapping handler) {
        if(this.handlerAdapterMap==null){
            return null;
        }
        return this.handlerAdapterMap.get(handler);
    }

    private QHandlerMapping getHandler(HttpServletRequest req) {
        if(this.handlerMappings.isEmpty()){
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath,"").replaceAll("/+","/");
        for(QHandlerMapping handlerMapping : handlerMappings){
            Matcher matcher = handlerMapping.getPattern().matcher(url);
            if(!matcher.matches()){
                continue;
            }
            return handlerMapping;
        }
        return  null;
    }

}
