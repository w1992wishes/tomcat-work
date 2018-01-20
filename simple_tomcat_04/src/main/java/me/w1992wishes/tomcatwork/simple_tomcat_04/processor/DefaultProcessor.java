package me.w1992wishes.tomcatwork.simple_tomcat_04.processor;

import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpResponse;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by w1992wishes
 * on 2018/1/16.
 */
public class DefaultProcessor extends Processor {
    @Override
    boolean match(String url) {
        return true;
    }

    @Override
    protected void action(HttpRequest request, HttpResponse response) {
        Writer writer = null;
        try {
            writer = response.getWriter();
            String message = "no suitable processor";
            writer.write(message);
        } catch (IOException e) {
            LOGGER.error("get response writer failure");
        } finally {
            if (writer != null){
                try {
                    writer.close();
                } catch (IOException e) {
                    LOGGER.error("close writer failure");
                }
            }
        }
    }
}
