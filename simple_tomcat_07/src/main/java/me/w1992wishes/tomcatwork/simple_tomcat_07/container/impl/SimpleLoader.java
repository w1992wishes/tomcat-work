package me.w1992wishes.tomcatwork.simple_tomcat_07.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_07.loader.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimpleLoader implements Loader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleLoader.class);

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
            LOGGER.error("construct SimpleLoader instance failure", e);
        }
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
    public String getInfo() {
        return "A simple loader";
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

}
