package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.ContainerBase;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.exception.LifecycleException;

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
    private Loader loader;
    private String name;
    protected Container parent = null;
    //每个容器都有一个pipeline
    private SimplePipeline pipeline = new SimplePipeline(this);

    public SimpleWrapper(){
        pipeline.setBasic(new SimpleWrapperValve());
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response){
        pipeline.invoke(request, response);
    }

    @Override
    public void load() throws ServletException {
        instance = loadServlet();
    }

    @Override
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public Valve[] getValves() {
        return pipeline.getValves();
    }

    @Override
    public void addValve(Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }

    @Override
    public Loader getLoader() {
        if (loader != null){
            return loader;
        }
        if (parent != null){
            return parent.getLoader();
        }
        return null;
    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
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
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container container) {
        this.parent = container;
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

    @Override
    public void start() throws LifecycleException {

    }

    @Override
    public void stop() throws LifecycleException {

    }
}
