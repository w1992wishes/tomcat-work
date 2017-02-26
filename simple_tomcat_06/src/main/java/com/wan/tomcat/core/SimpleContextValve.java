package com.wan.tomcat.core;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 基础阀
 * Created by wanqinfeng on 2017/2/25.
 */
public class SimpleContextValve implements Valve, Contained {

    protected Container container;

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public String getInfo() {
        return null;
    }

    public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
        //validate the request and response object type
        if (!(request.getRequest() instanceof HttpServletRequest) || !(response.getResponse() instanceof HttpServletResponse)){
            return;
        }

        //Disallow any direct access to resources under WEB-INF or MEA-INF
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String contextPath = hreq.getContextPath();
        String requestURI = hreq.getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length()).toUpperCase();

        Context context = (Context)getContainer();

        //select the wrapper to be used for this request
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper) context.map(request, true);
        }catch (IllegalArgumentException e){
            badReuset(requestURI, (HttpServletResponse) response.getResponse());
            return;
        }
        if (wrapper == null){
            notFound(requestURI, (HttpServletResponse) response.getResponse());
            return;
        }
        //ask this wrapper to process this request
        response.setContext(context);
        wrapper.invoke(request, response);
    }

    private void badReuset(String requestURI, HttpServletResponse response){
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notFound(String requestURI, HttpServletResponse response){
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
