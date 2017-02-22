package com.wan.tomcat.core;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/2/22.
 */
public class SimpleContextValve implements Valve, Contained {
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
        return null;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {

        //validate the request and response object types
        if (!(request.getRequest() instanceof HttpServletRequest) || !(response.getResponse() instanceof HttpServletResponse)) {
            return;
        }

        //Disallow any direct access to resources under WEB-INF or META-INF
        HttpServletRequest hreq = (HttpServletRequest) request.getRequest();
        String contextPath = hreq.getContextPath();
        String requestURI = hreq.getRequestURI();
        String relativeURI = requestURI.substring(contextPath.length()).toUpperCase();

        Context context = (Context) getContainer();
        //select the Wrapper to be used for this Request
        Wrapper wrapper = null;
        try {
            wrapper = (Wrapper) context.map(request, true);
        } catch (IllegalArgumentException e) {
            badRequest(requestURI, (HttpServletResponse) response.getResponse());
            return;
        }
        if (wrapper == null) {
            notFound(requestURI, (HttpServletResponse) response.getResponse());
            return;
        }

        //ask this wrapper to process this request
        response.setContext(context);
        wrapper.invoke(request, response);
    }

    public void badRequest(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, requestURI);
        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        }
    }

    public void notFound(String requestURI, HttpServletResponse response) {
        try {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, requestURI);
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
    }
}
