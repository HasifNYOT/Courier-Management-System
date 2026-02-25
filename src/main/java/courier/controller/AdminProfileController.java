package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import connection.ConnectionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import procourier.model.Admin;
import courier.dao.ProcourierDAO;

@WebServlet("/AdminProfileController")
public class AdminProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        
      //Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        Integer adminId = (Integer) request.getSession().getAttribute("adminId");
        
        if (adminId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Admin_profileImage FROM admin WHERE Admin_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, adminId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profileImagePath = rs.getString("Admin_profileImage");
                    if (profileImagePath == null || profileImagePath.isEmpty()) {
                        profileImagePath = "images/default-profile.png"; // Fallback if no image in database
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Add profile image path to request scope
        request.setAttribute("profileImagePath", profileImagePath);
        System.out.println("Profile Image Path: " + profileImagePath);
        
       
        // Debugging session information
        System.out.println("Session ID in AdminProfileController: " + (session != null ? session.getId() : "No session"));
        System.out.println("Admin ID from session in AdminProfileController: " + (session != null ? session.getAttribute("adminId") : "No adminId"));

        if (session == null || session.getAttribute("adminId") == null) {
            // Redirect to login if session is not found or courierId is not set
            response.sendRedirect("adminlogin.jsp");
            return;
        }


        try {
            ProcourierDAO dao = new ProcourierDAO();
            Admin admin = dao.getAdminById(adminId);

            if (admin == null) {
                // Redirect to error page if courier is not found
                response.sendRedirect("error.jsp");
                return;
            }

            // Set courier object as request attribute
            request.setAttribute("admin", admin);
            // Forward request to the profile view page
            request.getRequestDispatcher("ViewAdminProfile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to error page in case of any exception
            response.sendRedirect("error.jsp");
        }
    }
}

