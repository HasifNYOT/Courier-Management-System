package courier.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import courier.dao.ProcourierDAO;

@WebServlet("/ConfirmPaymentController")
public class ConfirmPaymentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int packageId = Integer.parseInt(request.getParameter("packageId"));
            String paymentMethod = request.getParameter("method");

            if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
                request.setAttribute("error", "Please select a payment method before confirming payment.");
                request.getRequestDispatcher("payment.jsp").forward(request, response);
                return;
            }

            // Call DAO to update payment method
            ProcourierDAO paymentDAO = new ProcourierDAO();
            paymentDAO.updatePaymentMethod(packageId, paymentMethod);
            
            System.out.print("packageId = " + packageId);
            System.out.print("packageId = " + paymentMethod);

            // Call DAO to update payment status
            paymentDAO.updatePaymentStatus(packageId, "Complete");

            // Redirect back to Order List
            response.sendRedirect("OrderListController?message=Succesfully paid");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
            System.out.print("tak dpt retrive package ID");
        }
    }
}
