package me.w1992wishes.tomcatwork.simple_tomcat_06.container;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Wrapper extends Container{

    /**
     * Load and initialize an instance of this servlet, if there is not already
     * at least one initialized instance.  This can be used, for example, to
     * load servlets that are marked in the deployment descriptor to be loaded
     * at server startup time.
     *
     * @exception ServletException if the servlet init() method threw
     *  an exception
     * @exception ServletException if some other loading problem occurs
     */
    void load() throws ServletException;

    /**
     * Allocate an initialized instance of this Servlet that is ready to have
     * its <code>service()</code> method called.  If the servlet class does
     * not implement <code>SingleThreadModel</code>, the (only) initialized
     * instance may be returned immediately.  If the servlet class implements
     * <code>SingleThreadModel</code>, the Wrapper implementation must ensure
     * that this instance is not allocated again until it is deallocated by a
     * call to <code>deallocate()</code>.
     *
     * @exception ServletException if the servlet init() method threw
     *  an exception
     * @exception ServletException if a loading error occurs
     */
    Servlet allocate() throws ServletException;

    /**
     * Return the fully qualified servlet class name for this servlet.
     */
    String getServletClass();

    /**
     * Set the fully qualified servlet class name for this servlet.
     *
     * @param servletClass Servlet class name
     */
    void setServletClass(String servletClass);

}
