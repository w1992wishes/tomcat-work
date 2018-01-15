package me.w1992wishes.tomcatwork.simple_tomcat_04.core;

import org.apache.catalina.*;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

/**
 * Created by wanqinfeng on 2017/2/8.
 */
public class SimpleContainer implements Container{

    public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";

    public SimpleContainer(){}

    public void invoke(Request request, Response response) throws IOException, ServletException {
        String servletName = ((HttpServletRequest) request).getRequestURI();
        servletName = servletName.substring(servletName.lastIndexOf("/") + 1);
        URLClassLoader loader = null;

        URL[] urls = new URL[1];
        URLStreamHandler streamHandler = null;
        File classPath = new File(WEB_ROOT);
        String repository = (new URL("file", null, classPath.getCanonicalPath() + File.separator) ).toString();
        urls[0] = new URL(null, repository, streamHandler);
        loader = new URLClassLoader(urls);

        Class myClass = null;
        try {
            myClass = loader.loadClass(servletName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Servlet servlet = null;
        try {
            servlet = (Servlet) myClass.newInstance();
            servlet.service((HttpServletRequest)request, (HttpServletResponse)response);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void addChild(Container container) {

    }

    public void addContainerListener(ContainerListener containerListener) {

    }

    public void addMapper(Mapper mapper) {

    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    public Container findChild(String s) {
        return null;
    }

    public Container[] findChildren() {
        return new Container[0];
    }

    public Mapper findMapper(String s) {
        return null;
    }

    public Mapper[] findMappers() {
        return new Mapper[0];
    }

    public Cluster getCluster() {
        return null;
    }

    public String getInfo() {
        return null;
    }

    public Loader getLoader() {
        return null;
    }

    public Logger getLogger() {
        return null;
    }

    public Manager getManager() {
        return null;
    }

    public String getName() {
        return null;
    }

    public Container getParent() {
        return null;
    }

    public ClassLoader getParentClassLoader() {
        return null;
    }

    public Realm getRealm() {
        return null;
    }

    public DirContext getResources() {
        return null;
    }

    public Container map(Request request, boolean b) {
        return null;
    }

    public void removeChild(Container container) {

    }

    public void removeContainerListener(ContainerListener containerListener) {

    }

    public void removeMapper(Mapper mapper) {

    }

    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    public void setCluster(Cluster cluster) {

    }

    public void setLoader(Loader loader) {

    }

    public void setLogger(Logger logger) {

    }

    public void setManager(Manager manager) {

    }

    public void setName(String s) {

    }

    public void setParent(Container container) {

    }

    public void setParentClassLoader(ClassLoader classLoader) {

    }

    public void setRealm(Realm realm) {

    }

    public void setResources(DirContext dirContext) {

    }
}
