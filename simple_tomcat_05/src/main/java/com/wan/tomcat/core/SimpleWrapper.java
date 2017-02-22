package com.wan.tomcat.core;

import org.apache.catalina.*;

import javax.naming.directory.DirContext;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * 一个wrapper容器只有一个servlet类
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimpleWrapper implements Wrapper, Pipeline{

    // the servlet instance
    private Servlet instance = null;
    private String servletClass;
    private Loader loader;
    private String name;
    protected  Container parent = null;
    //每个容器都有一个pipeline
    private SimplePipeline pipeline = new SimplePipeline(this);

    public SimpleWrapper(){
        pipeline.setBasic(new SimpleWrapperValve());
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
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
    public void addInitParameter(String s, String s1) {

    }

    @Override
    public void addInstanceListener(InstanceListener instanceListener) {

    }

    @Override
    public void addSecurityReference(String s, String s1) {

    }

    @Override
    public String findInitParameter(String s) {
        return null;
    }

    @Override
    public String[] findInitParameters() {
        return new String[0];
    }

    @Override
    public String findSecurityReference(String s) {
        return null;
    }

    @Override
    public String[] findSecurityReferences() {
        return new String[0];
    }

    @Override
    public long getAvailable() {
        return 0;
    }

    @Override
    public String getJspFile() {
        return null;
    }

    @Override
    public int getLoadOnStartup() {
        return 0;
    }

    @Override
    public String getRunAs() {
        return null;
    }

    @Override
    public boolean isUnavailable() {
        return false;
    }

    @Override
    public void removeInitParameter(String s) {

    }

    @Override
    public void removeInstanceListener(InstanceListener instanceListener) {

    }

    @Override
    public void removeSecurityReference(String s) {

    }

    @Override
    public void setAvailable(long l) {

    }

    @Override
    public void setJspFile(String s) {

    }

    @Override
    public void setLoadOnStartup(int i) {

    }

    @Override
    public void setRunAs(String s) {

    }

    @Override
    public void unload() throws ServletException {

    }

    @Override
    public void unavailable(UnavailableException e) {

    }

    @Override
    public void deallocate(Servlet servlet) throws ServletException {

    }

    @Override
    public void addChild(Container container) {

    }

    @Override
    public void addContainerListener(ContainerListener containerListener) {

    }

    @Override
    public void addMapper(Mapper mapper) {

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public Container findChild(String s) {
        return null;
    }

    @Override
    public Container[] findChildren() {
        return new Container[0];
    }

    @Override
    public Mapper findMapper(String s) {
        return null;
    }

    @Override
    public Mapper[] findMappers() {
        return new Mapper[0];
    }

    @Override
    public Cluster getCluster() {
        return null;
    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public Manager getManager() {
        return null;
    }

    @Override
    public ClassLoader getParentClassLoader() {
        return null;
    }

    @Override
    public Realm getRealm() {
        return null;
    }

    @Override
    public DirContext getResources() {
        return null;
    }

    @Override
    public Container map(Request request, boolean b) {
        return null;
    }

    @Override
    public void removeChild(Container container) {

    }

    @Override
    public void removeContainerListener(ContainerListener containerListener) {

    }

    @Override
    public void removeMapper(Mapper mapper) {

    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    @Override
    public void setCluster(Cluster cluster) {

    }

    @Override
    public void setLogger(Logger logger) {

    }

    @Override
    public void setManager(Manager manager) {

    }

    @Override
    public void setParentClassLoader(ClassLoader classLoader) {

    }

    @Override
    public void setRealm(Realm realm) {

    }

    @Override
    public void setResources(DirContext dirContext) {

    }
}
