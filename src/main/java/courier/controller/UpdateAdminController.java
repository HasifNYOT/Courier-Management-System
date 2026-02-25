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
import procourier.model.Admin;
import connection.ConnectionManager;
import courier.dao.ProcourierDAO;

@WebServlet("/UpdateAdminController")
@MultipartConfig(maxFileSize = 16177215) // Limit file size to ~16MB
public class UpdateAdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        Integer adminId = (Integer) request.getSession().getAttribute("adminId");
        if (adminId == null) {
            response.sendRedirect("adminlogin.jsp");
            return;
        }
        System.out.println("Courier ID: " + adminId);
        
        if (adminId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Admin_profileImage FROM admin WHERE Admin_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, adminId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profileImagePath = rs.getString("Admin_profileImage");
                    //System.out.println("Profile image path from DB: " + profileImagePath);
                    if (profileImagePath == null || profileImagePath.isEmpty()) {
                        profileImagePath = "images/default-profile.png"; // Fallback if no image in database
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
      //Fetch Customer data
        ProcourierDAO dao = new ProcourierDAO();
        // Add profile image path to request scope
        try {
        	Admin admin = dao.getAdminById(adminId);
	        request.setAttribute("admin", admin); // Set the package object as 'order' in request
        	request.setAttribute("profileImagePath", profileImagePath);
        	request.getRequestDispatcher("UpdateAdminProfile.jsp").forward(request, response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        response.getWriter().println("Error forwarding to JSP: " + e.getMessage());
	    }
	}
	
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        Part profileImagePart = request.getPart("profileImage");
        
     // Retrieve existing profile image
        String existingProfileImage = request.getParameter("existingProfileImage");
        String profileImagePath = existingProfileImage; // Default to existing image

        // Check if the user uploaded a profile image
        if (profileImagePart != null && profileImagePart.getSize() > 0) {
            // User uploaded a new image
            String uploadedFileName = profileImagePart.getSubmittedFileName();
            profileImagePath = "images/" + uploadedFileName; // Set the path in the images folder

            // Define the full path to save the image
            String uploadPath = getServletContext().getRealPath("/") + profileImagePath;

            // Ensure the directory exists
            File uploadDir = new File(getServletContext().getRealPath("/") + "images");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // Create the directory if it doesn't exist
            }

            // Copy the uploaded file to the specified path
            Files.copy(profileImagePart.getInputStream(), Paths.get(uploadPath), StandardCopyOption.REPLACE_EXISTING);
        }

        // Get courier ID from session
        Integer adminId = (Integer) request.getSession().getAttribute("adminId");
        if (adminId == null) {
            response.sendRedirect("adminlogin.jsp");
            return;
        }

        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "UPDATE admin SET Admin_email = ?, Admin_phone = ?, Admin_profileImage = ? WHERE Admin_ID = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, profileImagePath); // Save the correct profile image path
            stmt.setInt(4, adminId);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Redirect back to the profile page after the update
        response.sendRedirect("AdminProfileController");
    }
}

