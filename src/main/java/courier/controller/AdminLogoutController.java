package courier.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/AdminLogoutController")
public class AdminLogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve the current session, if exists
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate(); // Invalidate the session
        }

        // Redirect to login page with a logout message
        response.sendRedirect("adminlogin.jsp?message=logged_out");
    }
}
