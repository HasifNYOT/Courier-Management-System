package courier.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import courier.dao.ProcourierDAO;
import procourier.model.Package;

@WebServlet("/UpdateOrderController")
public class UpdateOrderController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            // Retrieve action and session customer ID
            String action = request.getParameter("action");
            Integer customerId = (Integer) request.getSession().getAttribute("customerId");

            // If customer is not logged in, redirect to login page
            if (customerId == null) {
                response.sendRedirect("userlogin.jsp");
                return;
            }
            
            System.out.println("KASTERMERID: " + customerId);

            ProcourierDAO dao = new ProcourierDAO();

            if ("update".equals(action)) {
                int packageId = Integer.parseInt(request.getParameter("packageId"));
                Package order = dao.getPackageById(packageId);
                request.setAttribute("order", order); // Set the package object as 'order' in request
                request.getRequestDispatcher("UpdateOrder.jsp").forward(request, response);

            } else if ("confirm".equals(action)) {
                int packageId = Integer.parseInt(request.getParameter("packageId"));

                // Retrieve updated values from form
                double weight = Double.parseDouble(request.getParameter("weight"));
                String dimension = request.getParameter("dimension").trim();
                String description = request.getParameter("description").trim();
                String receiverName = request.getParameter("receiverName").trim();
                String dropoffLocation = request.getParameter("dropoffLocation").trim();

                // Fetch the package to update
                Package existingPackage = dao.getPackageById(packageId);
                if (existingPackage != null) {
                    existingPackage.setWeight(weight);
                    existingPackage.setDimension(dimension);
                    existingPackage.setDescription(description);
                    existingPackage.setReceivername(receiverName);
                    existingPackage.setDropoff(dropoffLocation);
                    existingPackage.setPackageStatus("Unassign");

                    // Update in database
                    dao.updatePackage(packageId, existingPackage);
                }

                // Redirect to Order List
                response.sendRedirect("OrderListController");

            } else if ("makePayment".equals(action)) {
                int packageId = Integer.parseInt(request.getParameter("packageId"));
                request.setAttribute("packageId", packageId);
                request.getRequestDispatcher("payment.jsp").forward(request, response);

            } else if ("cancel".equals(action)) {
                response.sendRedirect("OrderListController");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("errorPage.jsp?message=operationError");
        }
    }
}
