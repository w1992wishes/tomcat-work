package me.w1992wishes.tomcatwork.simple_tomcat_07.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_07.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_07.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Contained;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Valve;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.ValveContext;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础阀
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimpleWrapperValve implements Valve, Contained {

    protected Container container;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return "The Basic Simple Wrapper Valve";
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {
        SimpleWrapper wrapper = (SimpleWrapper) getContainer();
        ServletRequest sreq = request.getRequest();
        ServletResponse sres = response.getResponse();
        Servlet servlet = null;
        HttpServletRequest hreq = null;
        if (sreq instanceof HttpServletRequest)
            hreq = (HttpServletRequest) sreq;
        HttpServletResponse hres = null;
        if (sres instanceof HttpServletResponse)
            hres = (HttpServletResponse) sres;

        //allocate servlet instance to process this request
        servlet = wrapper.allocate();
        if (hres != null && hreq != null) {
            servlet.service(hreq, hres);
        } else {
            servlet.service(sreq, sres);
        }
    }
}
