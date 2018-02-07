package me.w1992wishes.tomcatwork.simple_tomcat_07.container;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Context extends Container {

    String findServletMapping(String pattern);

    void addServletMapping(String pattern, String name);

}
