package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_05.container.*;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleContext;
import org.apache.catalina.*;
import org.apache.catalina.Container;
import org.apache.catalina.Wrapper;

import javax.servlet.http.HttpServletRequest;

/**
 * 映射器组件，帮助servlet容器，选择一个子容器来处理某个指定的请求
 * Created by wanqinfeng on 2017/2/22.
 */
public class SimpleContextMapper implements Mapper{

    private SimpleContext context = null;
    private String protocol;

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container getContainer() {
        return context;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container map(Request request, boolean update) {
        // Identify the context-relative URI to be mapped
        String contextPath = ((HttpServletRequest)request.getRequest()).getContextPath();
        String requestURI = ((HttpServletRequest)request.getRequest()).getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length());
        // Apply the standard request URI mapping rules from the specification
        me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper wrapper = null;
        String servletPath = relativeURI;
        String pathInfo = null;
        String name = context.findServletMapping(relativeURI);
        if (name!=null)
            wrapper = (me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper)context.findChild(name);
        return wrapper;
    }

    @Override
    public void setContainer(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container) {
        if (!(container instanceof SimpleContext))
            throw new IllegalArgumentException("Illegal type of container");
        context = (SimpleContext)container;
    }

    @Override
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
