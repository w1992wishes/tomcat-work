package me.w1992wishes.tomcatwork.simple_tomcat_02.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Response;
import me.w1992wishes.tomcatwork.simple_tomcat_02.Request;

import java.io.IOException;

/**
 * 静态资源处理器
 *
 * Created by wanqinfeng on 2017/1/24.
 */
public class StaticResourceProcessor extends Processor{

    @Override
    boolean match(String url) {
        return url == null ? false : url.startsWith("/resource");
    }

    @Override
    protected void action(Request request, Response response) {
        try {
            response.sendStaticResource();
        } catch (IOException e) {
            LOGGER.error("send static resource failure");
        }
    }
}
