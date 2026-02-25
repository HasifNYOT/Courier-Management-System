package courier.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import procourier.model.Customer;
import connection.ConnectionManager;
import courier.dao.ProcourierDAO;

@WebServlet("/UpdateCustomerController")
@MultipartConfig(maxFileSize = 16177215) // Limit file size to ~16MB
public class UpdateCustomerController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Default profile image path
        String profileImagePath = "images/default-profile.png"; 
        Integer customerId = (Integer) request.getSession().getAttribute("customerId");

        // If customer is not logged in, redirect to login page
        if (customerId == null) {
            response.sendRedirect("userlogin.jsp");
            return;
        }

        // Fetch the profile image from the database
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "SELECT Customer_profileImage FROM customer WHERE Customer_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                profileImagePath = rs.getString("Customer_profileImage");
                if (profileImagePath == null || profileImagePath.isEmpty()) {
                    profileImagePath = "images/default-profile.png"; // Fallback if no image in DB
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fetch customer data
        ProcourierDAO dao = new ProcourierDAO();
        try {
            Customer cust = dao.getCustomerById(customerId);
            request.setAttribute("cust", cust);
            request.setAttribute("profileImagePath", profileImagePath);
            request.getRequestDispatcher("UpdateCustomerProfile.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error forwarding to JSP: " + e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get user inputs
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String password = request.getParameter("password");
        
        // Retrieve existing profile image
        String existingProfileImage = request.getParameter("existingProfileImage");

        Part profileImagePart = request.getPart("profileImage");
        String profileImagePath = existingProfileImage; // Default to existing image

        // Check if the user uploaded a new profile image
        if (profileImagePart != null && profileImagePart.getSize() > 0) {
            String uploadedFileName = profileImagePart.getSubmittedFileName();
            profileImagePath = "images/" + uploadedFileName; // Store in a separate folder

            // Define the full path to save the image
            String uploadPath = getServletContext().getRealPath("/") + profileImagePath;

            // Ensure the directory exists
            File uploadDir = new File(getServletContext().getRealPath("/") + "profile_images");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save the uploaded file
            Files.copy(profileImagePart.getInputStream(), Paths.get(uploadPath), StandardCopyOption.REPLACE_EXISTING);
        }

        // Get customer ID from session
        Integer customerId = (Integer) request.getSession().getAttribute("customerId");
        if (customerId == null) {
            response.sendRedirect("userlogin.jsp");
            return;
        }

        // Update the customer record
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "UPDATE customer SET Customer_email = ?, Customer_phone = ?, Customer_password = ?, Customer_profileImage = ? WHERE Customer_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, password);
            stmt.setString(4, profileImagePath);
            stmt.setInt(5, customerId);

            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirect to profile page after updating
        response.sendRedirect("CustomerProfileController");
    }
}
