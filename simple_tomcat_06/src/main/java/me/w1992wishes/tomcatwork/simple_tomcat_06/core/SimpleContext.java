package me.w1992wishes.tomcatwork.simple_tomcat_06.core;

import org.apache.catalina.*;
import org.apache.catalina.deploy.*;
import org.apache.catalina.util.CharsetMapper;
import org.apache.catalina.util.LifecycleSupport;

import javax.naming.directory.DirContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wanqinfeng on 2017/2/25.
 */
public class SimpleContext implements Context, Pipeline,Lifecycle{

    //构造方法，设置基础阀
    public SimpleContext(){
        pipeline.setBasic(new SimpleContextValve());
    }

    protected HashMap children = new HashMap();
    private Loader loader;
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);
    private SimplePipeline pipeline = new SimplePipeline(this);
    private HashMap servletMappings = new HashMap();
    protected Mapper mapper;
    protected HashMap mappers = new HashMap();
    private Container parent;
    protected boolean started;

    public void addServletMapping(String pattern, String name) {
        synchronized (servletMappings){
            servletMappings.put(pattern, name);
        }
    }

    public String findServletMapping(String pattern) {
        synchronized (servletMappings){
            return ((String)servletMappings.get(pattern));
        }
    }

    public Loader getLoader() {
        if (loader != null)
            return loader;
        if (parent != null)
            return parent.getLoader();
        return null;
    }

    public void setLoader(Loader loader) {
        this.loader = loader;
    }

    public void addChild(Container child) {
        child.setParent((Container) this);
        children.put(child.getName(), child);
    }

    public void addMapper(Mapper mapper) {
        mapper.setContainer((Container) this);
        synchronized (mappers){
            if (mappers.get(mapper.getProtocol()) != null)
                throw new IllegalArgumentException("adMapper: Protocol" + mapper.getProtocol() + "is not unique");
            mapper.setContainer((Container)this);
            mappers.put(mapper.getProtocol(), mapper);
            if (mappers.size() == 1)
                this.mapper = mapper;
            else
                this.mapper = null;
        }

    }

    public Container findChild(String name) {
        if (name == null)
            return null;
        synchronized (children){
            return ((Container)children.get(name));
        }
    }

    public Container[] findChildren() {
        synchronized (children){
            Container[] results = new Container[children.size()];
            return ((Container[]) children.values().toArray(results));
        }
    }

    public ContainerListener[] findContainerListeners() {
        return new ContainerListener[0];
    }

    public Mapper findMapper(String protocol) {
        if (mapper != null)
            return mapper;
        else
            synchronized (mappers) {
                return ((Mapper)mappers.get(protocol));
            }
    }

    public void invoke(Request request, Response response) throws IOException, ServletException {
        pipeline.invoke(request,response);
    }

    public Container map(Request request, boolean update) {
        Mapper mapper = findMapper(request.getRequest().getProtocol());
        if (mappers == null)
            return null;
        return (mapper.map(request, update));
    }

    public Valve getBasic() {
        return pipeline.getBasic();
    }

    public Valve[] getValves() {
        return pipeline.getValves();
    }

    public void removeValve(Valve valve) {
        pipeline.removeValve(valve);
    }

    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    public LifecycleListener[] findLifecycleListeners() {
        return lifecycle.findLifecycleListeners();
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    public synchronized void start() throws LifecycleException {
        if (started)
            throw new LifecycleException("SimpleContext has already started");

        //Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);
        started = true;
        try{
            //start our subordinate components, if any
            if ((loader != null) && (loader instanceof Lifecycle))
                ((Lifecycle)loader).start();

            //start our child containers, if any
            Container[] children = findChildren();
            for (int i = 0; i<children.length; i++){
                if (children[i] instanceof Lifecycle)
                    ((Lifecycle)children[i]).start();
            }

            //start the valves in our pipeline
            //if any
            if (pipeline instanceof Lifecycle)
                ((Lifecycle)pipeline).start();
            //Notify our interested LifecycleListeners
            lifecycle.fireLifecycleEvent(START_EVENT, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        //Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);
    }

    public synchronized void stop() throws LifecycleException {
        if (!started)
            throw new LifecycleException("SimpleContext has not been started");
        //Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;
        try{
            //Stop the valves in our pipeline
            if (pipeline instanceof Lifecycle){
                ((Lifecycle)pipeline).stop();
            }

            //stop our child containers
            Container[] children = findChildren();
            for (int i = 0; i<children.length; i++){
                if (children[i] instanceof Lifecycle){
                    ((Lifecycle)children[i]).stop();
                }
            }
            if ((loader != null) && (loader instanceof Lifecycle)){
                ((Lifecycle)loader).stop();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //notify our interested lifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);
    }

    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

    }

    public void addApplicationListener(String s) {

    }

    public void addApplicationParameter(ApplicationParameter applicationParameter) {

    }

    public void addConstraint(SecurityConstraint securityConstraint) {

    }

    public void addEjb(ContextEjb contextEjb) {

    }

    public void addEnvironment(ContextEnvironment contextEnvironment) {

    }

    public void addErrorPage(ErrorPage errorPage) {

    }

    public void addFilterDef(FilterDef filterDef) {

    }

    public void addFilterMap(FilterMap filterMap) {

    }

    public void addInstanceListener(String s) {

    }

    public void addLocalEjb(ContextLocalEjb contextLocalEjb) {

    }

    public void addMimeMapping(String s, String s1) {

    }

    public void addParameter(String s, String s1) {

    }

    public void addResource(ContextResource contextResource) {

    }

    public void addResourceEnvRef(String s, String s1) {

    }

    public void addResourceLink(ContextResourceLink contextResourceLink) {

    }

    public void addRoleMapping(String s, String s1) {

    }

    public void addSecurityRole(String s) {

    }

    public void addTaglib(String s, String s1) {

    }

    public void addWelcomeFile(String s) {

    }

    public void addWrapperLifecycle(String s) {

    }

    public void addWrapperListener(String s) {

    }

    public Wrapper createWrapper() {
        return null;
    }

    public String[] findApplicationListeners() {
        return new String[0];
    }

    public ApplicationParameter[] findApplicationParameters() {
        return new ApplicationParameter[0];
    }

    public SecurityConstraint[] findConstraints() {
        return new SecurityConstraint[0];
    }

    public ContextEjb findEjb(String s) {
        return null;
    }

    public ContextEjb[] findEjbs() {
        return new ContextEjb[0];
    }

    public ContextEnvironment findEnvironment(String s) {
        return null;
    }

    public ContextEnvironment[] findEnvironments() {
        return new ContextEnvironment[0];
    }

    public ErrorPage findErrorPage(int i) {
        return null;
    }

    public ErrorPage findErrorPage(String s) {
        return null;
    }

    public ErrorPage[] findErrorPages() {
        return new ErrorPage[0];
    }

    public FilterDef findFilterDef(String s) {
        return null;
    }

    public FilterDef[] findFilterDefs() {
        return new FilterDef[0];
    }

    public FilterMap[] findFilterMaps() {
        return new FilterMap[0];
    }

    public String[] findInstanceListeners() {
        return new String[0];
    }

    public ContextLocalEjb findLocalEjb(String s) {
        return null;
    }

    public ContextLocalEjb[] findLocalEjbs() {
        return new ContextLocalEjb[0];
    }

    public String findMimeMapping(String s) {
        return null;
    }

    public String[] findMimeMappings() {
        return new String[0];
    }

    public String findParameter(String s) {
        return null;
    }

    public String[] findParameters() {
        return new String[0];
    }

    public ContextResource findResource(String s) {
        return null;
    }

    public String findResourceEnvRef(String s) {
        return null;
    }

    public String[] findResourceEnvRefs() {
        return new String[0];
    }

    public ContextResourceLink findResourceLink(String s) {
        return null;
    }

    public ContextResourceLink[] findResourceLinks() {
        return new ContextResourceLink[0];
    }

    public ContextResource[] findResources() {
        return new ContextResource[0];
    }

    public String findRoleMapping(String s) {
        return null;
    }

    public boolean findSecurityRole(String s) {
        return false;
    }

    public String[] findSecurityRoles() {
        return new String[0];
    }

    public String[] findServletMappings() {
        return new String[0];
    }

    public String findStatusPage(int i) {
        return null;
    }

    public int[] findStatusPages() {
        return new int[0];
    }

    public String findTaglib(String s) {
        return null;
    }

    public String[] findTaglibs() {
        return new String[0];
    }

    public boolean findWelcomeFile(String s) {
        return false;
    }

    public String[] findWelcomeFiles() {
        return new String[0];
    }

    public String[] findWrapperLifecycles() {
        return new String[0];
    }

    public String[] findWrapperListeners() {
        return new String[0];
    }

    public Object[] getApplicationListeners() {
        return new Object[0];
    }

    public boolean getAvailable() {
        return false;
    }

    public CharsetMapper getCharsetMapper() {
        return null;
    }

    public boolean getConfigured() {
        return false;
    }

    public boolean getCookies() {
        return false;
    }

    public boolean getCrossContext() {
        return false;
    }

    public String getDisplayName() {
        return null;
    }

    public boolean getDistributable() {
        return false;
    }

    public String getDocBase() {
        return null;
    }

    public LoginConfig getLoginConfig() {
        return null;
    }

    public boolean getOverride() {
        return false;
    }

    public String getPath() {
        return null;
    }

    public boolean getPrivileged() {
        return false;
    }

    public String getPublicId() {
        return null;
    }

    public boolean getReloadable() {
        return false;
    }

    public ServletContext getServletContext() {
        return null;
    }

    public int getSessionTimeout() {
        return 0;
    }

    public String getWrapperClass() {
        return null;
    }

    public void reload() {

    }

    public void removeApplicationListener(String s) {

    }

    public void removeApplicationParameter(String s) {

    }

    public void removeConstraint(SecurityConstraint securityConstraint) {

    }

    public void removeEjb(String s) {

    }

    public void removeEnvironment(String s) {

    }

    public void removeErrorPage(ErrorPage errorPage) {

    }

    public void removeFilterDef(FilterDef filterDef) {

    }

    public void removeFilterMap(FilterMap filterMap) {

    }

    public void removeInstanceListener(String s) {

    }

    public void removeLocalEjb(String s) {

    }

    public void removeMimeMapping(String s) {

    }

    public void removeParameter(String s) {

    }

    public void removeResource(String s) {

    }

    public void removeResourceEnvRef(String s) {

    }

    public void removeResourceLink(String s) {

    }

    public void removeRoleMapping(String s) {

    }

    public void removeSecurityRole(String s) {

    }

    public void removeServletMapping(String s) {

    }

    public void removeTaglib(String s) {

    }

    public void removeWelcomeFile(String s) {

    }

    public void removeWrapperLifecycle(String s) {

    }

    public void removeWrapperListener(String s) {

    }

    public void setApplicationListeners(Object[] objects) {

    }

    public void setAvailable(boolean b) {

    }

    public void setCharsetMapper(CharsetMapper charsetMapper) {

    }

    public void setConfigured(boolean b) {

    }

    public void setCookies(boolean b) {

    }

    public void setCrossContext(boolean b) {

    }

    public void setDisplayName(String s) {

    }

    public void setDistributable(boolean b) {

    }

    public void setDocBase(String s) {

    }

    public void setLoginConfig(LoginConfig loginConfig) {

    }

    public NamingResources getNamingResources() {
        return null;
    }

    public void setNamingResources(NamingResources namingResources) {

    }

    public void setOverride(boolean b) {

    }

    public void setPath(String s) {

    }

    public void setPrivileged(boolean b) {

    }

    public void setPublicId(String s) {

    }

    public void setReloadable(boolean b) {

    }

    public void setSessionTimeout(int i) {

    }

    public void setWrapperClass(String s) {

    }

    public void addContainerListener(ContainerListener containerListener) {

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

    public void addValve(Valve valve) {

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
