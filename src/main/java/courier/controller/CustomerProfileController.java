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
import procourier.model.Customer;
import courier.dao.ProcourierDAO;

@WebServlet("/CustomerProfileController")
public class CustomerProfileController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);

        
      //Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        Integer customerId = (Integer) request.getSession().getAttribute("customerId");
        
        if (customerId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Customer_profileImage FROM customer WHERE Customer_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, customerId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profileImagePath = rs.getString("Customer_profileImage");
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
        System.out.println("Session ID in CustomerProfileController: " + (session != null ? session.getId() : "No session"));
        System.out.println("Customer ID from session in CustomerProfileController: " + (session != null ? session.getAttribute("customerId") : "No customerId"));

        if (session == null || session.getAttribute("customerId") == null) {
            // Redirect to login if session is not found or courierId is not set
            response.sendRedirect("userlogin.jsp");
            return;
        }


        try {
            ProcourierDAO dao = new ProcourierDAO();
            Customer customer = dao.getCustomerById(customerId);

            if (customer == null) {
                // Redirect to error page if courier is not found
                response.sendRedirect("error.jsp");
                return;
            }

            // Set courier object as request attribute
            request.setAttribute("customer", customer);
            // Forward request to the profile view page
            request.getRequestDispatcher("ViewCustomerProfile.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // Redirect to error page in case of any exception
            response.sendRedirect("error.jsp");
        }
    }
}

