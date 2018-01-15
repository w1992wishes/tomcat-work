package me.w1992wishes.tomcatwork.simple_tomcat_09.core;

import org.apache.catalina.*;

/**
 * Created by wanqinfeng on 2017/3/6.
 */
public class SimpleContextConfig implements LifecycleListener {
    public void lifecycleEvent(LifecycleEvent event) {
        if (Lifecycle.START_EVENT.equals(event.getType())) {
            Context context = (Context) event.getLifecycle();
            context.setConfigured(true);
        }
    }
}
