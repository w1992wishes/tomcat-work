package me.w1992wishes.tomcatwork.simple_tomcat_03.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_03.connector.http.*;

import javax.servlet.Servlet;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

public class ServletProcessor extends Processor{

    @Override
    boolean match(String url) {
        return url == null ? false : url.startsWith("/servlet");
    }

    @Override
    protected void action(HttpRequest request, HttpResponse response) {
        String uri = request.getRequestURI();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        try {
            // create a URLClassLoader
            URL[] urls = new URL[1];
            File classPath = new File(Constants.WEB_ROOT);
            URLStreamHandler streamHandler = null;
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class failure", e);
        }

        Servlet servlet = null;

        try {
            servlet = (Servlet) myClass.newInstance();
            HttpResponseFacade responseFacade = new HttpResponseFacade(response);
            HttpRequestFacade requestFacade = new HttpRequestFacade(request);
            servlet.service(requestFacade, responseFacade);
            ((HttpResponse) response).finishResponse();
        } catch (Exception e) {
            LOGGER.error("", e);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }
}