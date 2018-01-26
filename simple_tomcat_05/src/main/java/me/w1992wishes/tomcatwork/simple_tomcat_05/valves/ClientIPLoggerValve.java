package me.w1992wishes.tomcatwork.simple_tomcat_05.valves;

import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Contained;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.ValveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * 用来将客服端的ip地址输出到控制台上
 * Created by wanqinfeng on 2017/2/19.
 */
public class ClientIPLoggerValve implements Valve, Contained {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientIPLoggerValve.class);

    private Container container;

    @Override
    public Container getContainer() {
        return container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    public String getInfo() {
        return "Client IP Logger Valve";
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext) throws ServletException, IOException {

        //pass this request on to the next valve in our pipeline
        valveContext.invokeNext(request, response);

        LOGGER.info("Client IP Logger valve");

        ServletRequest sreq = request.getRequest();

        LOGGER.info(sreq.getRemoteAddr());

        LOGGER.info("-----------------Client IP Logger valve End--------------------");
    }
}
