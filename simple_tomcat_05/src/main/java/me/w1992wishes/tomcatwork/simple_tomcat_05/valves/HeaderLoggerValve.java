package me.w1992wishes.tomcatwork.simple_tomcat_05.valves;

import org.apache.catalina.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

/**
 * 将请求头信息输出到控制台
 * Created by wanqinfeng on 2017/2/19.
 */
public class HeaderLoggerValve implements Valve, Contained{

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
        System.out.println("Header Logger Valve");
        ServletRequest sreq = request.getRequest();
        if (sreq instanceof HttpServletRequest){
            HttpServletRequest hreq = (HttpServletRequest)sreq;
            Enumeration headerNames = hreq.getHeaderNames();
            while (headerNames.hasMoreElements()){
                String headerName = headerNames.nextElement().toString();
                String headerValue = hreq.getHeader(headerName);
                System.out.println(headerName + " : " + headerValue);
            }
        }else {
            System.out.println("Not an Http Request");
        }

        System.out.println("----------------------------------");
    }
}