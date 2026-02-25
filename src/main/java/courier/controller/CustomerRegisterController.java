package courier.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import courier.dao.ProcourierDAO;
import procourier.model.Customer;

@WebServlet("/CustomerRegisterController")
public class CustomerRegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProcourierDAO procourierDAO = new ProcourierDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Retrieve form data
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String dobString = request.getParameter("dob");

            // Check if passwords match
            /*if (!password.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Passwords do not match!");
                request.getRequestDispatcher("register.jsp").forward(request, response);
                return;
            }*/

            // Check if email already exists
            Customer existingCustomer = procourierDAO.getCustomerByEmail(email);
            if (existingCustomer != null) {
                response.sendRedirect("userlogin.jsp?error=The email already exist");
                return;
            }

            // Parse Date of Birth
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dob = sdf.parse(dobString);

            // Create Customer object
            Customer customer = new Customer();
            customer.setName(fullName);
            customer.setEmail(email);
            customer.setPassword(password);
            customer.setPhone(phone);
            customer.setAddress(address);
            customer.setDob(dob);

            // Save customer to the database
            procourierDAO.insertCustomer(customer);

            // Redirect to login page
            response.sendRedirect("userlogin.jsp?message=registered");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=unexpected");
        }
    }
}
