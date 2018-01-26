package me.w1992wishes.tomcatwork.simple_tomcat_05.util;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Lifecycle;
import me.w1992wishes.tomcatwork.simple_tomcat_05.LifecycleEvent;
import me.w1992wishes.tomcatwork.simple_tomcat_05.LifecycleListener;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public final class LifecycleSupport {

    public LifecycleSupport(Lifecycle lifecycle) {
        super();
        this.lifecycle = lifecycle;
    }

    /**
     * The source component for lifecycle events that we will fire.
     */
    private Lifecycle lifecycle = null;

    private LifecycleListener listeners[] = new LifecycleListener[0];

    public void addLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            LifecycleListener results[] = new LifecycleListener[listeners.length + 1];
            for (int i = 0; i < listeners.length; i++)
                results[i] = listeners[i];
            results[listeners.length] = listener;
            listeners = results;
        }
    }

    public LifecycleListener[] findLifecycleListeners() {
        return listeners;
    }

    public void fireLifecycleEvent(String type, Object data) {
        LifecycleEvent event = new LifecycleEvent(lifecycle, type, data);
        LifecycleListener interested[] = null;
        synchronized (listeners) {
            interested = (LifecycleListener[]) listeners.clone();
        }
        for (int i = 0; i < interested.length; i++)
            interested[i].lifecycleEvent(event);
    }

    public void removeLifecycleListener(LifecycleListener listener) {
        synchronized (listeners) {
            int n = -1;
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] == listener) {
                    n = i;
                    break;
                }
            }
            if (n < 0)
                return;
            LifecycleListener results[] = new LifecycleListener[listeners.length - 1];
            int j = 0;
            for (int i = 0; i < listeners.length; i++) {
                if (i != n)
                    results[j++] = listeners[i];
            }
            listeners = results;
        }
    }

}
