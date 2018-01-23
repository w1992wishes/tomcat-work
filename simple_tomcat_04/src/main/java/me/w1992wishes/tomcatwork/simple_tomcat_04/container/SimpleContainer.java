package me.w1992wishes.tomcatwork.simple_tomcat_04.container;

import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_04.processor.DefaultProcessor;
import me.w1992wishes.tomcatwork.simple_tomcat_04.processor.Processor;
import me.w1992wishes.tomcatwork.simple_tomcat_04.processor.ServletProcessor;
import me.w1992wishes.tomcatwork.simple_tomcat_04.processor.StaticResourceProcessor;

import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/2/8.
 */
public class SimpleContainer implements Container{

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public SimpleContainer(){}

    public void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException {
        //check if this is a request for a servlet or a static resource
        //a request for a servlet begins with "/servlet/"
        Processor servletProcessor = new ServletProcessor();
        Processor staticProcessor = new StaticResourceProcessor();
        Processor defaultProcessor = new DefaultProcessor();
        staticProcessor.setProcessor(defaultProcessor);
        servletProcessor.setProcessor(staticProcessor);
        servletProcessor.process(request, response);
    }

}
