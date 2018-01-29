package me.w1992wishes.tomcatwork.simple_tomcat_06.container;

import me.w1992wishes.tomcatwork.simple_tomcat_06.ContainerListener;
import me.w1992wishes.tomcatwork.simple_tomcat_06.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_06.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_06.connector.http.HttpResponse;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2018/1/20.
 */
public interface Container {

    String ADD_CHILD_EVENT = "addChild";

    String ADD_VALVE_EVENT = "addValve";

    String REMOVE_CHILD_EVENT = "removeChild";

    String REMOVE_VALVE_EVENT = "removeValve";

    String ADD_MAPPER_EVENT = "addMapper";

    /**
     * Process the specified Request, and generate the corresponding Response,
     * according to the design of this particular Container.
     *
     */
    void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException;

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
     * Return descriptive information about this Container implementation and
     * the corresponding version number, in the format
     * <code>&lt;description&gt;/&lt;version&gt;</code>.
     */
    String getInfo();

    Container getParent();

    void setParent(Container container);

    void addChild(Container child);

    Container findChild(String name);

    Container[] findChildren();

    void removeChild(Container child);

    void addMapper(Mapper mapper);

    Mapper findMapper(String protocol);

    Mapper[] findMappers();

    /**
     * Return the child Container that should be used to process this Request,
     * based upon its characteristics.  If no such child Container can be
     * identified, return <code>null</code> instead.
     *
     * @param request Request being processed
     * @param update Update the Request to reflect the mapping selection?
     */
    Container map(HttpRequest request, boolean update);

    void addContainerListener(ContainerListener listener);

    ContainerListener[] findContainerListeners();

    void removeContainerListener(ContainerListener listener);

}
