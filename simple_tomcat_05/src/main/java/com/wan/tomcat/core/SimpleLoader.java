package com.wan.tomcat.core;

import org.apache.catalina.Container;
import org.apache.catalina.Loader;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimpleLoader implements Loader {

    //类加载器的加载路径
    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    ClassLoader classLoader = null;
    Container container = null;

    public SimpleLoader() {
        try {
            URL[] urls = new URL[1];
            URLStreamHandler streamHandler = null;
            File classPath = new File(WEB_ROOT);
            String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator)).toString();
            urls[0] = new URL(null, repository, streamHandler);
            classLoader = new URLClassLoader(urls);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void addRepository(String s) {

    }

    @Override
    public String[] findRepositories() {
        return new String[0];
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public boolean getDelegate() {
        return false;
    }

    @Override
    public String getInfo() {
        return "A simple loader";
    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public boolean modified() {
        return false;
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public void setDelegate(boolean b) {

    }

    @Override
    public void setReloadable(boolean b) {

    }
}
