package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import connection.ConnectionManager;

@WebServlet("/AdminLoginController")
public class AdminLoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
    	// Clear any existing session
        HttpSession session = request.getSession(false); // Get the current session, if it exists
        if (session != null) {
            session.invalidate(); // Invalidate the old session
        }
        session = request.getSession(true); // Create a new session for the admin
    	
    	
    	String email = request.getParameter("adminEmail");
        String password = request.getParameter("adminPassword");

        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "SELECT * FROM admin WHERE Admin_email  = ? AND Admin_password  = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int adminId = rs.getInt("Admin_ID");
                session.setAttribute("adminId", adminId);
                session.setAttribute("adminName", rs.getString("Admin_name"));
                session.setAttribute("adminEmail", email);
                response.sendRedirect("adminHome.jsp");
                System.out.print("Admin ID: " + adminId);
            } else {
                request.setAttribute("errorMessage", "Invalid username or password.");
                System.out.print("Tak keluar pape pon");
                request.getRequestDispatcher("adminlogin.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }
}
