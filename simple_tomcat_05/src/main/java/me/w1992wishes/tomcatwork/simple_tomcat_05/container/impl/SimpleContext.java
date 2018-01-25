package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import org.apache.catalina.*;
import org.apache.catalina.deploy.*;
import org.apache.catalina.util.CharsetMapper;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;

/**
 * servlet容器，可以包含多个servlet，对应可以包含多个Wrapper子容器
 * Created by wanqinfeng on 2017/2/22.
 */
public class SimpleContext implements Context, me.w1992wishes.tomcatwork.simple_tomcat_05.container.Pipeline {

    //构造方法中初始化context的基础阀
    public SimpleContext() {
        pipeline.setBasic(new SimpleContextValve());
    }

    protected HashMap children = new HashMap();
    protected Loader loader = null;
    protected SimplePipeline pipeline = new SimplePipeline(this);
    protected HashMap servletMappings = new HashMap();
    protected Mapper mapper = null;
    protected HashMap mappers = new HashMap();
    private me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container parent = null;

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        pipeline.invoke(request, response);
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container map(Request request, boolean update) {
        //this method is taken from the map method in org.apache.cataline.core.ContainerBase
        //the findMapper method always returns the default mapper, if any, regardless the
        //request's protocol
        Mapper mapper = findMapper(request.getRequest().getProtocol());
        if (mapper == null)
            return null;

        //Use this Mapper to perform this mapping
        return (mapper.map(request, update));
    }

    @Override
    public void addServletMapping(String pattern, String name) {
        synchronized (servletMappings) {
            servletMappings.put(pattern, name);
        }
    }

    @Override
    public String findServletMapping(String pattern) {
        synchronized (servletMappings) {
            return ((String) servletMappings.get(pattern));
        }
    }

    @Override
    public Loader getLoader() {
        if (loader != null)
            return loader;
        if (parent != null)
            return parent.getLoader();
        return null;
    }

    @Override
    public void setLoader(Loader loader) {
        this.loader = loader;
    }


    @Override
    public void addChild(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container child) {
        child.setParent((me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container) this);
        children.put(child.getName(), child);
    }


    @Override
    public void addMapper(Mapper mapper) {
        // this method is adopted from addMapper in ContainerBase
        // the first mapper added becomes the default mapper
        mapper.setContainer((me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container) this);
        this.mapper = mapper;
        synchronized (mappers) {
            if (mappers.get(mapper.getProtocol()) != null ) {
                throw new IllegalArgumentException("addMapper:  Protocol '" +
                        mapper.getProtocol() + "' is not unique");
            }
            mapper.setContainer((me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container) this);
            mappers.put(mapper.getProtocol(), mapper);
            if (mappers.size() == 1)
                this.mapper = mapper;
            else
                this.mapper = null;
        }
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container findChild(String name) {
        if (name == null)
            return null;
        synchronized (children) {
            return ((me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container) children.get(name));
        }
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container[] findChildren() {
        synchronized (children) {
            me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container results[] = new me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container[children.size()];
            return ((me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container[]) children.values().toArray(results));
        }
    }

    @Override
    public Mapper findMapper(String protocol) {
        // the default mapper will always be returned, if any,
        // regardless the value of protocol
        if (mapper != null)
            return mapper;
        else
            synchronized (mappers) {
                return ((Mapper) mappers.get(protocol));
            }
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve getBasic() {
        return pipeline.getBasic();
    }

    @Override
    public void setBasic(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public synchronized void addValve(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {
        pipeline.addValve(valve);
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve[] getValves() {
        return pipeline.getValves();
    }

    @Override
    public void removeValve(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {
        pipeline.removeValve(valve);
    }

    @Override
    public void addApplicationListener(String s) {

    }

    @Override
    public void addApplicationParameter(ApplicationParameter applicationParameter) {

    }

    @Override
    public void addConstraint(SecurityConstraint securityConstraint) {

    }

    @Override
    public void addEjb(ContextEjb contextEjb) {

    }

    @Override
    public void addEnvironment(ContextEnvironment contextEnvironment) {

    }

    @Override
    public void addErrorPage(ErrorPage errorPage) {

    }

    @Override
    public void addFilterDef(FilterDef filterDef) {

    }

    @Override
    public void addFilterMap(FilterMap filterMap) {

    }

    @Override
    public void addInstanceListener(String s) {

    }

    @Override
    public void addLocalEjb(ContextLocalEjb contextLocalEjb) {

    }

    @Override
    public void addMimeMapping(String s, String s1) {

    }

    @Override
    public void addParameter(String s, String s1) {

    }

    @Override
    public void addResource(ContextResource contextResource) {

    }

    @Override
    public void addResourceEnvRef(String s, String s1) {

    }

    @Override
    public void addRoleMapping(String s, String s1) {

    }

    @Override
    public void addSecurityRole(String s) {

    }

    @Override
    public void addTaglib(String s, String s1) {

    }

    @Override
    public void addWelcomeFile(String s) {

    }

    @Override
    public void addWrapperLifecycle(String s) {

    }

    @Override
    public void addWrapperListener(String s) {

    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper createWrapper() {
        return null;
    }

    @Override
    public String[] findApplicationListeners() {
        return new String[0];
    }

    @Override
    public ApplicationParameter[] findApplicationParameters() {
        return new ApplicationParameter[0];
    }

    @Override
    public SecurityConstraint[] findConstraints() {
        return new SecurityConstraint[0];
    }

    @Override
    public ContextEjb findEjb(String s) {
        return null;
    }

    @Override
    public ContextEjb[] findEjbs() {
        return new ContextEjb[0];
    }

    @Override
    public ContextEnvironment findEnvironment(String s) {
        return null;
    }

    @Override
    public ContextEnvironment[] findEnvironments() {
        return new ContextEnvironment[0];
    }

    @Override
    public ErrorPage findErrorPage(int i) {
        return null;
    }

    @Override
    public ErrorPage findErrorPage(String s) {
        return null;
    }

    @Override
    public ErrorPage[] findErrorPages() {
        return new ErrorPage[0];
    }

    @Override
    public FilterDef findFilterDef(String s) {
        return null;
    }

    @Override
    public FilterDef[] findFilterDefs() {
        return new FilterDef[0];
    }

    @Override
    public FilterMap[] findFilterMaps() {
        return new FilterMap[0];
    }

    @Override
    public String[] findInstanceListeners() {
        return new String[0];
    }

    @Override
    public ContextLocalEjb findLocalEjb(String s) {
        return null;
    }

    @Override
    public ContextLocalEjb[] findLocalEjbs() {
        return new ContextLocalEjb[0];
    }

    @Override
    public String findMimeMapping(String s) {
        return null;
    }

    @Override
    public String[] findMimeMappings() {
        return new String[0];
    }

    @Override
    public String findParameter(String s) {
        return null;
    }

    @Override
    public String[] findParameters() {
        return new String[0];
    }

    @Override
    public ContextResource findResource(String s) {
        return null;
    }

    @Override
    public String findResourceEnvRef(String s) {
        return null;
    }

    @Override
    public String[] findResourceEnvRefs() {
        return new String[0];
    }

    @Override
    public ContextResource[] findResources() {
        return new ContextResource[0];
    }

    @Override
    public String findRoleMapping(String s) {
        return null;
    }

    @Override
    public boolean findSecurityRole(String s) {
        return false;
    }

    @Override
    public String[] findSecurityRoles() {
        return new String[0];
    }

    @Override
    public String[] findServletMappings() {
        return new String[0];
    }

    @Override
    public String findStatusPage(int i) {
        return null;
    }

    @Override
    public int[] findStatusPages() {
        return new int[0];
    }

    @Override
    public String findTaglib(String s) {
        return null;
    }

    @Override
    public String[] findTaglibs() {
        return new String[0];
    }

    @Override
    public boolean findWelcomeFile(String s) {
        return false;
    }

    @Override
    public String[] findWelcomeFiles() {
        return new String[0];
    }

    @Override
    public String[] findWrapperLifecycles() {
        return new String[0];
    }

    @Override
    public String[] findWrapperListeners() {
        return new String[0];
    }

    @Override
    public Object[] getApplicationListeners() {
        return new Object[0];
    }

    @Override
    public boolean getAvailable() {
        return false;
    }

    @Override
    public CharsetMapper getCharsetMapper() {
        return null;
    }

    @Override
    public boolean getConfigured() {
        return false;
    }

    @Override
    public boolean getCookies() {
        return false;
    }

    @Override
    public boolean getCrossContext() {
        return false;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Override
    public boolean getDistributable() {
        return false;
    }

    @Override
    public String getDocBase() {
        return null;
    }

    @Override
    public LoginConfig getLoginConfig() {
        return null;
    }

    @Override
    public boolean getOverride() {
        return false;
    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public boolean getPrivileged() {
        return false;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public boolean getReloadable() {
        return false;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public int getSessionTimeout() {
        return 0;
    }

    @Override
    public String getWrapperClass() {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void removeApplicationListener(String s) {

    }

    @Override
    public void removeApplicationParameter(String s) {

    }

    @Override
    public void removeConstraint(SecurityConstraint securityConstraint) {

    }

    @Override
    public void removeEjb(String s) {

    }

    @Override
    public void removeEnvironment(String s) {

    }

    @Override
    public void removeErrorPage(ErrorPage errorPage) {

    }

    @Override
    public void removeFilterDef(FilterDef filterDef) {

    }

    @Override
    public void removeFilterMap(FilterMap filterMap) {

    }

    @Override
    public void removeInstanceListener(String s) {

    }

    @Override
    public void removeLocalEjb(String s) {

    }

    @Override
    public void removeMimeMapping(String s) {

    }

    @Override
    public void removeParameter(String s) {

    }

    @Override
    public void removeResource(String s) {

    }

    @Override
    public void removeResourceEnvRef(String s) {

    }

    @Override
    public void removeRoleMapping(String s) {

    }

    @Override
    public void removeSecurityRole(String s) {

    }

    @Override
    public void removeServletMapping(String s) {

    }

    @Override
    public void removeTaglib(String s) {

    }

    @Override
    public void removeWelcomeFile(String s) {

    }

    @Override
    public void removeWrapperLifecycle(String s) {

    }

    @Override
    public void removeWrapperListener(String s) {

    }

    @Override
    public void setApplicationListeners(Object[] objects) {

    }

    @Override
    public void setAvailable(boolean b) {

    }

    @Override
    public void setCharsetMapper(CharsetMapper charsetMapper) {

    }

    @Override
    public void setConfigured(boolean b) {

    }

    @Override
    public void setCookies(boolean b) {

    }

    @Override
    public void setCrossContext(boolean b) {

    }

    @Override
    public void setDisplayName(String s) {

    }

    @Override
    public void setDistributable(boolean b) {

    }

    @Override
    public void setDocBase(String s) {

    }

    @Override
    public void setLoginConfig(LoginConfig loginConfig) {

    }

    @Override
    public void setOverride(boolean b) {

    }

    @Override
    public void setPath(String s) {

    }

    @Override
    public void setPrivileged(boolean b) {

    }

    @Override
    public void setPublicId(String s) {

    }

    @Override
    public void setReloadable(boolean b) {

    }

    @Override
    public void setSessionTimeout(int i) {

    }

    @Override
    public void setWrapperClass(String s) {

    }

    @Override
    public void addContainerListener(ContainerListener containerListener) {

    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

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
    public String getName() {
        return null;
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container getParent() {
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
    public void removeChild(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container) {

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
    public void setName(String s) {

    }

    @Override
    public void setParent(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container) {

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
