package me.w1992wishes.tomcatwork.simple_tomcat_04.processor;


import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpResponse;

import java.io.IOException;

public class StaticResourceProcessor extends Processor {

    @Override
    boolean match(String url) {
        return url == null ? false : url.startsWith("/resource");
    }

    @Override
    protected void action(HttpRequest request, HttpResponse response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            LOGGER.error("send static resource failure", e);
        }
    }

}
