package me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl;

import me.w1992wishes.tomcatwork.simple_tomcat_05.container.*;
import org.apache.catalina.*;
import org.apache.catalina.Container;
import org.apache.catalina.Valve;
import org.apache.catalina.ValveContext;

import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public class SimplePipeline implements me.w1992wishes.tomcatwork.simple_tomcat_05.container.Pipeline {

    public SimplePipeline(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container){
        setContainer(container);
    }

    //the basic Valve associated with this Pipeline
    protected me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve basic = null;
    //The Container with which this Pipeline is associated
    protected me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container;
    //the array of Valves
    protected me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valves[] = new me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve[0];

    public void setContainer(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container container) {
        this.container = container;
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve getBasic() {
        return basic;
    }

    @Override
    public void setBasic(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {
        this.basic = valve;
        ((Contained)valve).setContainer(container);
    }

    @Override
    public void addValve(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {
        if (valve instanceof Contained)
            ((Contained)valve).setContainer(this.container);

        synchronized (valves){
            me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve results[] = new me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve[valves.length + 1];
            System.arraycopy(valves,0,results,0,valves.length);
            results[valves.length] = valve;
            valves = results;
        }
    }

    @Override
    public me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve[] getValves() {
        return valves;
    }

    @Override
    public void invoke(Request request, Response response) throws IOException, ServletException {
        //invoke the first Vlave in this pipeline for the request
        (new SimplePipelineValveContext()).invokeNext(request, response);
    }

    @Override
    public void removeValve(me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve valve) {

    }

    //this class is copied from org.apache.catalina.core.StandardPipeline
    //class's StandardPipelineValveContext inner class
    protected  class SimplePipelineValveContext implements me.w1992wishes.tomcatwork.simple_tomcat_05.container.ValveContext {

        protected int stage = 0;

        @Override
        public String getInfo() {
            return null;
        }

        @Override
        public void invokeNext(Request request, Response response) throws IOException, ServletException {
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
