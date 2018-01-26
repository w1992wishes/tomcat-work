package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.*;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimplePipeline implements Pipeline {

    public SimplePipeline(Container container){
        setContainer(container);
    }

    //the basic Valve associated with this Pipeline
    protected Valve basic = null;
    //The Container with which this Pipeline is associated
    protected Container container;
    //the array of Valves
    protected Valve valves[] = new Valve[0];

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public Valve getBasic() {
        return basic;
    }

    @Override
    public void setBasic(Valve valve) {
        this.basic = valve;
        ((Contained)valve).setContainer(container);
    }

    @Override
    public void addValve(Valve valve) {
        if (valve instanceof Contained)
            ((Contained)valve).setContainer(this.container);

        synchronized (valves){
            Valve results[] = new Valve[valves.length + 1];
            System.arraycopy(valves,0,results,0,valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    @Override
    public Valve[] getValves() {
        return valves;
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response) throws ServletException, IOException {
        //invoke the first Vlave in this pipeline for the request
        (new SimplePipelineValveContext()).invokeNext(request, response);
    }

    @Override
    public void removeValve(Valve valve) {

    }

    //this class is copied from org.apache.catalina.core.StandardPipeline
    //class's StandardPipelineValveContext inner class
    protected  class SimplePipelineValveContext implements ValveContext {

        protected int stage = 0;

        @Override
        public String getInfo() {
            return "Simple ValveContext For SimplePipeline";
        }

        @Override
        public void invokeNext(HttpRequest request, HttpResponse response) throws ServletException, IOException {
            int subscript = stage;
            stage = stage + 1;
            //invoke the requested Valve for the current request thread
            if (subscript < valves.length){
                valves[subscript].invoke(request,response,this);
            }else if ((subscript == valves.length) && (basic != null)){
                basic.invoke(request, response, this);
            }else {
                throw  new ServletException("No valve");
            }
        }
    }// end of inner class

}
