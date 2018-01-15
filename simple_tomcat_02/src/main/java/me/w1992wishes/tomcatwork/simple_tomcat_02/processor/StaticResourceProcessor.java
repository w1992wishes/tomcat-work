package me.w1992wishes.tomcatwork.simple_tomcat_02.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Response;
import me.w1992wishes.tomcatwork.simple_tomcat_02.Request;

import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/1/24.
 */
public class StaticResourceProcessor {
    public void process(Request request, Response response) throws IOException {
        response.sendStaticResource();
    }
}
