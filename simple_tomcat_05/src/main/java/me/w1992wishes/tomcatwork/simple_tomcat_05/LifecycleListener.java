package me.w1992wishes.tomcatwork.simple_tomcat_05;

public interface LifecycleListener {

    /**
     * Acknowledge the occurrence of the specified event.
     *
     * @param event LifecycleEvent that has occurred
     */
    void lifecycleEvent(LifecycleEvent event);

}