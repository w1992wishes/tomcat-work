package me.w1992wishes.tomcatwork.simple_tomcat_04.container;

import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_04.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2018/1/20.
 */
public interface Container {

    void invoke(HttpRequest request, HttpResponse response) throws IOException, ServletException;

}
