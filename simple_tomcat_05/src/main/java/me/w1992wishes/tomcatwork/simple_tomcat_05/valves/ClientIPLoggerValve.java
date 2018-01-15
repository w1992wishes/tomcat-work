package me.w1992wishes.tomcatwork.simple_tomcat_05.valves;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import java.io.IOException;

/**
 * 用来将客服端的ip地址输出到控制台上
 * Created by wanqinfeng on 2017/2/19.
 */
public class ClientIPLoggerValve implements Valve, Contained {

    protected Container container;

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
        return null;
    }

    @Override
    public void invoke(Request request, Response response, ValveContext valveContext) throws IOException, ServletException {
        //pass this request on to the next valve in our pipeline
        valveContext.invokeNext(request, response);
        System.out.println("Client IP Logger valve");
        ServletRequest sreq = request.getRequest();
        System.out.println(sreq.getRemoteAddr());
        System.out.println("-------------------------------------");
    }
}
