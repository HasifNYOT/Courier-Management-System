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
import procourier.model.Delivery;
import connection.ConnectionManager;
import courier.dao.ProcourierDAO;

@WebServlet("/OngoingDeliveryController")
public class OngoingDeliveryController extends HttpServlet {
	private static final long serialVersionUID = 1L;
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchQuery = request.getParameter("search");
        List<Delivery> ongoingDeliveries = new ArrayList<>();


        //Fetch the profile image for the logged-in courier
        String profileImagePath = "images/default-profile.png"; // Default profile image path
        Integer courierId = (Integer) request.getSession().getAttribute("courierId");
        
        if (courierId != null) {
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT Courier_profileImage FROM courier WHERE Courier_ID = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courierId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    profileImagePath = rs.getString("Courier_profileImage");
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
        
        
        //Ongoing deliveries
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "SELECT d.Delivery_ID, d.Delivery_status, d.Delivery_date, p.Dropoff_location " +
	                    "FROM delivery d " +
	                    "LEFT JOIN package p ON d.Package_ID = p.Package_ID " +
	                    "WHERE d.Delivery_status = 'In Progress' AND d.Courier_ID = ?";
            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                sql += " AND Delivery_ID LIKE ?";
            }

            PreparedStatement stmt = conn.prepareStatement(sql);
            
         // Set the Courier_ID parameter
	        stmt.setInt(1, courierId);
	        
	     // Set the search query parameter if it exists
	        if (searchQuery != null && !searchQuery.isEmpty()) {
	            stmt.setString(2, "%" + searchQuery + "%");
	        }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ongoingDeliveries.add(new Delivery(
                    rs.getInt("Delivery_ID"),
                    null, // Courier can be fetched if needed
                    null, // Customer can be fetched if needed
                    rs.getString("Delivery_status"),
                    rs.getDate("Delivery_date"),
                    null,  // Pickup_location can be fetched if needed
                    null,  // Package can be fetched if needed
                    null,  // Payment can be fetched if needed
                    rs.getString("Dropoff_location")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("ongoingDeliveries", ongoingDeliveries);
        request.getRequestDispatcher("ListOngoingDelivery.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int deliveryId = Integer.parseInt(request.getParameter("Delivery_ID"));
        int courierId = (int) request.getSession().getAttribute("courierId");
        String searchQuery = request.getParameter("search"); // Capture search query

        try {
            ProcourierDAO dao = new ProcourierDAO();

            if ("complete".equals(action)) {
                Courier courier = dao.getCourierById(courierId);
                if (courier != null) {
                	
                	// Step 1: Update delivery status
                	dao.updateDeliveryStatus(deliveryId, "Completed");
                	
                	// Step 2: Update courier's active deliveries
                    courier.setCurrentActiveDelivery(courier.getCurrentActiveDelivery() - 1);
                    dao.updateCourier(courier);
                    
                } else {
                    throw new Exception("Courier not found!");
                }
            }

            // Fetch updated courier details to show in JSP
            Courier courier = dao.getCourierById(courierId);
            request.setAttribute("profileImagePath", courier.getProfileImage());

            // Fetch updated deliveries after confirmation
            List<Delivery> deliveries = new ArrayList<>();
            try (Connection conn = ConnectionManager.getConnection()) {
                String sql = "SELECT d.Delivery_ID, d.Delivery_status, d.Delivery_date, p.Dropoff_location " +
		                    "FROM delivery d " +
		                    "LEFT JOIN package p ON d.Package_ID = p.Package_ID " +
		                    "WHERE d.Delivery_status = 'In Progress' AND d.Courier_ID = ?";
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    sql += " AND d.Delivery_ID LIKE ?";
                }

                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, courierId);
                if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                    stmt.setString(2, "%" + searchQuery + "%");
                }

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Delivery delivery = new Delivery(
                		rs.getInt("Delivery_ID"),
                        null, // Courier can be fetched if needed
                        null, // Customer can be fetched if needed
                        rs.getString("Delivery_status"),
                        rs.getDate("Delivery_date"),
                        null,  // Pickup_location can be fetched if needed
                        null,  // Package can be fetched if needed
                        null,  // Payment be fetched if needed
                        rs.getString("Dropoff_location")
                    );
                    deliveries.add(delivery);
                }
            }

            // Pass searchQuery and deliveries back to JSP
            request.setAttribute("searchQuery", searchQuery);
            request.setAttribute("ongoingDeliveries", deliveries);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<script>alert('An error occurred: " + e.getMessage() + "'); window.history.back();</script>");
            return;
        }

        // Forward to JSP
        request.getRequestDispatcher("ListOngoingDelivery.jsp?message=Delivery completed").forward(request, response);
    }
}
