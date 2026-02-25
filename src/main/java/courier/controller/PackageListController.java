package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import courier.dao.ProcourierDAO;
import procourier.model.Package;
import connection.ConnectionManager;

@WebServlet("/PackageListController")
public class PackageListController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Fetch unassigned packages
            List<Package> unassignedPackages = ProcourierDAO.getUnassignedPackages();

            // Set the list in the request scope
            request.setAttribute("unassignedPackages", unassignedPackages);
            
            
          //Fetch the profile image for the logged-in admin
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

            // Forward to the JSP
            request.getRequestDispatcher("adminPackageList.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch package list.");
        }
    }
}
