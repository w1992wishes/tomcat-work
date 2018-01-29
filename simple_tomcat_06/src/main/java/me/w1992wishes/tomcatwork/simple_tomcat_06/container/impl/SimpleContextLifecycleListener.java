package me.w1992wishes.tomcatwork.simple_tomcat_06.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_06.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_06.LifecycleEvent;
import me.w1992wishes.tomcatwork.simple_tomcat_06.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by w1992wishes
 * on 2018/1/29.
 */
public class SimpleContextLifecycleListener implements LifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleContextLifecycleListener.class);

    @Override
    public void lifecycleEvent(LifecycleEvent event) {

        Lifecycle lifecycle = event.getLifecycle();
        LOGGER.info("SimpleContextLifecycleListener's event {}", event.getType());
        if (lifecycle.START_EVENT.equals(event.getType())){
            LOGGER.info("starting context ...");
        } else if (lifecycle.STOP_EVENT.equals(event.getType())){
            LOGGER.info("stopping context ...");
        }

    }

}
