package me.w1992wishes.tomcatwork.simple_tomcat_10.core;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/3/7.
 */
public class SimplePipeline implements Pipeline, Lifecycle {

    public SimplePipeline(Container container) {
        setContainer(container);
    }

    // The basic Valve (if any) associated with this Pipeline.
    protected Valve basic = null;
    // The Container with which this Pipeline is associated.
    protected Container container = null;
    // the array of Valves
    protected Valve valves[] = new Valve[0];

    public void setContainer(Container container) {
        this.container = container;
    }

    public Valve getBasic() {
        return basic;
    }

    public void setBasic(Valve valve) {
        this.basic = valve;
        ((Contained) valve).setContainer(container);
    }

    public void addValve(Valve valve) {
        if (valve instanceof Contained)
            ((Contained) valve).setContainer(this.container);

        synchronized (valves) {
            Valve results[] = new Valve[valves.length +1];
            System.arraycopy(valves, 0, results, 0, valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    public Valve[] getValves() {
        return valves;
    }

    public void invoke(Request request, Response response)
            throws IOException, ServletException {
        // Invoke the first Valve in this pipeline for this request
        (new StandardPipelineValveContext()).invokeNext(request, response);
    }

    public void removeValve(Valve valve) {
    }

    // implementation of the Lifecycle interface's methods
    public void addLifecycleListener(LifecycleListener listener) {
    }

    public LifecycleListener[] findLifecycleListeners() {
        return null;
    }

    public void removeLifecycleListener(LifecycleListener listener) {
    }

    public synchronized void start() throws LifecycleException {
    }

    public void stop() throws LifecycleException {
    }

    // this class is copied from org.apache.catalina.core.StandardPipeline class's
    // StandardPipelineValveContext inner class.
    protected class StandardPipelineValveContext implements ValveContext {
        protected int stage = 0;
        public String getInfo() {
            return null;
        }

        public void invokeNext(Request request, Response response)
                throws IOException, ServletException {
            int subscript = stage;
            stage = stage + 1;
            // Invoke the requested Valve for the current request thread
            if (subscript < valves.length) {
                valves[subscript].invoke(request, response, this);
            }
            else if ((subscript == valves.length) && (basic != null)) {
                basic.invoke(request, response, this);
            }
            else {
                throw new ServletException("No valve");
            }
        }
    } // end of inner class

}
