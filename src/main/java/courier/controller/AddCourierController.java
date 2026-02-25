package courier.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import procourier.model.Courier;
import courier.dao.ProcourierDAO;
import connection.ConnectionManager;

@WebServlet("/AddCourierController")
public class AddCourierController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ProcourierDAO procourierDAO = new ProcourierDAO();
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Retrieve form data
        String fullName = request.getParameter("fullname");
        String ic = request.getParameter("ic");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String license = request.getParameter("license");
        String dob = request.getParameter("dob");
        String password = request.getParameter("password");
        
     // Check if email already exists
        Courier existingCustomer = procourierDAO.getCourierByEmail(email);
        if (existingCustomer != null) {
            response.sendRedirect("userlogin.jsp?error=The email already exist");
            return;
        }

        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "INSERT INTO Courier (Courier_name, Courier_IC, Courier_email, Courier_phone, Courier_license, Courier_DOB, Courier_password, Courier_Status) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, 'Active')";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, fullName);
            stmt.setString(2, ic);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, license);
            stmt.setString(6, dob);
            stmt.setString(7, password);
            stmt.executeUpdate();

            // Redirect to login page after successful registration
            response.sendRedirect("userlogin.jsp?message=registered");
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }
}