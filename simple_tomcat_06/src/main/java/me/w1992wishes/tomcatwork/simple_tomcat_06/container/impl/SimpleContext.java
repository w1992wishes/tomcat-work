package me.w1992wishes.tomcatwork.simple_tomcat_06.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_06.container.ContainerBase;
import me.w1992wishes.tomcatwork.simple_tomcat_06.container.Context;

import java.util.HashMap;

/**
 * servlet容器，可以包含多个servlet，对应可以包含多个Wrapper子容器
 * Created by wanqinfeng on 2017/2/22.
 */
public class SimpleContext extends ContainerBase implements Context {

    //构造方法中初始化context的基础阀
    public SimpleContext() {
        setBasic(new SimpleContextValve());
    }

    protected HashMap servletMappings = new HashMap();

    @Override
    public void addServletMapping(String pattern, String name) {
        synchronized (servletMappings) {
            servletMappings.put(pattern, name);
        }
    }

    @Override
    public String findServletMapping(String pattern) {
        synchronized (servletMappings) {
            return ((String) servletMappings.get(pattern));
        }
    }

    @Override
    public String getInfo() {
        return "Simple Context";
    }
}
