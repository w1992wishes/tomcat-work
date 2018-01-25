package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Valve {

    /**
     * Return descriptive information about this Valve implementation.
     */
    String getInfo();

    void invoke(HttpRequest request, HttpResponse response,
                       ValveContext context);
}
