package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface ValveContext {

    /**
     * Return descriptive information about this ValveContext implementation.
     */
    public String getInfo();

    public void invokeNext(HttpRequest request, HttpResponse response);

}
