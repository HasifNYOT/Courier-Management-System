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
import procourier.model.Courier;
import courier.dao.ProcourierDAO;

@WebServlet("/CourierProfileController")
public class CourierProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        
      //Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        Integer courierId = (Integer) request.getSession().getAttribute("courierId");
        
        if (courierId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Courier_profileImage FROM courier WHERE Courier_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courierId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profileImagePath = rs.getString("Courier_profileImage");
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
        System.out.println("Session ID in CourierProfileController: " + (session != null ? session.getId() : "No session"));
        System.out.println("Courier ID from session in CourierProfileController: " + (session != null ? session.getAttribute("courierId") : "No courierId"));

        if (session == null || session.getAttribute("courierId") == null) {
            // Redirect to login if session is not found or courierId is not set
            response.sendRedirect("userlogin.jsp");
            return;
        }


        try {
            ProcourierDAO dao = new ProcourierDAO();
            Courier courier = dao.getCourierById(courierId);

            if (courier == null) {
                // Redirect to error page if courier is not found
                response.sendRedirect("error.jsp");
                return;
            }

            // Set courier object as request attribute
            request.setAttribute("courier", courier);
            // Forward request to the profile view page
            request.getRequestDispatcher("ViewCourierProfile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to error page in case of any exception
            response.sendRedirect("error.jsp");
        }
    }
}

