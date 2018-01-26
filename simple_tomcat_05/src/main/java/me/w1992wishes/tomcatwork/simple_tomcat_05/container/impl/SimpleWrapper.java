package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.ContainerBase;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * 一个wrapper容器只有一个servlet类
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimpleWrapper extends ContainerBase implements Wrapper {

    // the servlet instance
    private Servlet instance = null;
    private String servletClass;

    public SimpleWrapper(){
        setBasic(new SimpleWrapperValve());
    }

    private Servlet loadServlet() throws ServletException{
        if (instance != null){
            return instance;
        }

        Servlet servlet = null;
        String actualClass = servletClass;
        if (actualClass == null){
            throw new ServletException("Servlet class has not been specified");
        }

        Loader loader = getLoader();
        //Acquire an instance of the class loader to be used
        if (loader == null){
            throw new ServletException("No loader.");
        }
        ClassLoader classLoader = loader.getClassLoader();

        //load the specified servlet cass from the appropriate class loader
        Class classClass = null;
        try{
            if (classLoader != null){
                classClass = classLoader.loadClass(actualClass);
            }
        }catch (ClassNotFoundException e){
            throw new ServletException("Servlet class not found");
        }

        //Instantiate and initialize an instance of the servlet class itself
        try{
            servlet = (Servlet)classClass.newInstance();
        }catch (Throwable e){
            throw new ServletException("Failed to instantiate servlet");
        }

        //call the initialization method of the servlet
        try{
            servlet.init(null);
        }catch (Throwable f){
            throw new ServletException("Failed initialize servlet");
        }
        return servlet;
    }

    @Override
    public void load() throws ServletException {
        instance = loadServlet();
    }

    @Override
    public Servlet allocate() throws ServletException {
        //Load and initialize our instance if necessary
        if (instance == null){
            try{
                instance = loadServlet();
            }catch (ServletException e){
                throw e;
            }catch (Throwable e){
                throw new ServletException("Cannot allocate a servlet instance");
            }
        }
        return instance;
    }

    @Override
    public void setServletClass(String servletClass) {
        this.servletClass = servletClass;
    }

    @Override
    public String getServletClass() {
        return servletClass;
    }

    @Override
    public String getInfo() {
        return "simple wrapper";
    }

    /**
     * wrapper是最小的容器，不能添加child
     * @param child
     */
    public void addChild(Container child) {
        throw new IllegalStateException("SimpleWrapper.notChild");
    }

}
