package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_05.exception.LifecycleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public abstract class ContainerBase implements Container, Lifecycle, Pipeline{

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

    protected Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     * The child Containers belonging to this Container, keyed by name.
     */
    protected HashMap children = new HashMap();

    /**
     * Has this component been started?
     */
    protected boolean started = false;

    /**
     * Return the child Container, associated with this Container, with
     * the specified name (if any); otherwise, return <code>null</code>
     *
     * @param name Name of the child Container to be retrieved
     */
    public Container findChild(String name) {

        if (name == null)
            return (null);
        synchronized (children) {
            return ((Container) children.get(name));
        }

    }

    /**
     * Return the set of children Containers associated with this Container.
     * If this Container has no children, a zero-length array is returned.
     */
    public Container[] findChildren() {

        synchronized (children) {
            Container results[] = new Container[children.size()];
            return ((Container[]) children.values().toArray(results));
        }

    }

    /**
     * Remove an existing child Container from association with this parent
     * Container.
     *
     * @param child Existing child Container to be removed
     */
    public void removeChild(Container child) {

        synchronized(children) {
            if (children.get(child.getName()) == null)
                return;
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

    /**
     * Add a new child Container to those associated with this Container,
     * if supported.  Prior to adding this Container to the set of children,
     * the child's <code>setParent()</code> method must be called, with this
     * Container as an argument.  This method may thrown an
     * <code>IllegalArgumentException</code> if this Container chooses not
     * to be attached to the specified Container, in which case it is not added
     *
     * @param child New child Container to be added
     *
     * @exception IllegalArgumentException if this exception is thrown by
     *  the <code>setParent()</code> method of the child Container
     * @exception IllegalArgumentException if the new child does not have
     *  a name unique from that of existing children of this Container
     * @exception IllegalStateException if this Container does not support
     *  child Containers
     */
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
            if (children.get(child.getName()) != null)
                throw new IllegalArgumentException("addChild:  Child name '" +
                        child.getName() +
                        "' is not unique");
            child.setParent((Container) this);
            children.put(child.getName(), child);

            if (started && (child instanceof Lifecycle)) {
                boolean success = false;
                try {
                    ((Lifecycle) child).start();
                    success = true;
                } catch (LifecycleException e) {
                    LOGGER.error("ContainerBase.addChild: start: ", e);
                    throw new IllegalStateException
                            ("ContainerBase.addChild: start: " + e);
                } finally {
                    if (!success) {
                        children.remove(child.getName());
                    }
                }
            }
            fireContainerEvent(ADD_CHILD_EVENT, child);
        }

    }

    /**
     * Notify all container event listeners that a particular event has
     * occurred for this Container.  The default implementation performs
     * this notification synchronously using the calling thread.
     *
     * @param type Event type
     * @param data Event data
     */
    public void fireContainerEvent(String type, Object data) {

        if (listeners.size() < 1)
            return;
        ContainerEvent event = new ContainerEvent(this, type, data);
        ContainerListener list[] = new ContainerListener[0];
        synchronized (listeners) {
            list = (ContainerListener[]) listeners.toArray(list);
        }
        for (int i = 0; i < list.length; i++)
            ((ContainerListener) list[i]).containerEvent(event);

    }

}
