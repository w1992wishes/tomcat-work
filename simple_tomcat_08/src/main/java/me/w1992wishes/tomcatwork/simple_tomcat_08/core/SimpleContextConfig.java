package me.w1992wishes.tomcatwork.simple_tomcat_08.core;

import org.apache.catalina.Context;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

/**
 * Created by wanqinfeng on 2017/3/1.
 */
public class SimpleContextConfig implements LifecycleListener {

    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            Context context = (Context) event.getLifecycle();
            context.setConfigured(true);
        }
    }
}
