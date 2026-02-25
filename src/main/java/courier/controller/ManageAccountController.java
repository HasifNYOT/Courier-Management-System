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
import procourier.model.Courier;
import procourier.model.Customer;
import connection.ConnectionManager;

@WebServlet("/ManageAccountController")
public class ManageAccountController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Courier> courierList = new ArrayList<>();
        List<Customer> customerList = new ArrayList<>();

        try (Connection conn = ConnectionManager.getConnection()) {
            // Fetch data from the Courier table
            String courierSql = "SELECT * FROM Courier";
            try (PreparedStatement ps = conn.prepareStatement(courierSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Courier courier = new Courier();
                    courier.setId(rs.getInt("Courier_ID"));
                    courier.setName(rs.getString("Courier_name"));
                    courier.setEmail(rs.getString("Courier_email"));
                    courier.setPhone(rs.getString("Courier_phone"));
                    courier.setPassword(rs.getString("Courier_password"));
                    courier.setLicense(rs.getString("Courier_license"));
                    courier.setStatus(rs.getString("Courier_Status"));
                    courier.setDob(rs.getDate("Courier_DOB"));
                    courierList.add(courier); // Add each courier to the list
                }
            }

            // Fetch data from the Customer table
            String customerSql = "SELECT * FROM Customer";
            try (PreparedStatement ps = conn.prepareStatement(customerSql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("Customer_ID"));
                    customer.setName(rs.getString("Customer_name"));
                    customer.setPassword(rs.getString("Customer_password"));
                    customer.setEmail(rs.getString("Customer_email"));
                    customer.setPhone(rs.getString("Customer_phone"));
                    customer.setAddress(rs.getString("Customer_Address"));
                    customer.setDob(rs.getDate("Customer_DOB"));
                    customerList.add(customer); // Add each customer to the list
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
        

        // Set both lists as request attributes
        request.setAttribute("courierList", courierList);
        request.setAttribute("customerList", customerList);

        // Forward to the JSP page
        request.getRequestDispatcher("adminManageAccount.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String type = request.getParameter("type");
        String id = request.getParameter("id");

        if (type != null && id != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                // Check if the type is "courier" and delete the courier
                if (type.equals("courier")) {
                    String deleteCourierSql = "DELETE FROM courier WHERE Courier_ID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteCourierSql)) {
                        ps.setInt(1, Integer.parseInt(id));
                        ps.executeUpdate(); // Execute the delete operation
                    }
                }
                // Check if the type is "customer" and delete the customer
                else if (type.equals("customer")) {
                    String deleteCustomerSql = "DELETE FROM customer WHERE Customer_ID = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteCustomerSql)) {
                        ps.setInt(1, Integer.parseInt(id));
                        ps.executeUpdate(); // Execute the delete operation
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // After deletion, redirect back to the ManageAccount page to refresh the lists
        response.sendRedirect("ManageAccountController");
    }
}

