package me.w1992wishes.tomcatwork.simple_tomcat_05.valves;

import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpRequest;
import me.w1992wishes.tomcatwork.simple_tomcat_05.connector.http.HttpResponse;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Contained;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Container;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.Valve;
import me.w1992wishes.tomcatwork.simple_tomcat_05.container.ValveContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 将请求头信息输出到控制台
 * Created by wanqinfeng on 2017/2/19.
 */
public class HeaderLoggerValve implements Valve, Contained {

    private static final Logger LOGGER = LoggerFactory.getLogger(HeaderLoggerValve.class);

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
        return "Header Logger Valve";
    }

    @Override
    public void invoke(HttpRequest request, HttpResponse response, ValveContext valveContext){

        //pass this request on to the next valve in our pipeline
        valveContext.invokeNext(request, response);

        LOGGER.info("Header Logger Valve");

        ServletRequest sreq = request.getRequest();
        if (sreq instanceof HttpServletRequest){
            HttpServletRequest hreq = (HttpServletRequest)sreq;
            Enumeration headerNames = hreq.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement().toString();
                String headerValue = hreq.getHeader(headerName);
                LOGGER.info(headerName + " : " + headerValue);
            }
        }else {
            LOGGER.error("Not an Http Request");
        }
        LOGGER.info("---------------Header Logger Valve End----------------------");

    }
}
