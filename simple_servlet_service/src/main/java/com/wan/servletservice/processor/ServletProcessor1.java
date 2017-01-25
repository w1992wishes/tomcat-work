package com.wan.servletservice.processor;

import com.wan.servletservice.Request;
import com.wan.servletservice.Response;
import com.wan.servletservice.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by wanqinfeng on 2017/1/24.
 */
public class ServletProcessor1 {

    private Logger LOGGER = LoggerFactory.getLogger(ServletProcessor1.class);

    public void process(Request request, Response response) {
        String uri = request.getUri();
        String servletName = uri.substring(uri.lastIndexOf("/") + 1);
        URLClassLoader loader = null;
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(Constants.WEB_ROOT);
            //the classPath of repository is taken from the
            //createClassLoader method n
            //org.apache.catalina.startup.ClassLoaderFactory
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            LOGGER.debug(classPath.getCanonicalPath());
            LOGGER.debug(repository);
            //the code for forming the URL is taken form
            //the addRepository method in
            //org.apache.catalina.loader.StandardClassLoader
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            LOGGER.error("servlet process fail", e);
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            LOGGER.error("load class fail", e);
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service((ServletRequest) request, (ServletResponse) response);
        } catch (Exception e) {
            LOGGER.error("", e);
        } catch (Throwable e) {
            LOGGER.error("", e);
        }
    }
}
