package me.w1992wishes.tomcatwork.simple_tomcat_07.container;

import me.w1992wishes.tomcatwork.simple_tomcat_07.connector.http.HttpRequest;

/**
 * Interface defining methods that a parent Container may implement to select
 * a subordinate Container to process a particular Request, optionally
 * modifying the properties of the Request to reflect the selections made.
 * <p>
 * A typical Container may be associated with a single Mapper that processes
 * all requests to that Container, or a Mapper per request protocol that allows
 * the same Container to support multiple protocols at once.
 *
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Mapper {

    Container getContainer();

    void setContainer(Container container);

    String getProtocol();

    void setProtocol(String protocol);

    /**
     * Return the child Container that should be used to process this Request,
     * based upon its characteristics.  If no such child Container can be
     * identified, return <code>null</code> instead.
     *
     * @param request Request being processed
     * @param update Update the Request to reflect the mapping selection?
     */
    Container map(HttpRequest request, boolean update);

}
