package com.qcj.spring.framework.webmvc;

import java.util.Map;

/**
 * Created by qcj on 2019-09-13
 */
public class QModelAndView {

    private String viewName;
    private Map<String,?> model;
    public QModelAndView(String viewName, Map<String, ?> model) {
        this.viewName = viewName;
        this.model = model;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Map<String, ?> getModel() {
        return model;
    }

    public void setModel(Map<String, ?> model) {
        this.model = model;
    }
}
