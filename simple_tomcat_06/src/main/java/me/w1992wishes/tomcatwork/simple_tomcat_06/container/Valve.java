package me.w1992wishes.tomcatwork.simple_tomcat_06.container;

import me.w1992wishes.tomcatwork.simple_tomcat_06.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_06.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Valve {

    /**
     * Return descriptive information about this Valve implementation.
     */
    String getInfo();

    void invoke(HttpRequest request, HttpResponse response, ValveContext context) throws ServletException, IOException;
}
