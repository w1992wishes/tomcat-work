package me.w1992wishes.tomcatwork.simple_tomcat_04.container;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by wanqinfeng on 2017/2/8.
 */
public class SimpleContainer implements Container{

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public SimpleContainer(){}

    public void invoke(Request request, Response response) throws IOException, ServletException {
        String servletName = ((HttpServletRequest) request).getRequestURI();
        servletName = servletName.substring(servletName.lastIndexOf("/") + 1);
        URLClassLoader loader = null;

        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(WEB_ROOT);
        String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator) ).toString();
        urls[0] = new URL(null, repository, streamHandler);
        loader = new URLClassLoader(urls);

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service((HttpServletRequest)request, (HttpServletResponse)response);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
