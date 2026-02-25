package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import courier.dao.ProcourierDAO;
import procourier.model.Courier;
import procourier.model.Customer;
import procourier.model.Package;
import procourier.model.Delivery;
import procourier.model.Payment;
import connection.ConnectionManager;

@WebServlet("/AssignCourierController")
public class AssignCourierController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Fetch available couriers
            List<Courier> couriers = ProcourierDAO.getCouriersWithStatus();

            // Set the couriers and package ID in the request scope
            request.setAttribute("couriers", couriers);
            request.setAttribute("packageId", request.getParameter("packageId"));
            
            
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
            request.getRequestDispatcher("adminAssignCourier.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fetch courier list.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Retrieve form data
            int courierId = Integer.parseInt(request.getParameter("courierId"));
            int packageId = Integer.parseInt(request.getParameter("packageId"));
            String pickupLocation = request.getParameter("pickupLocation");

            // Create an instance of ProcourierDAO
            ProcourierDAO procourierDAO = new ProcourierDAO();

            // Fetch package details
            Package selectedPackage = procourierDAO.getPackageById(packageId);
	            
	         // Fetch package details
            Payment selectedPayment = ProcourierDAO.getPaymentByPackageId(packageId);

            // Create a new delivery
            Delivery newDelivery = new Delivery();

            // Set the courier
            Courier courier = new Courier();
            courier.setId(courierId);
            newDelivery.setCourier(courier);

            // Set the customer
            Customer customer = selectedPackage.getCustomer();
            newDelivery.setCustomer(customer);

            // Set other fields
            newDelivery.setStatus("Pending");
            newDelivery.setDate(new Date());
            newDelivery.setPickup(pickupLocation);
            newDelivery.setPackage(selectedPackage);
            newDelivery.setPayment(selectedPayment);

            // Insert the new delivery
            procourierDAO.insertDelivery(newDelivery);
            procourierDAO.updatePackageStatus(packageId, "Assigned");

            // Redirect back to Package List page
            response.sendRedirect("PackageListController?message=Successfully Assign");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to assign courier.");
        }
    }
}

