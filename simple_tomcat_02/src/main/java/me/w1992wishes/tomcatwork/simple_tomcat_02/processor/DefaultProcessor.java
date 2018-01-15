package me.w1992wishes.tomcatwork.simple_tomcat_02.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_02.Request;
import me.w1992wishes.tomcatwork.simple_tomcat_02.Response;

import java.io.IOException;
import java.io.Writer;

/**
 * 默认都不匹配由该Processor处理
 *
 * Created by w1992wishes
 * on 2018/1/15.
 */
public class DefaultProcessor extends Processor {

    @Override
    boolean match(String url) {
        return true;
    }

    @Override
    protected void action(Request request, Response response) {
        Writer writer = null;
        try {
            writer = response.getWriter();
            String message = "no suitable processor";
            writer.write(message);
        } catch (IOException e) {
            LOG.error("get response writer failure");
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    LOG.error("close writer failure");
                }
            }
        }

    }

}
