package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2018/1/20.
 */
public interface Container {

    /**
     * Process the specified Request, and generate the corresponding Response,
     * according to the design of this particular Container.
     *
     */
    void invoke(HttpRequest request, HttpResponse response);

    /**
     * Return the Loader with which this Container is associated.  If there is
     * no associated Loader, return the Loader associated with our parent
     * Container (if any); otherwise, return <code>null</code>.
     */
    Loader getLoader();

    /**
     * Set the Loader with which this Container is associated.
     *
     * @param loader The newly associated loader
     */
    void setLoader(Loader loader);

    /**
     * Set a name string (suitable for use by humans) that describes this
     * Container.  Within the set of child containers belonging to a particular
     * parent, Container names must be unique.
     *
     * @param name New name of this container
     *
     */
    void setName(String name);

    /**
     * Return a name string (suitable for use by humans) that describes this
     * Container.  Within the set of child containers belonging to a particular
     * parent, Container names must be unique.
     */
    String getName();

    /**
     * Return the Container for which this Container is a child, if there is
     * one.  If there is no defined parent, return <code>null</code>.
     */
    Container getParent();

    /**
     * Set the parent Container to which this Container is being added as a
     * child.  This Container may refuse to become attached to the specified
     * Container by throwing an exception.
     *
     * @param container Container to which this Container is being added
     *  as a child
     *
     */
    void setParent(Container container);

    /**
     * Add a new child Container to those associated with this Container,
     * if supported.  Prior to adding this Container to the set of children,
     * the child's <code>setParent()</code> method must be called, with this
     * Container as an argument.  This method may thrown an
     * <code>IllegalArgumentException</code> if this Container chooses not
     * to be attached to the specified Container, in which case it is not added
     *
     * @param child New child Container to be added
     */
    void addChild(Container child);

    /**
     * Return the child Container, associated with this Container, with
     * the specified name (if any); otherwise, return <code>null</code>
     *
     * @param name Name of the child Container to be retrieved
     */
    Container findChild(String name);

    /**
     * Return the set of children Containers associated with this Container.
     * If this Container has no children, a zero-length array is returned.
     */
    Container[] findChildren();

    /**
     * Remove an existing child Container from association with this parent
     * Container.
     *
     * @param child Existing child Container to be removed
     */
    void removeChild(Container child);

    /**
     * Return descriptive information about this Container implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    String getInfo();

}
