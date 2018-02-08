package me.w1992wishes.tomcatwork.simple_tomcat_07.loader;

import me.w1992wishes.tomcatwork.simple_tomcat_07.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_07.LifecycleListener;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Context;
import me.w1992wishes.tomcatwork.simple_tomcat_07.exception.LifecycleException;
import me.w1992wishes.tomcatwork.simple_tomcat_07.util.LifecycleSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * Created by w1992wishes
 * on 2018/2/7.
 */
public class WebappLoader implements Lifecycle , Loader , Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(WebappLoader.class);

    /**
     * The class loader being managed by this Loader component.
     */
    private  ClassLoader parentClassLoader = null;

    /**
     * The Container with which this Loader has been associated.
     */
    private Container container = null;

    /**
     * The class loader being managed by this Loader component.
     */
    private WebappClassLoader classLoader = null;

    /**
     * Has this component been started?
     */
    private boolean started = false;

    /**
     * The lifecycle event support for this component.
     */
    protected LifecycleSupport lifecycle = new LifecycleSupport(this);

    /**
     * The Java class name of the ClassLoader implementation to be used.
     * This class should extend WebappClassLoader, otherwise, a different
     * loader implementation must be used.
     */
    private String loaderClass = "org.apache.catalina.loader.WebappClassLoader";

    /**
     * The "follow standard delegation model" flag that will be used to
     * configure our ClassLoader.
     */
    private boolean delegate = false;

    /**
     * The reloadable flag for this Loader.
     */
    private boolean reloadable = false;

    /**
     * The background thread.
     */
    private Thread thread = null;


    /**
     * The background thread completion semaphore.
     */
    private boolean threadDone = false;

    /**
     * The number of seconds between checks for modified classes, if
     * automatic reloading is enabled.
     */
    private int checkInterval = 15;

    /**
     * Name to register for the background thread.
     */
    private String threadName = "WebappLoader";

    public WebappLoader(){
        super();
    }

    public WebappLoader(ClassLoader parent) {
        super();
        this.parentClassLoader = parent;
    }

    public boolean modified() {
        return (classLoader.modified());
    }

    /**
     * Create associated classLoader.
     */
    private WebappClassLoader createClassLoader()
            throws Exception {

        Class clazz = Class.forName(loaderClass);
        WebappClassLoader classLoader = null;

        if (parentClassLoader == null) {
            // Will cause a ClassCast is the class does not extend WCL, but
            // this is on purpose (the exception will be caught and rethrown)
            classLoader = (WebappClassLoader) clazz.newInstance();
        } else {
            Class[] argTypes = { ClassLoader.class };
            Object[] args = { parentClassLoader };
            Constructor constr = clazz.getConstructor(argTypes);
            classLoader = (WebappClassLoader) constr.newInstance(args);
        }

        return classLoader;

    }

    private void threadStart() {

        // Has the background thread already been started?
        if (thread != null)
            return;

        // Validate our current state
        if (!reloadable)
            throw new IllegalStateException("webappLoader.notReloadable");
        if (!(container instanceof Context))
            throw new IllegalStateException("webappLoader.notContext");

        threadDone = false;
        threadName = "WebappLoader[" + container.getName() + "]";
        thread = new Thread(this, threadName);
        thread.setDaemon(true);
        thread.start();

    }

    @Override
    public void start() throws LifecycleException {
        // Validate and update our current component state
        if (started)
            throw new LifecycleException("webappLoader.alreadyStarted");
        lifecycle.fireLifecycleEvent(START_EVENT, null);
        started = true;

        // Construct a class loader based on our current repositories list
        try {

            classLoader = createClassLoader();
            classLoader.setDelegate(this.delegate);

            if (classLoader instanceof Lifecycle)
                ((Lifecycle) classLoader).start();

        } catch (Throwable t) {
            throw new LifecycleException("start: ", t);
        }

        // Start our background thread if we are reloadable
        if (reloadable) {
            LOGGER.info("webappLoader.reloading");
            try {
                threadStart();
            } catch (IllegalStateException e) {
                throw new LifecycleException(e);
            }
        }
    }

    private void threadStop() {
        if (thread == null)
            return;

        threadDone = true;
        thread.interrupt();
        try {
            thread.join();
        } catch (InterruptedException e) {
            ;
        }

        thread = null;
    }

    @Override
    public void stop() throws LifecycleException {
        // Validate and update our current component state
        if (!started)
            throw new LifecycleException("webappLoader.notStarted");

        lifecycle.fireLifecycleEvent(STOP_EVENT, null);
        started = false;

        // Stop our background thread if we are reloadable
        if (reloadable)
            threadStop();

        classLoader = null;
    }

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
    public String getInfo() {
        return null;
    }

    @Override
    public ClassLoader getClassLoader() {
        return ((ClassLoader) classLoader);
    }

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public boolean getDelegate() {
        return delegate;
    }

    @Override
    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean getReloadable() {
        return reloadable;
    }

    @Override
    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    @Override
    public void run() {

        // Loop until the termination semaphore is set
        while (!threadDone) {

            // Wait for our check interval
            threadSleep();

            if (!started)
                break;

            try {
                // Perform our modification check
                if (!classLoader.modified())
                    continue;
            } catch (Exception e) {
               LOGGER.error("webappLoader.failModifiedCheck", e);
                continue;
            }

            // Handle a need for reloading
            notifyContext();
            break;

        }

    }

    /**
     * Sleep for the duration specified by the <code>checkInterval</code>
     * property.
     */
    private void threadSleep() {
        try {
            Thread.sleep(checkInterval * 1000L);
        } catch (InterruptedException e) {
            ;
        }
    }

    /**
     * Notify our Context that a reload is appropriate.
     */
    private void notifyContext() {
        WebappContextNotifier notifier = new WebappContextNotifier();
        (new Thread(notifier)).start();
    }

    /**
     * Private thread class to notify our associated Context that we have
     * recognized the need for a reload.
     */
    protected class WebappContextNotifier implements Runnable {
        /**
         * Perform the requested notification.
         */
        public void run() {
            //((Context) container).reload();
        }
    }

}
