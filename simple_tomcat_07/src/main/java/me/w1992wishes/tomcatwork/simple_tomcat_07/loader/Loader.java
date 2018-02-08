package me.w1992wishes.tomcatwork.simple_tomcat_07.loader;

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

    /**
     * Return the "follow standard delegation model" flag used to configure
     * our ClassLoader.
     */
    boolean getDelegate();

    /**
     * Set the "follow standard delegation model" flag used to configure
     * our ClassLoader.
     *
     * @param delegate The new flag
     */
    void setDelegate(boolean delegate);

    /**
     * Return the reloadable flag for this Loader.
     */
    boolean getReloadable();

    /**
     * Set the reloadable flag for this Loader.
     *
     * @param reloadable The new reloadable flag
     */
    void setReloadable(boolean reloadable);

}
