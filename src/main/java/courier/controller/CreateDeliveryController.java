package courier.controller;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import connection.ConnectionManager;

@WebServlet("/CreateDeliveryController")
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
                 maxFileSize = 1024 * 1024 * 10,      // 10MB
                 maxRequestSize = 1024 * 1024 * 50)  // 50MB
public class CreateDeliveryController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Retrieve form data
            String receiverName = request.getParameter("receiverName");
            String weight = request.getParameter("weight");
            String dimension = request.getParameter("dimension");
            String contentDescription = request.getParameter("description");
            String dropOffLocation = request.getParameter("dropOffLocation");
            Part packageImage = request.getPart("packageImage");
            int customerId = Integer.parseInt(request.getParameter("customerId"));

            // Save image file
            String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();

            String fileName = new File(packageImage.getSubmittedFileName()).getName();
            String filePath = uploadPath + File.separator + fileName;
            packageImage.write(filePath);

            // Insert package into the database
            try (Connection conn = ConnectionManager.getConnection()) {
                String packageSql = "INSERT INTO Package (Customer_ID, Receiver_name, Weight, Dimension, Content_description, Package_Image_Path, Dropoff_location) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement packageStmt = conn.prepareStatement(packageSql);
                packageStmt.setInt(1, customerId);
                packageStmt.setString(2, receiverName);
                packageStmt.setString(3, weight);
                packageStmt.setString(4, dimension);
                packageStmt.setString(5, contentDescription);
                packageStmt.setString(6, fileName);  // Save the file name in the database
                packageStmt.setString(7, dropOffLocation);  // Insert drop-off location directly
                packageStmt.executeUpdate();

                // Redirect to success page with a success message
                response.sendRedirect("customerHome.jsp?message=packageCreated");
            }

        } catch (Exception e) {
            // Handle errors (optional: log the error or display an error message to the user)
            e.printStackTrace();
            response.sendRedirect("errorPage.html"); // Replace with your actual error page
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	//Fetch the profile image for the logged-in courier
    	Integer customerId = (Integer) request.getSession().getAttribute("customerId");
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        
        if (customerId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Customer_profileImage FROM customer WHERE customer_ID = ?";
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
    	
        request.getRequestDispatcher("CreateOrder.jsp").forward(request, response);
    }

}
