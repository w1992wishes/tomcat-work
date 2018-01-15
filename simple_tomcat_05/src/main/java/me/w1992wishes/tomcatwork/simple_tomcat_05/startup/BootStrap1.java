package me.w1992wishes.tomcatwork.simple_tomcat_05.startup;

import me.w1992wishes.tomcatwork.simple_tomcat_05.core.SimpleLoader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.core.SimpleWrapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.ClientIPLoggerValve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.HeaderLoggerValve;
import org.apache.catalina.Loader;
import org.apache.catalina.Pipeline;
import org.apache.catalina.Valve;
import org.apache.catalina.Wrapper;
import org.apache.catalina.connector.http.HttpConnector;

/**
 * Created by wanqinfeng on 2017/2/19.
 */
public final class BootStrap1 {
    public static void main(String[] args) {
        HttpConnector connector = new HttpConnector();
        Wrapper wrapper = new SimpleWrapper();
        wrapper.setServletClass("ModernServlet");
        Loader loader = new SimpleLoader();
        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();

        wrapper.setLoader(loader);
        ((Pipeline)wrapper).addValve(valve1);
        ((Pipeline)wrapper).addValve(valve2);

        connector.setContainer(wrapper);

        try {
            connector.initialize();//open serverSocket
            connector.start();//wait connection and process

            //make the application wait until we pass a key
            System.in.read();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
