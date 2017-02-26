package com.wan.tomcat.core;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/2/25.
 */
public class SimplePipeline implements Pipeline, Lifecycle {

    public SimplePipeline(Container container) {
        setContainer(container);
    }

    //The basic valve associated with the pipeline
    protected Valve basic;
    //The Container with which this pipeline is associated
    protected Container container;
    //the array of valves
    protected Valve valves[] = new Valve[0];

    public void setContainer(Container container) {
        this.container = container;
    }

    public void setBasic(Valve valve) {
        this.basic = valve;
        ((Contained) valve).setContainer(container);
    }

    public Valve getBasic() {
        return basic;
    }

    public void addValve(Valve valve) {
        if (valve instanceof Contained)
            ((Contained) valve).setContainer(this.container);
        synchronized (valves) {
            Valve[] results = new Valve[valves.length + 1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    public Valve[] getValves() {
        return valves;
    }

    public void addLifecycleListener(LifecycleListener lifecycleListener) {

    }

    public LifecycleListener[] findLifecycleListeners() {
        return new LifecycleListener[0];
    }

    public void removeLifecycleListener(LifecycleListener lifecycleListener) {

    }

    public void start() throws LifecycleException {

    }

    public void stop() throws LifecycleException {

    }

    public void invoke(Request request, Response response) throws IOException, ServletException {

    }

    public void removeValve(Valve valve) {

    }

    protected class StandardPipelineValveContext implements ValveContext {

        protected int stage = 0;

        public String getInfo() {
            return null;
        }

        public void invokeNext(Request request, Response response) throws IOException, ServletException {
            int subscript = stage;
            stage = stage + 1;

            if (subscript < valves.length)
                valves[subscript].invoke(request, response, this);
            else if ((subscript == valves.length) && (basic != null))
                basic.invoke(request, response, this);
            else
                throw new ServletException("No valve");
        }
    }
}
