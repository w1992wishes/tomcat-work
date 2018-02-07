package me.w1992wishes.tomcatwork.simple_tomcat_07;

import me.w1992wishes.tomcatwork.simple_tomcat_07.container.Container;

import java.util.EventObject;

/**
 * Created by w1992wishes
 * on 2018/1/25.
 */
public class ContainerEvent extends EventObject {

    /**
     * The Container on which this event occurred.
     */
    private Container container = null;

    /**
     * The event data associated with this event.
     */
    private Object data = null;

    /**
     * The event type this instance represents.
     */
    private String type = null;

    /**
     * Construct a new ContainerEvent with the specified parameters.
     *
     * @param container Container on which this event occurred
     * @param type Event type
     * @param data Event data
     */
    public ContainerEvent(Container container, String type, Object data) {
        super(container);
        this.container = container;
        this.type = type;
        this.data = data;
    }

    /**
     * Return the event data of this event.
     */
    public Object getData() {
        return (this.data);
    }

    /**
     * Return the Container on which this event occurred.
     */
    public Container getContainer() {
        return (this.container);
    }


    /**
     * Return the event type of this event.
     */
    public String getType() {
        return (this.type);
    }

    /**
     * Return a string representation of this event.
     */
    public String toString() {
        return ("ContainerEvent['" + getContainer() + "','" + getType() + "','" + getData() + "']");
    }

}
