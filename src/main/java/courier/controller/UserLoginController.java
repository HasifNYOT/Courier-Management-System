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

@WebServlet("/UserLoginController")
public class UserLoginController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Clear any existing session
    	HttpSession session = request.getSession(false); // Get the current session, if it exists
        if (session != null) {
            session.invalidate(); // Invalidate the old session
        }
        session = request.getSession(true); // Create a new session for the user
    	
    	
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String userType = request.getParameter("userType");

        // Log user type
        System.out.println("User type: " + userType);

        try (Connection conn = ConnectionManager.getConnection()) {
            String sql;
            if ("courier".equals(userType)) {
                sql = "SELECT * FROM Courier WHERE Courier_email = ? AND Courier_password = ?";
            } else if ("customer".equals(userType)) {
                sql = "SELECT * FROM Customer WHERE Customer_email = ? AND Customer_password = ?";
            } else {
                response.getWriter().println("<script>alert('Invalid user type!'); window.history.back();</script>");
                return;
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            // Log SQL execution
            System.out.println("Executing query for email: " + email);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Retrieve common details
                int userId = rs.getInt(userType.equals("courier") ? "Courier_ID" : "Customer_ID");
                String userName = rs.getString(userType.equals("courier") ? "Courier_name" : "Customer_name");

                // Log successful login
                System.out.println("Login successful for " + userType + " ID: " + userId);

                // Set session attributes
                session.setAttribute(userType + "Id", userId);
                session.setAttribute(userType + "Email", email);
                session.setAttribute(userType + "Name", userName);

                // Redirect to appropriate home page
                if ("courier".equals(userType)) {
                    response.sendRedirect("courierHome.jsp");
                } else {
                    response.sendRedirect("customerHome.jsp");
                }
            } else {
                // Login failed
            	request.setAttribute("errorMessage", "Invalid email or password.");
            	request.getRequestDispatcher("userlogin.jsp").forward(request, response);
            	
            	
            	
                //System.out.println("Invalid credentials for email: " + email);
                //response.getWriter().println("<script>alert('Wrong email or password!'); window.history.back();</script>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}