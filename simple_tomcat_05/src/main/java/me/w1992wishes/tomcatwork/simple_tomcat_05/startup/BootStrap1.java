package me.w1992wishes.tomcatwork.simple_tomcat_05.startup;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpConnector;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Pipeline;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Wrapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleLoader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleWrapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.ClientIPLoggerValve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.HeaderLoggerValve;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public final class BootStrap1 {
    public static void main(String[] args) {

        Loader loader = new SimpleLoader();

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();

        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("PrimitiveServlet");
        wrapper.setLoader(loader);
        ((Pipeline)wrapper).addValve(valve1);
        ((Pipeline)wrapper).addValve(valve2);

        HttpConnector connector = new HttpConnector();
        connector.setContainer(wrapper);
        try {
            //open serverSocket
            connector.initialize();
            //wait connection and process
            connector.start();

            //make the application wait until we pass a key
            System.in.read();
        }catch (Exception e){
            throw new RuntimeException();
        }
    }
}
