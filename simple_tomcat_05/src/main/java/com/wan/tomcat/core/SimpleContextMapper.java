package com.wan.tomcat.core;

import org.apache.catalina.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 映射器组件，帮助servlet容器，选择一个子容器来处理某个指定的请求
 * Created by wanqinfeng on 2017/2/22.
 */
public class SimpleContextMapper implements Mapper{

    private SimpleContext context = null;
    private String protocol;

    @Override
    public Container getContainer() {
        return context;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public Container map(Request request, boolean update) {
        // Identify the context-relative URI to be mapped
        String contextPath = ((HttpServletRequest)request.getRequest()).getContextPath();
        String requestURI = ((HttpServletRequest)request.getRequest()).getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length());
        // Apply the standard request URI mapping rules from the specification
        Wrapper wrapper = null;
        String servletPath = relativeURI;
        String pathInfo = null;
        String name = context.findServletMapping(relativeURI);
        if (name!=null)
            wrapper = (Wrapper)context.findChild(name);
        return wrapper;
    }

    @Override
    public void setContainer(Container container) {
        if (!(container instanceof SimpleContext))
            throw new IllegalArgumentException("Illegal type of container");
        context = (SimpleContext)container;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
