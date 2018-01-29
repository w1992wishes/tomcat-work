package me.w1992wishes.tomcatwork.simple_tomcat_05.startup;

import me.w1992wishes.tomcatwork.simple_tomcat_05.Loader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpConnector;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.*;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleContext;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleContextMapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleLoader;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.impl.SimpleWrapper;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.ClientIPLoggerValve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.valves.HeaderLoggerValve;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wanqinfeng on 2017/2/22.
 */
public class BootStrap2 {

    private static final Logger LOGGER = LoggerFactory.getLogger(BootStrap2.class);

    public static void main(String[] args) {

        Context context = new SimpleContext();

        Loader loader = new SimpleLoader();
        context.setLoader(loader);

        Valve valve1 = new HeaderLoggerValve();
        Valve valve2 = new ClientIPLoggerValve();
        ((Pipeline) context).addValve(valve1);
        ((Pipeline) context).addValve(valve2);

        Wrapper wrapper1 = new SimpleWrapper();
        wrapper1.setName("Primitive");
        wrapper1.setServletClass("PrimitiveServlet");
        Wrapper wrapper2 = new SimpleWrapper();
        wrapper2.setName("Modern");
        wrapper2.setServletClass("ModernServlet");
        context.addChild(wrapper1);
        context.addChild(wrapper2);

        Mapper mapper = new SimpleContextMapper();
        mapper.setProtocol("http");
        context.addMapper(mapper);

        // context.addServletMapping(pattern, name);
        context.addServletMapping("/Primitive", "Primitive");
        context.addServletMapping("/Modern", "Modern");

        HttpConnector connector = new HttpConnector();
        connector.setContainer(context);

        try {
            connector.initialize();
            connector.start();

            // make the application wait until we press a key.
            System.in.read();
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RuntimeException();
        }
    }

}
