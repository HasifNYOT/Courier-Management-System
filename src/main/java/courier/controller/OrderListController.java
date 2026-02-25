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
import connection.ConnectionManager;
import courier.dao.ProcourierDAO;
import procourier.model.Package;

@WebServlet("/OrderListController")
public class OrderListController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        // Retrieve Customer_ID from session
        Integer customerId = (Integer) request.getSession().getAttribute("customerId");

        if (customerId == null) {
            response.sendRedirect("userlogin.jsp?message=unauthorized");
            return;
        }
        
      //Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        
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

        List<Package> orderList = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection()) {
        	String sql = """
                    SELECT p.Package_ID, p.Receiver_name, p.Weight, p.Dimension, p.Content_description, p.Dropoff_location 
                    FROM Package p 
                    LEFT JOIN Payment pay ON p.Package_ID = pay.Package_ID
                    WHERE p.Customer_ID = ? AND (pay.Payment_status IS NULL OR pay.Payment_status = 'Pending')
                    """;

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Package pkg = new Package();
                pkg.setPackageId(rs.getInt("Package_ID"));
                pkg.setReceivername(rs.getString("Receiver_name"));
                pkg.setWeight(rs.getDouble("Weight"));
                pkg.setDimension(rs.getString("Dimension"));
                pkg.setDescription(rs.getString("Content_description"));
                pkg.setDropoff(rs.getString("Dropoff_location"));
                orderList.add(pkg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("errorPage.jsp?message=database_error");
            return;
        }

        request.setAttribute("orderList", orderList);
        request.getRequestDispatcher("OrderList.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Handle delete functionality
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            try {
                int packageId = Integer.parseInt(request.getParameter("packageId"));

                // Use ProcourierDAO to delete the package
                ProcourierDAO procourierDAO = new ProcourierDAO();
                procourierDAO.deletePackage(packageId);

                // Redirect to refresh the order list
                response.sendRedirect("OrderListController?message=deleteSuccess");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("errorPage.jsp?message=deleteError");
            }
        }
    }
}
