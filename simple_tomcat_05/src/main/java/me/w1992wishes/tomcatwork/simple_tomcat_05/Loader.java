package me.w1992wishes.tomcatwork.simple_tomcat_05;

import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Loader {

    /**
     * Return descriptive information about this Loader implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    String getInfo();

    /**
     * Return the Java class loader to be used by this Container.
     */
    ClassLoader getClassLoader();


    /**
     * Return the Container with which this Loader has been associated.
     */
    Container getContainer();


    /**
     * Set the Container with which this Loader has been associated.
     *
     * @param container The associated Container
     */
    void setContainer(Container container);

}
