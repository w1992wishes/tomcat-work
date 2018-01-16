import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.io.PrintWriter;

public class PrimitiveServlet implements Servlet{

    private Logger LOGGER = LoggerFactory.getLogger(PrimitiveServlet.class);

    public void init(ServletConfig config) throws ServletException{
        LOGGER.info("init");
    }
    public void service(ServletRequest request, ServletResponse response) throws IOException {

        LOGGER.info("servlet {} start", getClass().getSimpleName());

        PrintWriter out = response.getWriter();
        out.println("hello, roses are red");
        out.println("------------------------");
        out.println("violets are blue");

        LOGGER.info("servlet {} start", getClass().getSimpleName());

    }
    public void destroy(){
        LOGGER.info("destroy");
    }
    public String getServletInfo(){
        return null;
    }
    public ServletConfig getServletConfig(){
        return null;
    }
}