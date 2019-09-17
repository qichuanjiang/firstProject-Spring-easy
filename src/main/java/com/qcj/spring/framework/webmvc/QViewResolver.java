package com.qcj.spring.framework.webmvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qcj on 2019-09-13
 * 目的：
 * 1、将一个静态文件变为一个动态文件
 * 2、根据用户传送参数不同，产生不同的结果
 */
public class QViewResolver {

    private String viewName;
    private File templateFile;

    public QViewResolver(String viewName,File templateFile){
        this.viewName = viewName;
        this.templateFile = templateFile;
    }

    public String viewResolver(QModelAndView mv) throws IOException {

        StringBuffer sb = new StringBuffer();
        RandomAccessFile ra = new RandomAccessFile(this.templateFile,"r");

        String line;
        while(null != (line = ra.readLine())){
            line = new String(getBytes(line.toCharArray()));
            Matcher matcher = matcherStr(line);
            while (matcher.find()){
                for(int i = 1;i <= matcher.groupCount();i++){
                    //获取¥{}中间的值
                    String paramName = matcher.group(i);
                    Object paramValue = mv.getModel().get(paramName);
                    if(null==paramValue){continue; }
                    line = line.replaceAll("¥\\{"+paramName+"}",paramValue.toString());
                }
            }
            sb.append(line);
        }
        return sb.toString();
    }
    // 将 char[] 强转为 byte[]
    private static byte[] getBytes(char[] chars) {
        byte [] result = new byte[chars.length];
        for(int i=0;i<chars.length;i++){
            result[i] = (byte) chars[i];
        }
        return result;
    }

    private Matcher matcherStr(String str){
        Pattern pattern = Pattern.compile("¥\\{(.+?)}",Pattern.CASE_INSENSITIVE);
        return pattern.matcher(str);
    }
    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public File getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(File templateFile) {
        this.templateFile = templateFile;
    }
}
