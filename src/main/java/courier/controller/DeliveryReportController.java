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
import procourier.model.DeliveryReport;
import connection.ConnectionManager;

@WebServlet("/DeliveryReportController")
public class DeliveryReportController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<DeliveryReport> deliveryReports = new ArrayList<>();
        String deliveryIDParam = request.getParameter("deliveryID");

        try (Connection conn = ConnectionManager.getConnection()) {
            String sql =  "SELECT d.Delivery_ID, d.Delivery_date, d.Pickup_location, " +
                    "c.Courier_ID, c.Courier_name, c.Courier_email, c.Courier_phone, " +
                    "cust.Customer_ID, cust.Customer_name, cust.Customer_email, cust.Customer_phone, cust.Customer_Address, " +
                    "p.receiver_name, p.Dropoff_location, " +
                    "pay.Payment_amount, pay.Payment_method, pay.Payment_status, pay.Payment_time, pay.Payment_receipt " +
                    "FROM Delivery d " +
                    "JOIN Courier c ON d.Courier_ID = c.Courier_ID " +
                    "JOIN Customer cust ON d.Customer_ID = cust.Customer_ID " +
                    "JOIN Package p ON d.Package_ID = p.Package_ID " +
                    "JOIN Payment pay ON d.Payment_ID = pay.Payment_ID " +
                    "WHERE d.Delivery_status = 'Completed'";

            // If a specific Delivery ID is provided, add a WHERE clause to filter
            if (deliveryIDParam != null && !deliveryIDParam.isEmpty()) {
                sql += " AND d.Delivery_ID = ?";
            }

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                if (deliveryIDParam != null && !deliveryIDParam.isEmpty()) {
                    ps.setInt(1, Integer.parseInt(deliveryIDParam));
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DeliveryReport report = new DeliveryReport();
                        report.setDeliveryID(rs.getInt("Delivery_ID"));
                        report.setDeliveryDate(rs.getDate("Delivery_date"));
                        report.setPickupLocation(rs.getString("Pickup_location"));

                        report.setCourierID(rs.getInt("Courier_ID"));
                        report.setCourierName(rs.getString("Courier_name"));
                        report.setCourierEmail(rs.getString("Courier_email"));
                        report.setCourierPhone(rs.getString("Courier_phone"));

                        report.setCustomerID(rs.getInt("Customer_ID"));
                        report.setCustomerName(rs.getString("Customer_name"));
                        report.setCustomerEmail(rs.getString("Customer_email"));
                        report.setCustomerPhone(rs.getString("Customer_phone"));
                        report.setCustomerAddress(rs.getString("Customer_Address"));

                        report.setReceiverName(rs.getString("receiver_name"));
                        report.setDropoffLocation(rs.getString("Dropoff_location"));

                        report.setPaymentAmount(rs.getDouble("Payment_amount"));
                        report.setPaymentMethod(rs.getString("Payment_method"));
                        report.setPaymentStatus(rs.getString("Payment_status"));
                        report.setPaymentTime(rs.getTimestamp("Payment_time"));
                        report.setPaymentReceipt(rs.getString("Payment_receipt"));

                        deliveryReports.add(report);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        
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
        

        // Pass the filtered or complete list to the JSP
        request.setAttribute("deliveryReportList", deliveryReports);
        request.getRequestDispatcher("adminDeliveryReport.jsp").forward(request, response);
    }
}
