package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

import me.w1992wishes.tomcatwork.simple_tomcat_05.*;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimplePipeline;
import me.w1992wishes.tomcatwork.simple_tomcat_05.exception.LifecycleException;
import me.w1992wishes.tomcatwork.simple_tomcat_05.util.LifecycleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public abstract class ContainerBase implements Container, Lifecycle, Pipeline{

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * Perform addChild with the permissions of this class.
     * addChild can be called with the XML parser on the stack,
     * this allows the XML parser to have fewer privileges than
     * Tomcat.
     */
    protected class PrivilegedAddChild  implements PrivilegedAction {

        private Container child;

        PrivilegedAddChild(Container child) {
            this.child = child;
        }

        public Object run() {
            addChildInternal(child);
            return null;
        }

    }

    protected Map<String, Container> children = new HashMap();

    /**
     * Has this component been started?
     */
    protected boolean started = false;

    protected ArrayList<ContainerListener> listeners = new ArrayList();

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * The Loader implementation with which this Container is associated.
     */
    protected Loader loader = null;

    /**
     * The human-readable name of this Container.
     */
    protected String name = null;

    /**
     * The parent Container to which this Container is a child.
     */
    protected Container parent = null;

    /**
     * The Pipeline object with which this Container is associated.
     */
    protected Pipeline pipeline = new SimplePipeline(this);

    /**
     * The one and only Mapper associated with this Container, if any.
     */
    protected Mapper mapper = null;

    /**
     * The set of Mappers associated with this Container, keyed by protocol.
     */
    protected Map<String, Mapper> mappers = new HashMap();

    /**
     * The Java class name of the default Mapper class for this Container.
     */
    protected String mapperClass = null;

    //------------------------------------------------------------------------------------

    /**
     * Process the specified Request, to produce the corresponding Response,
     * by invoking the first Valve in our pipeline (if any), or the basic
     * Valve otherwise.
     *
     * @param request Request to be processed
     * @param response Response to be produced
     *
     * @exception IllegalStateException if neither a pipeline or a basic
     *  Valve have been configured for this Container
     * @exception IOException if an input/output error occurred while
     *  processing
     * @exception ServletException if a ServletException was thrown
     *  while processing this request
     */
    @Override
    public void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        pipeline.invoke(request, response);
    }

    public Pipeline getPipeline() {
        return (this.pipeline);
    }

    // ------------------------------------------------------ Container Methods

    @Override
    public abstract String getInfo();

    @Override
    public Loader getLoader() {
        if (loader != null)
            return (loader);
        if (parent != null)
            return (parent.getLoader());
        return (null);
    }

    @Override
    public synchronized void setLoader(Loader loader) {

        // Change components if necessary
        Loader oldLoader = this.loader;
        if (oldLoader == loader)
            return;
        this.loader = loader;

        // Stop the old component if necessary
        if (started && (oldLoader != null) && (oldLoader instanceof Lifecycle)) {
            try {
                ((Lifecycle) oldLoader).stop();
            } catch (LifecycleException e) {
                LOGGER.error("ContainerBase.setLoader: stop: ", e);
            }
        }

        // Start the new component if necessary
        if (loader != null)
            loader.setContainer(this);
        if (started && (loader != null) &&
                (loader instanceof Lifecycle)) {
            try {
                ((Lifecycle) loader).start();
            } catch (LifecycleException e) {
                LOGGER.error("ContainerBase.setLoader: start: ", e);
            }
        }

    }

    @Override
    public Container getParent() {
        return parent;
    }

    @Override
    public void setParent(Container parent) {
        this.parent = parent;
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
    public void addMapper(Mapper mapper) {
        synchronized(mappers) {
            if (mappers.get(mapper.getProtocol()) != null)
                throw new IllegalArgumentException("addMapper:  Protocol '" +  mapper.getProtocol() + "' is not unique");
            mapper.setContainer((Container) this);
            if (started && (mapper instanceof Lifecycle)) {
                try {
                    ((Lifecycle) mapper).start();
                } catch (LifecycleException e) {
                    LOGGER.error("ContainerBase.addMapper: start: ", e);
                    throw new IllegalStateException("ContainerBase.addMapper: start: " + e);
                }
            }
            mappers.put(mapper.getProtocol(), mapper);
            if (mappers.size() == 1)
                this.mapper = mapper;
            else
                this.mapper = null;
            fireContainerEvent(ADD_MAPPER_EVENT, mapper);
        }
    }

    @Override
    public Mapper findMapper(String protocol) {
        if (mapper != null)
            return (mapper);
        else
            synchronized (mappers) {
                return ((Mapper) mappers.get(protocol));
            }
    }

    @Override
    public Mapper[] findMappers() {
        synchronized (mappers) {
            Mapper results[] = new Mapper[mappers.size()];
            return ((Mapper[]) mappers.values().toArray(results));
        }
    }

    @Override
    public Container map(HttpRequest request, boolean update) {
        // Select the Mapper we will use
        Mapper mapper = findMapper(request.getRequest().getProtocol());
        if (mapper == null)
            return (null);

        // Use this Mapper to perform this mapping
        return (mapper.map(request, update));
    }

    @Override
    public Container findChild(String name) {
        if (name == null)
            return (null);
        synchronized (children) {
            return (children.get(name));
        }
    }

    @Override
    public Container[] findChildren() {
        synchronized (children) {
            Container results[] = new Container[children.size()];
            return (children.values().toArray(results));
        }
    }

    @Override
    public void removeChild(Container child) {

        synchronized(children) {
            if (children.get(child.getName()) == null){
                return;
            }
            children.remove(child.getName());
        }
        if (started && (child instanceof Lifecycle)) {
            try {
                ((Lifecycle) child).stop();
            } catch (LifecycleException e) {
                LOGGER.error("ContainerBase.removeChild: stop: ", e);
            }
        }
        fireContainerEvent(REMOVE_CHILD_EVENT, child);
        child.setParent(null);

    }

    @Override
    public void addChild(Container child) {
        if (System.getSecurityManager() != null) {
            PrivilegedAction dp = new PrivilegedAddChild(child);
            AccessController.doPrivileged(dp);
        } else {
            addChildInternal(child);
        }
    }

    private void addChildInternal(Container child) {
        synchronized(children) {
            if (children.get(child.getName()) != null){
                throw new IllegalArgumentException("addChild:  Child name '" +  child.getName() + "' is not unique");
            }
            child.setParent((Container) this);
            children.put(child.getName(), child);

            if (started && (child instanceof Lifecycle)) {
                boolean success = false;
                try {
                    ((Lifecycle) child).start();
                    success = true;
                } catch (LifecycleException e) {
                    LOGGER.error("ContainerBase.addChild: start: ", e);
                    throw new IllegalStateException("ContainerBase.addChild: start: " + e);
                } finally {
                    if (!success) {
                        children.remove(child.getName());
                    }
                }
            }
            fireContainerEvent(ADD_CHILD_EVENT, child);
        }
    }

    @Override
    public void addContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public ContainerListener[] findContainerListeners() {
        synchronized (listeners) {
            ContainerListener[] results = new ContainerListener[listeners.size()];
            return ((ContainerListener[]) listeners.toArray(results));
        }
    }

    @Override
    public void removeContainerListener(ContainerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void fireContainerEvent(String type, Object data) {

        if (listeners.size() < 1){
            return;
        }

        ContainerEvent event = new ContainerEvent(this, type, data);
        ContainerListener list[] = new ContainerListener[0];
        synchronized (listeners) {
            list = (ContainerListener[]) listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++){
            ((ContainerListener) list[i]).containerEvent(event);
        }

    }

    // ------------------------------------------------------ Lifecycle Methods

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        lifecycle.addLifecycleListener(listener);
    }

    @Override
    public LifecycleListener[] findLifecycleListeners() {
        return lifecycle.findLifecycleListeners();
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        lifecycle.removeLifecycleListener(listener);
    }

    @Override
    public synchronized void start() throws LifecycleException {

        // Validate and update our current component state
        if (started){
            throw new LifecycleException("containerBase.alreadyStarted");
        }

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_START_EVENT, null);

        started = true;

        // Start our subordinate components, if any
        if ((loader != null) && (loader instanceof Lifecycle))
            ((Lifecycle) loader).start();

        // Start our child containers, if any
        Container children[] = findChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Lifecycle)
                ((Lifecycle) children[i]).start();
        }

        // Start the Valves in our pipeline (including the basic), if any
        if (pipeline instanceof Lifecycle)
            ((Lifecycle) pipeline).start();

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(START_EVENT, null);

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_START_EVENT, null);

    }

    @Override
    public synchronized void stop() throws LifecycleException {

        // Validate and update our current component state
        if (!started)
            throw new LifecycleException("containerBase.notStarted");

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(BEFORE_STOP_EVENT, null);

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        // Stop the Valves in our pipeline (including the basic), if any
        if (pipeline instanceof Lifecycle) {
            ((Lifecycle) pipeline).stop();
        }

        // Stop our child containers, if any
        Container children[] = findChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Lifecycle)
                ((Lifecycle) children[i]).stop();
        }

        if ((loader != null) && (loader instanceof Lifecycle)) {
            ((Lifecycle) loader).stop();
        }

        // Notify our interested LifecycleListeners
        lifecycle.fireLifecycleEvent(AFTER_STOP_EVENT, null);

    }

    // ------------------------------------------------------- Pipeline Methods

    @Override
    public void setBasic(Valve valve) {
        pipeline.setBasic(valve);
    }

    @Override
    public Valve getBasic() {
        return (pipeline.getBasic());
    }

    @Override
    public synchronized void removeValve(Valve valve) {
        pipeline.removeValve(valve);
        fireContainerEvent(REMOVE_VALVE_EVENT, valve);
    }

    @Override
    public Valve[] getValves() {
        return (pipeline.getValves());
    }

    @Override
    public synchronized void addValve(Valve valve) {
        pipeline.addValve(valve);
        fireContainerEvent(ADD_VALVE_EVENT, valve);
    }

}
