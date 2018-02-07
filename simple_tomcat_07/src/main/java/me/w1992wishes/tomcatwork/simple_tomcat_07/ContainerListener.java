package me.w1992wishes.tomcatwork.simple_tomcat_07;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public interface ContainerListener {

    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event ContainerEvent that has occurred
     */
    void containerEvent(ContainerEvent event);

}
