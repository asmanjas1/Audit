package servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import beans.ActionType;
import beans.UserCredentials;
import services.UserAuditServiceImpl;
import services.UserCredentialsServiceImpl;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static Logger logger = LoggerFactory.getLogger(LoginServlet.class);
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException, IOException {

        // get request parameters for userID and password
        String user = request.getParameter("user");
        if (user == null || user.isEmpty()) {
            failureResponse(request, response, "please enter username");
            return;
        }
        String pwd = request.getParameter("pwd");
        if (pwd == null || pwd.isEmpty()) {
            failureResponse(request, response, "please enter password");
            return;
        }
        try {
            UserCredentials credentials = new UserCredentials(user, pwd);
            Boolean isAuthorised = UserCredentialsServiceImpl.getInstance().validateUserCred(credentials);
            if (isAuthorised) {
                UserAuditServiceImpl userAuditService = UserAuditServiceImpl.getInstance();
                userAuditService.saveAuditAction(user, ActionType.LOGGED_IN.name());
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                //setting session to expiry in 30 mins
                session.setMaxInactiveInterval(30 * 60);
                Cookie userName = new Cookie("user", user);
                userName.setMaxAge(30 * 60);
                response.addCookie(userName);
                response.sendRedirect("LoginSuccess.jsp");
            } else {
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
                PrintWriter out = response.getWriter();
                out.println("<font color=red>Either user name or password is wrong.</font>");
                rd.include(request, response);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            failureResponse(request, response, e.getMessage());
        }


    }

    private void failureResponse(HttpServletRequest request, HttpServletResponse response, String userMessage) throws ServletException, IOException {
        RequestDispatcher rd = getServletContext().getRequestDispatcher("/Login.html");
        PrintWriter out = response.getWriter();
        out.println("<font color=red>" + userMessage + " </font>");
        rd.include(request, response);
    }

}
