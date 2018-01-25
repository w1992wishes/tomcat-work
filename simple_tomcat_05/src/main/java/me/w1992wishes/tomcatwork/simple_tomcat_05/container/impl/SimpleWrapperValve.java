package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

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
public class SimpleWrapperValve implements me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve, Contained {

    protected me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container;

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public void invoke(Request request, Response response, me.w1992wishes.tomcatwork.simple_tomcat_05.container.ValveContext valveContext) throws IOException, ServletException {
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
        try {
            servlet = wrapper.allocate();
            if (hres != null && hreq != null) {
                servlet.service(hreq, hres);
            } else {
                servlet.service(sreq, sres);
            }
        } catch (ServletException e) {
        }
    }
}
