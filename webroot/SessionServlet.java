import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wanqinfeng on 2017/3/6.
 */
public class SessionServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionServlet.class);

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LOGGER.info("servlet {} start", getClass().getSimpleName());

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head><title>SessionServlet</title></head>");
        out.println("<body>");
        String value = request.getParameter("value");
        HttpSession session = request.getSession(true);
        out.println("<br>the previous value is " +
                (String) session.getAttribute("value"));
        out.println("<br>the current value is " + value);
        session.setAttribute("value", value);
        out.println("<br><hr>");
        out.println("<form>");
        out.println("New Value: <input name=value>");
        out.println("<input type=submit>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");

        LOGGER.info("servlet {} end", getClass().getSimpleName());
    }
}
