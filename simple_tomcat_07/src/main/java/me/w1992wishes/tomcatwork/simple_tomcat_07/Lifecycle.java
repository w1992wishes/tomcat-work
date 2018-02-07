package me.w1992wishes.tomcatwork.simple_tomcat_07;

import me.w1992wishes.tomcatwork.simple_tomcat_07.exception.LifecycleException;

public interface Lifecycle {

    // ----------------------------------------------------- Manifest Constants

    String START_EVENT = "start";

    String BEFORE_START_EVENT = "before_start";

    String AFTER_START_EVENT = "after_start";

    String STOP_EVENT = "stop";

    String BEFORE_STOP_EVENT = "before_stop";

    String AFTER_STOP_EVENT = "after_stop";

    // --------------------------------------------------------- Public Methods

    void start() throws LifecycleException;

    void stop() throws LifecycleException;

    void addLifecycleListener(LifecycleListener listener);

    LifecycleListener[] findLifecycleListeners();

    void removeLifecycleListener(LifecycleListener listener);

}
