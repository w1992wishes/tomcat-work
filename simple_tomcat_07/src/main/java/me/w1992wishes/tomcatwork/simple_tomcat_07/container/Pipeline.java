package me.w1992wishes.tomcatwork.simple_tomcat_07.container;

import me.w1992wishes.tomcatwork.simple_tomcat_07.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_07.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Pipeline {

    Valve getBasic();

    void setBasic(Valve valve);

    void addValve(Valve valve);

    Valve[] getValves();

    void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException;

    void removeValve(Valve valve);
}
