package me.w1992wishes.tomcatwork.simple_tomcat_05;

import java.util.EventObject;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public final class LifecycleEvent extends EventObject {

    /**
     * Construct a new LifecycleEvent with the specified parameters.
     *
     * @param lifecycle Component on which this event occurred
     * @param type Event type (required)
     */
    public LifecycleEvent(Lifecycle lifecycle, String type) {
        this(lifecycle, type, null);
    }

    /**
     * Construct a new LifecycleEvent with the specified parameters.
     *
     * @param lifecycle Component on which this event occurred
     * @param type Event type (required)
     * @param data Event data (if any)
     */
    public LifecycleEvent(Lifecycle lifecycle, String type, Object data) {
        super(lifecycle);
        this.lifecycle = lifecycle;
        this.type = type;
        this.data = data;
    }

    /**
     * The event data associated with this event.
     */
    private Object data = null;

    /**
     * The Lifecycle on which this event occurred.
     */
    private Lifecycle lifecycle = null;

    /**
     * The event type this instance represents.
     */
    private String type = null;

    /**
     * Return the event data of this event.
     */
    public Object getData() {
        return (this.data);
    }

    /**
     * Return the Lifecycle on which this event occurred.
     */
    public Lifecycle getLifecycle() {
        return (this.lifecycle);
    }

    /**
     * Return the event type of this event.
     */
    public String getType() {
        return (this.type);
    }

}
