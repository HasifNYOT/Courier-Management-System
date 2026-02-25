package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import connection.ConnectionManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import courier.dao.ProcourierDAO;
import procourier.model.Package;
import procourier.model.Payment;

@WebServlet("/PaymentController")
public class PaymentController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Handles deletion when the user clicks "Back to Order List"
        int packageId = Integer.parseInt(request.getParameter("packageId"));
        String deleteFlag = request.getParameter("delete");
        
      //Fetch the profile image for the logged-in admin
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

        if ("true".equals(deleteFlag)) {
            ProcourierDAO dao = new ProcourierDAO();
            try {
                List<Payment> existingPayments = dao.getPaymentsByPackage(packageId);
                for (Payment payment : existingPayments) {
                    dao.deletePayment(payment);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            request.getRequestDispatcher("OrderListController").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerId = (Integer) session.getAttribute("customerId");

        if (customerId == null) {
            String customerIdStr = request.getParameter("customerId");
            if (customerIdStr != null && !customerIdStr.isEmpty()) {
                customerId = Integer.parseInt(customerIdStr);
                session.setAttribute("Customer_ID", customerId);
            }
        }

        if (customerId == null) {
            response.sendRedirect("userlogin.jsp");
            return;
        }

        int packageId = Integer.parseInt(request.getParameter("packageId"));
        String method = request.getParameter("method");

        ProcourierDAO dao = new ProcourierDAO();
        Package pkg = new Package();
        try {
            pkg = dao.getPackageById(packageId);
            if (pkg == null) {
                response.sendRedirect("paymentFailed.jsp");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("paymentFailed.jsp");
            return;
        }

        // **Delete any existing payment records for this package**
        try {
            List<Payment> existingPayments = dao.getPaymentsByPackage(packageId);
            for (Payment payment : existingPayments) {
                dao.deletePayment(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("paymentFailed.jsp");
            return;
        }

        // **Insert a new payment record**
        double amount = calculatePaymentAmount(pkg.getWeight());
        Payment payment = new Payment();
        
        pkg.setPackageId(packageId);
        
        payment.setPackage(pkg);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setTime(new Date());
        payment.setStatus("Pending");
        payment.setReceipt(null);
        

        try {
            dao.insertPayment(payment);
            request.setAttribute("amount", amount);
            request.setAttribute("pkg", pkg);
            request.getRequestDispatcher("payment.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("paymentFailed.jsp");
        }
    }

    private double calculatePaymentAmount(double weight) {
        double basePrice = 5.00;
        double extraChargePerKg = 2.00;
        return weight <= 1.0 ? basePrice : basePrice + ((weight - 1) * extraChargePerKg);
    }
}
