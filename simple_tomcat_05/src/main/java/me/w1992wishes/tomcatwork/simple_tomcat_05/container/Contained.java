package me.w1992wishes.tomcatwork.simple_tomcat_05.container;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface Contained {

    /**
     * Return the <code>Container</code> with which this instance is associated
     * (if any); otherwise return <code>null</code>.
     */
    Container getContainer();

    /**
     * Set the <code>Container</code> with which this instance is associated.
     *
     * @param container The Container instance with which this instance is to
     *  be associated, or <code>null</code> to disassociate this instance
     *  from any Container
     */
    void setContainer(Container container);

}
