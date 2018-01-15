package me.w1992wishes.tomcatwork.simple_tomcat_02.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Response;
import me.w1992wishes.tomcatwork.simple_tomcat_02.constant.Constants;
import me.w1992wishes.tomcatwork.simple_tomcat_02.facade.RequestFacade;
import me.w1992wishes.tomcatwork.simple_tomcat_02.Request;
import me.w1992wishes.tomcatwork.simple_tomcat_02.facade.ResponseFacade;

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
public class ServletProcessor extends Processor{

    @Override
    boolean match(String url) {
        return url.startsWith("/servlet");
    }

    @Override
    protected void action(Request request, Response response) {
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
            LOG.debug(classPath.getCanonicalPath());
            LOG.debug(repository);
            //the code for forming the URL is taken form
            //the addRepository method in
            //org.apache.catalina.loader.StandardClassLoader
            urls[0] = new URL(null, repository, streamHandler);
            loader = new URLClassLoader(urls);
        } catch (IOException e) {
            LOG.error("servlet process fail", e);
        }

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            LOG.error("load class fail", e);
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            RequestFacade requestFacade = new RequestFacade(request);
            ResponseFacade responseFacade = new ResponseFacade(response);
            servlet.service((ServletRequest) requestFacade, (ServletResponse) responseFacade);
        } catch (Exception e) {
            LOG.error("", e);
        } catch (Throwable e) {
            LOG.error("", e);
        }
    }
}
