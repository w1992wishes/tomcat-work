package me.w1992wishes.tomcatwork.simple_tomcat_07;

import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Container;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Loader {

    String getInfo();

    ClassLoader getClassLoader();

    Container getContainer();

    void setContainer(Container container);

}
