package courier.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connection.ConnectionManager;
import procourier.model.Admin;
import procourier.model.Courier;
import procourier.model.Customer;
import procourier.model.Delivery;
import procourier.model.Package;
import procourier.model.Payment;

public class ProcourierDAO {

    // Mark a delivery as completed
    public static void markDeliveryCompleted(int deliveryId) throws Exception {
        String sql = "UPDATE Delivery SET Delivery_status = 'Completed' WHERE Delivery_ID = ?";

        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, deliveryId);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Delivery with ID " + deliveryId + " has been marked as completed.");
            } else {
                System.out.println("No delivery found with ID " + deliveryId + ".");
            }
        }
    }

 // ==================== ADMIN CRUD OPERATIONS ====================

    public void insertAdmin(Admin admin) throws Exception {
    	String sql = "INSERT INTO admin (Admin_name, Admin_password, Admin_email, Admin_phone, Admin_DOB) VALUES (?, ?, ?, ?, ?)";
    	try (Connection conn = ConnectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
               stmt.setString(1, admin.getName());
               stmt.setString(2, admin.getPassword());
               stmt.setString(3, admin.getEmail());
               stmt.setString(4, admin.getPhone());
               stmt.setDate(5, new java.sql.Date(admin.getDob().getTime())); // Converting Java Date to SQL Date
               stmt.executeUpdate();
        }
    }

    public void updateAdmin(Admin admin) throws Exception {
        String sql = "UPDATE admin SET Admin_name = ?, Admin_password = ?, Admin_email = ?, Admin_phone = ?, Admin_DOB = ? WHERE Admin_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, admin.getName());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getEmail());
            stmt.setString(4, admin.getPhone());
            stmt.setDate(5, new java.sql.Date(admin.getDob().getTime())); // Convert Java Date to SQL Date
            stmt.setInt(6, admin.getId());  // Set Admin ID for the WHERE clause
            stmt.executeUpdate();
        }
    }

    public void deleteAdmin(Admin admin) throws Exception {
        String sql = "DELETE FROM admin WHERE Admin_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, admin.getId());  // Use the Admin object to get the ID for deletion
            stmt.executeUpdate();
        }
    }

    public Admin getAdminById(int id) throws Exception {
        String sql = "SELECT * FROM admin WHERE Admin_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Admin admin = new Admin();
                    admin.setId(rs.getInt("Admin_ID"));
                    admin.setName(rs.getString("Admin_name"));
                    admin.setPassword(rs.getString("Admin_password"));
                    admin.setEmail(rs.getString("Admin_email"));
                    admin.setPhone(rs.getString("Admin_phone"));
                    admin.setDob(rs.getDate("Admin_DOB"));
                    admin.setProfileImage(rs.getString("Admin_profileImage"));
                    return admin;
                }
            }
        }
        return null;
    }

    public List<Admin> getAllAdmins() throws Exception {
        String sql = "SELECT * FROM admin";
        List<Admin> admins = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("Admin_ID"));
                admin.setName(rs.getString("Admin_name"));
                admin.setPassword(rs.getString("Admin_password"));
                admin.setEmail(rs.getString("Admin_email"));
                admin.setPhone(rs.getString("Admin_phone"));
                admin.setDob(rs.getDate("Admin_DOB"));
                admins.add(admin);
            }
        }
        return admins;
    }

 // ==================== COURIER CRUD OPERATIONS ====================

    public void insertCourier(Courier courier) throws Exception {
        String sql = "INSERT INTO courier (Courier_name, Courier_IC, Courier_email, Courier_phone, Courier_password, Courier_license, Courier_Status, Courier_DOB) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courier.getName());
            stmt.setString(2, courier.getIc());
            stmt.setString(3, courier.getEmail());
            stmt.setString(4, courier.getPhone());
            stmt.setString(5, courier.getPassword());
            stmt.setString(6, courier.getLicense());
            stmt.setString(7, courier.getStatus());
            stmt.setDate(8, new java.sql.Date(courier.getDob().getTime())); // Assuming dob is of type Date
            stmt.executeUpdate();
        }
    }

    public void updateCourier(Courier courier) throws Exception {
        String sql = "UPDATE courier SET Courier_name = ?, Courier_IC = ?, Courier_email = ?, Courier_phone = ?, Courier_password = ?, Courier_license = ?, Courier_Status = ?, Courier_DOB = ?, Current_Active_Deliveries = ? WHERE Courier_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, courier.getName());
            stmt.setString(2, courier.getIc());
            stmt.setString(3, courier.getEmail());
            stmt.setString(4, courier.getPhone());
            stmt.setString(5, courier.getPassword());
            stmt.setString(6, courier.getLicense());
            stmt.setString(7, courier.getStatus());
            stmt.setDate(8, new java.sql.Date(courier.getDob().getTime())); // Assuming dob is of type Date
            stmt.setInt(9, courier.getCurrentActiveDelivery());
            stmt.setInt(10, courier.getId()); // Using courier's ID
            stmt.executeUpdate();
        }
    }

    public void deleteCourier(Courier courier) throws Exception {
        String sql = "DELETE FROM courier WHERE Courier_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, courier.getId()); // Using courier's ID to delete
            stmt.executeUpdate();
        }
    }

    public Courier getCourierById(int id) throws Exception {
        String sql = "SELECT * FROM courier WHERE Courier_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Creating a new Courier object and setting its fields from the ResultSet
                    Courier courier = new Courier();
                    courier.setId(rs.getInt("Courier_ID"));
                    courier.setName(rs.getString("Courier_name"));
                    courier.setIc(rs.getString("Courier_IC"));
                    courier.setEmail(rs.getString("Courier_email"));
                    courier.setPhone(rs.getString("Courier_phone"));
                    courier.setPassword(rs.getString("Courier_password")); // Ensure proper handling of passwords
                    courier.setLicense(rs.getString("Courier_license"));
                    courier.setStatus(rs.getString("Courier_Status"));
                    courier.setDob(rs.getDate("Courier_DOB"));
                    courier.setProfileImage(rs.getString("Courier_profileImage"));
                    courier.setCurrentActiveDelivery(rs.getInt("Current_Active_Deliveries"));
                    courier.setDeliveryLimit(rs.getInt("Delivery_Limit"));

                    return courier; // Returning the Courier object
                }
            }
        }
        // Returning null if the courier is not found
        return null;
    }

    public List<Courier> getAllCouriers() throws Exception {
        String sql = "SELECT * FROM courier";
        List<Courier> couriers = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Creating a new Courier object for each row in the ResultSet
                Courier courier = new Courier();
                courier.setId(rs.getInt("Courier_ID"));
                courier.setName(rs.getString("Courier_name"));
                courier.setIc(rs.getString("Courier_IC"));
                courier.setEmail(rs.getString("Courier_email"));
                courier.setPhone(rs.getString("Courier_phone"));
                courier.setPassword(rs.getString("Courier_password"));
                courier.setLicense(rs.getString("Courier_license"));
                courier.setStatus(rs.getString("Courier_Status"));
                courier.setDob(rs.getDate("Courier_DOB"));
                courier.setCurrentActiveDelivery(rs.getInt("Current_Active_Deliveries"));
                courier.setDeliveryLimit(rs.getInt("Delivery_Limit"));
                couriers.add(courier); // Add each courier to the list
            }
        }
        return couriers;
    }
    
    
    // Get couriers with their availability status
    public static List<Courier> getCouriersWithStatus() throws Exception {
        String sql = "SELECT c.*, " +
                     "(c.Delivery_Limit > c.Current_Active_Deliveries) AS IsAvailable " +
                     "FROM courier c";
        List<Courier> couriers = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Courier courier = new Courier();
                courier.setId(rs.getInt("Courier_ID"));
                courier.setName(rs.getString("Courier_name"));
                courier.setIc(rs.getString("Courier_IC"));
                courier.setEmail(rs.getString("Courier_email"));
                courier.setPhone(rs.getString("Courier_phone"));
                courier.setPassword(rs.getString("Courier_password"));
                courier.setLicense(rs.getString("Courier_license"));
                courier.setStatus(rs.getString("Courier_Status"));
                courier.setDob(rs.getDate("Courier_DOB"));
                courier.setCurrentActiveDelivery(rs.getInt("Current_Active_Deliveries"));
                courier.setDeliveryLimit(rs.getInt("Delivery_Limit"));

             // Determine availability without setting it explicitly in the Courier model
                boolean isAvailable = rs.getBoolean("IsAvailable");
                if (isAvailable) {
                    courier.setStatus("Available");
                } else {
                    courier.setStatus("Unavailable");
                }

                couriers.add(courier);
            }
        }
        return couriers;
    }
    
    
    public Courier getCourierByEmail(String email) {
        Courier courier = null;
        try {
            Connection con = ConnectionManager.getConnection(); // Ensure this method is correctly retrieving the connection
            String query = "SELECT * FROM courier WHERE Courier_email = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // If email exists, create a customer object
            	courier = new Courier();
            	courier.setId(rs.getInt("Courier_ID"));
                courier.setName(rs.getString("Courier_name"));
                courier.setIc(rs.getString("Courier_IC"));
                courier.setEmail(rs.getString("Courier_email"));
                courier.setPhone(rs.getString("Courier_phone"));
                courier.setPassword(rs.getString("Courier_password"));
                courier.setLicense(rs.getString("Courier_license"));
                courier.setStatus(rs.getString("Courier_Status"));
                courier.setDob(rs.getDate("Courier_DOB"));
                courier.setCurrentActiveDelivery(rs.getInt("Current_Active_Deliveries"));
                courier.setDeliveryLimit(rs.getInt("Delivery_Limit"));
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return courier; // Returns null if no customer is found
    }


    // Similar CRUD operations can be implemented for the Customer, Delivery, Package, and Payment tables.
    
 // ==================== CUSTOMER CRUD OPERATIONS ====================

    public void insertCustomer(Customer customer) throws Exception {
        String sql = "INSERT INTO customer (Customer_name, Customer_password, Customer_email, Customer_phone, Customer_Address, Customer_DOB) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setDate(6, new java.sql.Date(customer.getDob().getTime())); // Assuming dob is of type Date
            stmt.executeUpdate();
        }
    }
    
    
    public void updateCustomer(int id, Customer customer) throws Exception {
        String sql = "UPDATE customer SET Customer_name = ?, Customer_password = ?, Customer_email = ?, Customer_phone = ?, Customer_Address = ?, Customer_DOB = ? WHERE Customer_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getPassword());
            stmt.setString(3, customer.getEmail());
            stmt.setString(4, customer.getPhone());
            stmt.setString(5, customer.getAddress());
            stmt.setDate(6, new java.sql.Date(customer.getDob().getTime())); // Assuming dob is of type Date
            stmt.setInt(7, id);
            stmt.executeUpdate();
        }
    }

    public void deleteCustomer(Customer customer) throws Exception {
        String sql = "DELETE FROM customer WHERE Customer_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, customer.getId());
            stmt.executeUpdate();
        }
    }

    public Customer getCustomerById(int id) throws Exception {
        String sql = "SELECT * FROM customer WHERE Customer_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("Customer_ID"));
                    customer.setName(rs.getString("Customer_name"));
                    customer.setPassword(rs.getString("Customer_password"));
                    customer.setEmail(rs.getString("Customer_email"));
                    customer.setPhone(rs.getString("Customer_phone"));
                    customer.setAddress(rs.getString("Customer_Address"));
                    customer.setDob(rs.getDate("Customer_DOB"));
                    customer.setProfileImage(rs.getString("Customer_profileImage"));
                    return customer;
                }
            }
        }
        return null;
    }

    public List<Customer> getAllCustomers() throws Exception {
        String sql = "SELECT * FROM customer";
        List<Customer> customers = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Customer customer = new Customer();
                customer.setId(rs.getInt("Customer_ID"));
                customer.setName(rs.getString("Customer_name"));
                customer.setPassword(rs.getString("Customer_password"));
                customer.setEmail(rs.getString("Customer_email"));
                customer.setPhone(rs.getString("Customer_phone"));
                customer.setAddress(rs.getString("Customer_Address"));
                customer.setDob(rs.getDate("Customer_DOB"));
                customers.add(customer);
            }
        }
        return customers;
    }
    
    
    public Customer getCustomerByEmail(String email) {
        Customer customer = null;
        try {
            Connection con = ConnectionManager.getConnection(); // Ensure this method is correctly retrieving the connection
            String query = "SELECT * FROM customer WHERE Customer_email = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) { // If email exists, create a customer object
                customer = new Customer();
                customer.setId(rs.getInt("Customer_ID"));
                customer.setName(rs.getString("Customer_name"));
                customer.setPassword(rs.getString("Customer_password"));
                customer.setEmail(rs.getString("Customer_email"));
                customer.setPhone(rs.getString("Customer_phone"));
                customer.setAddress(rs.getString("Customer_Address"));
                customer.setDob(rs.getDate("Customer_DOB"));
            }

            rs.close();
            ps.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customer; // Returns null if no customer is found
    }
    
    
 // ==================== DELIVERY CRUD OPERATIONS ====================

    public void insertDelivery(Delivery delivery) throws Exception {
        String sql = "INSERT INTO delivery (Courier_ID, Customer_ID, Delivery_status, Delivery_date, Pickup_location, Package_ID, Payment_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delivery.getCourier().getId());
            stmt.setInt(2, delivery.getCustomer().getId());
            stmt.setString(3, delivery.getStatus());
            stmt.setDate(4, delivery.getDate() != null ? new java.sql.Date(delivery.getDate().getTime()) : null); // Assuming dob is of type Date
            stmt.setString(5, delivery.getPickup());
            stmt.setInt(6, delivery.getPackage().getPackageId());
            stmt.setInt(7, delivery.getPayment().getPaymentId());;
            stmt.executeUpdate();
        }
    }

    public void updateDelivery(Delivery delivery) throws Exception {
        String sql = "UPDATE delivery SET Courier_ID = ?, Customer_ID = ?, Delivery_status = ?, Delivery_date = ?, Pickup_location = ?, Package_ID = ?,  Payment_ID = ? WHERE Delivery_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delivery.getCourier().getId());
            stmt.setInt(2, delivery.getCustomer().getId());
            stmt.setString(3, delivery.getStatus());
            stmt.setDate(4, delivery.getDate() != null ? new java.sql.Date(delivery.getDate().getTime()) : null); // Assuming dob is of type Date
            stmt.setString(5, delivery.getPickup());
            stmt.setInt(6, delivery.getPackage().getPackageId());
            stmt.setInt(7, delivery.getPayment().getPaymentId());
            stmt.setInt(8, delivery.getDeliveryId());
            stmt.executeUpdate();
        }
    }
    
    //overloaded method that accepts int deliveryId and String status to update only the delivery status.
    public boolean updateDeliveryStatus(int deliveryId, String status) throws Exception {
        String sql = "UPDATE delivery SET Delivery_status = ? WHERE Delivery_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, deliveryId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            //stmt.executeUpdate();
        }
    }
    

    public void deleteDelivery(Delivery delivery) throws Exception {
        String sql = "DELETE FROM delivery WHERE Delivery_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, delivery.getDeliveryId());
            stmt.executeUpdate();
        }
    }

    public Delivery getDeliveryById(int id) throws Exception {
        String sql = "SELECT d.*, c.*, cu.*, p.*, py.* \r\n"
        		+ "FROM delivery d\r\n"
        		+ "JOIN courier c ON d.Courier_ID = c.Courier_ID\r\n"
        		+ "JOIN customer cu ON d.Customer_ID = cu.Customer_ID\r\n"
        		+ "JOIN package p ON d.Package_ID = p.Package_ID\r\n"
        		+ "JOIN payment py ON d.Payment_ID = py.Payment_ID\r\n"
        		+ "WHERE d.Delivery_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                	// Construct Delivery, Courier, Customer, Package, and Payment objects
                    Delivery delivery = new Delivery();
                    delivery.setDeliveryId(rs.getInt("Delivery_ID"));

                    Courier courier = new Courier();
                    courier.setId(rs.getInt("Courier_ID"));
                    courier.setName(rs.getString("Courier_name"));
                    courier.setStatus(rs.getString("Courier_Status"));
                    delivery.setCourier(courier);

                    Customer customer = new Customer();
                    customer.setId(rs.getInt("Customer_ID"));
                    customer.setName(rs.getString("Customer_name"));
                    delivery.setCustomer(customer);

                    Package pkg = new Package();
                    pkg.setPackageId(rs.getInt("Package_ID"));
                    pkg.setDescription(rs.getString("Content_description"));
                    delivery.setPackage(pkg);

                    Payment payment = new Payment();
                    payment.setPaymentId(rs.getInt("Payment_ID"));
                    payment.setAmount(rs.getDouble("Payment_amount"));
                    delivery.setPayment(payment);

                    delivery.setStatus(rs.getString("Delivery_status"));
                    delivery.setDate(rs.getDate("Delivery_date"));
                    delivery.setPickup(rs.getString("Pickup_location"));

                    return delivery;
                }
            }
        }
        return null;
    }

    public List<Delivery> getAllDeliveries() throws Exception {
        String sql = "SELECT d.*, c.*, cu.*, p.*, py.* \r\n"
        		+ "FROM delivery d\r\n"
        		+ "JOIN courier c ON d.Courier_ID = c.Courier_ID\r\n"
        		+ "JOIN customer cu ON d.Customer_ID = cu.Customer_ID\r\n"
        		+ "JOIN package p ON d.Package_ID = p.Package_ID\r\n"
        		+ "JOIN payment py ON d.Payment_ID = py.Payment_ID\r\n";
        List<Delivery> deliveries = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
            	// Construct Delivery, Courier, Customer, Package, and Payment objects
            	Delivery delivery = new Delivery();
                delivery.setDeliveryId(rs.getInt("Delivery_ID"));

                Courier courier = new Courier();
                courier.setId(rs.getInt("Courier_ID"));
                courier.setName(rs.getString("Courier_name"));
                courier.setStatus(rs.getString("Courier_Status"));
                delivery.setCourier(courier);

                Customer customer = new Customer();
                customer.setId(rs.getInt("Customer_ID"));
                customer.setName(rs.getString("Customer_name"));
                delivery.setCustomer(customer);

                Package pkg = new Package();
                pkg.setPackageId(rs.getInt("Package_ID"));
                pkg.setDescription(rs.getString("Content_description"));
                delivery.setPackage(pkg);

                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("Payment_ID"));
                payment.setAmount(rs.getDouble("Payment_amount"));
                delivery.setPayment(payment);

                delivery.setStatus(rs.getString("Delivery_status"));
                delivery.setDate(rs.getDate("Delivery_date"));
                delivery.setPickup(rs.getString("Pickup_location"));

                deliveries.add(delivery);
            }
        }
        return deliveries;
    }
    
    
 // ==================== PACKAGE CRUD OPERATIONS ====================

    public void insertPackage(Package pkg) throws Exception {
        String sql = "INSERT INTO package (Weight, Dimension, Content_description, receiver_name, Dropoff_location, Package_status, Customer_ID) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, pkg.getWeight());
            stmt.setString(2, pkg.getDimension());
            stmt.setString(3, pkg.getDescription());
            stmt.setString(4, pkg.getReceivername());
            stmt.setString(5, pkg.getDropoff());
            stmt.setString(6, pkg.getPackageStatus());
            stmt.setInt(6, pkg.getCustomer()!= null ? pkg.getCustomer().getId() : null);
            stmt.executeUpdate();
        }
    }

    public void updatePackage(int id, Package pkg) throws Exception {
        String sql = "UPDATE package SET Weight = ?, Dimension = ?, Content_description = ?, receiver_name = ?, Dropoff_location = ?, Customer_ID = ?, Package_status = ? WHERE Package_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the parameters from the Package object
            stmt.setDouble(1, pkg.getWeight());  
            stmt.setString(2, pkg.getDimension()); 
            stmt.setString(3, pkg.getDescription()); 
            stmt.setString(4, pkg.getReceivername());
            stmt.setString(5, pkg.getDropoff());
            stmt.setInt(6, pkg.getCustomer().getId());
            stmt.setString(7, pkg.getPackageStatus());
            stmt.setInt(8, id); 
            stmt.executeUpdate();
        }
    }
    
  //overloaded method that accepts int packageId and String packageStatus to update only the package status.
    public boolean updatePackageStatus(int packageId, String packageStatus) throws Exception {
        String sql = "UPDATE package SET Package_status = ? WHERE Package_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, packageStatus);
            stmt.setInt(2, packageId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
            //stmt.executeUpdate();
        }
    }

    public void deletePackage(int packageId) throws Exception {
        String sql = "DELETE FROM package WHERE Package_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, packageId); 
            stmt.executeUpdate(); 
        }
    }

    public Package getPackageById(int id) throws Exception {
        String sql = "SELECT * FROM package WHERE Package_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Create a new Package object
                    Package pkg = new Package();
                    
                    // Set Package fields from the result set
                    pkg.setPackageId(rs.getInt("Package_ID"));
                    pkg.setWeight(rs.getDouble("Weight"));
                    pkg.setDimension(rs.getString("Dimension"));
                    pkg.setDescription(rs.getString("Content_description"));
                    pkg.setReceivername(rs.getString("receiver_name"));
                    pkg.setDropoff(rs.getString("Dropoff_location"));

                 // Create a Customer object for the associated Customer_ID
                    Customer customer = new Customer();
                    customer.setId(rs.getInt("Customer_ID"));
                    pkg.setCustomer(customer);  // Set the Customer object
                    
                    return pkg;  // Return the populated Package object
                }
            }
        }
        return null;
    }

    public List<Package> getAllPackages() throws Exception {
        String sql = "SELECT * FROM package";
        List<Package> packages = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Create a new Package object
                Package pkg = new Package();
                
             // Set Package fields from the result set
                pkg.setPackageId(rs.getInt("Package_ID"));
                pkg.setWeight(rs.getDouble("Weight"));
                pkg.setDimension(rs.getString("Dimension"));
                pkg.setDescription(rs.getString("Content_description"));
                pkg.setReceivername(rs.getString("receiver_name"));
                pkg.setDropoff(rs.getString("Dropoff_location"));

             // Create a Customer object for the associated Customer_ID
                Customer customer = new Customer();
                customer.setId(rs.getInt("Customer_ID"));
                pkg.setCustomer(customer);  // Set the Customer object
                
                packages.add(pkg);  // Add the Package object to the list
            }
        }
        return packages;
    }
    
    
 // Get unassigned packages (not linked to any courier in the delivery table)
    public static List<Package> getUnassignedPackages() throws Exception {
        String sql = "SELECT p.* " +
		            "FROM package p " +
		            "LEFT JOIN payment py ON p.Package_ID = py.Package_ID " +
		            "WHERE p.Package_status = 'Unassign' AND py.Payment_status = 'Complete'";
        List<Package> packages = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Package pkg = new Package();
                pkg.setPackageId(rs.getInt("Package_ID"));
                pkg.setWeight(rs.getDouble("Weight"));
                pkg.setDimension(rs.getString("Dimension"));
                pkg.setDescription(rs.getString("Content_description"));
                pkg.setReceivername(rs.getString("receiver_name"));
                pkg.setDropoff(rs.getString("Dropoff_location"));
                packages.add(pkg);
            }
        }
        return packages;
    }
    
    
 // ==================== PAYMENT CRUD OPERATIONS ====================

    public void insertPayment(Payment payment) throws Exception {
        String sql = "INSERT INTO payment (Package_ID, Payment_amount, Payment_method, Payment_time, Payment_status, Payment_receipt) "
        			+ "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Using the Payment object's getter methods
            stmt.setInt(1, payment.getPackage().getPackageId());  // Getting the Package_ID from the Package object
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getMethod());
            stmt.setTimestamp(4, new Timestamp(payment.getTime().getTime()));  // Converting Date to Timestamp
            stmt.setString(5, payment.getStatus());
            stmt.setString(6, payment.getReceipt());

            stmt.executeUpdate();
        }
    }

    public void updatePayment(Payment payment) throws Exception {
        String sql = "UPDATE payment SET Package_ID = ?, Payment_amount = ?, Payment_method = ?, Payment_time = ?, Payment_status = ?, Payment_receipt = ? WHERE Payment_ID = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Using the Payment object's getter methods to retrieve the values
        	stmt.setInt(1, payment.getPackage().getPackageId());  // Getting the Package_ID from the Package object
            stmt.setDouble(2, payment.getAmount());
            stmt.setString(3, payment.getMethod());
            stmt.setTimestamp(4, new Timestamp(payment.getTime().getTime()));  // Converting Date to Timestamp
            stmt.setString(5, payment.getStatus());
            stmt.setString(6, payment.getReceipt());
            stmt.setInt(7, payment.getPaymentId());  // Use Payment object's ID

            stmt.executeUpdate();
        }
    }
    
    public void updatePaymentStatus(int packageId, String status) throws Exception {
	     String sql = "UPDATE payment SET Payment_status = ? WHERE Package_ID = ?";

	     try (Connection conn = ConnectionManager.getConnection();
	          PreparedStatement stmt = conn.prepareStatement(sql)) {
	         stmt.setString(1, status);
	         stmt.setInt(2, packageId);
	         stmt.executeUpdate();
	     }
	 }
    
    public void updatePaymentMethod(int packageId, String method) throws Exception {
	     String sql = "UPDATE payment SET Payment_method = ? WHERE Package_ID = ?";

	     try (Connection conn = ConnectionManager.getConnection();
	          PreparedStatement stmt = conn.prepareStatement(sql)) {
	         stmt.setString(1, method);
	         stmt.setInt(2, packageId);
	         stmt.executeUpdate();
	     }
	 }

    public void deletePayment(Payment payment) throws Exception {
        String sql = "DELETE FROM payment WHERE Payment_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Use the getPaymentId() method of the Payment object to get the ID
            stmt.setInt(1, payment.getPaymentId());
            stmt.executeUpdate();
        }
    }

    public Payment getPaymentById(int id) throws Exception {
        String sql = "SELECT p.*, pk.* FROM payment p JOIN package pk ON p.Package_ID = pk.Package_ID WHERE p.Payment_ID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Populate Package object
                    Package pAckage = new Package();
                    pAckage.setPackageId(rs.getInt("Package_ID"));
                    pAckage.setWeight(rs.getDouble("Weight"));
                    pAckage.setDimension(rs.getString("Dimension"));
                    pAckage.setDescription(rs.getString("Content_description"));
                    pAckage.setReceivername(rs.getString("receiver_name"));
                    pAckage.setDropoff(rs.getString("Dropoff_location"));

                    // Populate and return Payment object
                    return new Payment(
                        rs.getInt("Payment_ID"),
                        pAckage,
                        rs.getDouble("Payment_amount"),
                        rs.getString("Payment_method"),
                        rs.getTimestamp("Payment_time"),
                        rs.getString("Payment_status"),
                        rs.getString("Payment_receipt")
                    );
                }
            }
        }
        return null;
    }

    public List<Payment> getAllPayments() throws Exception {
        String sql = "SELECT p.*, pk.* FROM payment p JOIN package pk ON p.Package_ID = pk.Package_ID";
        List<Payment> payments = new ArrayList<>();
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // Populate Package object
                Package pAckage = new Package();
                pAckage.setPackageId(rs.getInt("Package_ID"));
                pAckage.setWeight(rs.getDouble("Weight"));
                pAckage.setDimension(rs.getString("Dimension"));
                pAckage.setDescription(rs.getString("Content_description"));
                pAckage.setReceivername(rs.getString("receiver_name"));
                pAckage.setDropoff(rs.getString("Dropoff_location"));

                // Populate Payment object
                Payment payment = new Payment(
                    rs.getInt("Payment_ID"),
                    pAckage,
                    rs.getDouble("Payment_amount"),
                    rs.getString("Payment_method"),
                    rs.getTimestamp("Payment_time"),
                    rs.getString("Payment_status"),
                    rs.getString("Payment_receipt")
                );

                payments.add(payment);
            }
        }
        return payments;
    }
    
    public static Payment getPaymentByPackageId(int packageId) throws Exception {
        Payment payment = null;
        String query = "SELECT p.*, pk.* " +
                       "FROM payment p " +
                       "JOIN package pk ON p.Package_ID = pk.Package_ID " +
                       "WHERE p.Package_ID = ?";
        
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(query)) {
            
            preparedStatement.setInt(1, packageId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Populate Package object
                    Package pAckage = new Package();
                    pAckage.setPackageId(resultSet.getInt("Package_ID"));
                    pAckage.setWeight(resultSet.getDouble("Weight"));
                    pAckage.setDimension(resultSet.getString("Dimension"));
                    pAckage.setDescription(resultSet.getString("Content_description"));
                    pAckage.setReceivername(resultSet.getString("receiver_name"));
                    pAckage.setDropoff(resultSet.getString("Dropoff_location"));
                    
                    // Populate Payment object
                    payment = new Payment();
                    payment.setPaymentId(resultSet.getInt("Payment_ID"));
                    payment.setPackage(pAckage); // Link the fully populated Package object
                    payment.setAmount(resultSet.getDouble("Payment_amount"));
                    payment.setMethod(resultSet.getString("Payment_method"));
                    payment.setTime(resultSet.getTimestamp("Payment_time"));
                    payment.setStatus(resultSet.getString("Payment_status"));
                    payment.setReceipt(resultSet.getString("Payment_receipt"));
                }
            }
        }
        
        return payment;
    }
    
    public List<Payment> getPaymentsByPackage(int packageId) throws Exception {
	     List<Payment> payments = new ArrayList<>();
	     Connection conn = null;
	     PreparedStatement stmt = null;
	     ResultSet rs = null;

	     try {
	         conn = ConnectionManager.getConnection();
	         String sql = "SELECT p.*, pk.Weight, pk.Dimension, pk.Content_description, pk.receiver_name, pk.Dropoff_location \r\n"
	         		+ "FROM Payment p \r\n"
	         		+ "JOIN Package pk ON p.Package_ID = pk.Package_ID \r\n"
	         		+ "WHERE p.Package_ID = ?";
	         stmt = conn.prepareStatement(sql);
	         stmt.setInt(1, packageId);
	         rs = stmt.executeQuery();

	         while (rs.next()) {
	        	 
	        	// Populate Package object
                 Package pAckage = new Package();
                 pAckage.setPackageId(rs.getInt("Package_ID"));
                 pAckage.setWeight(rs.getDouble("Weight"));
                 pAckage.setDimension(rs.getString("Dimension"));
                 pAckage.setDescription(rs.getString("Content_description"));
                 pAckage.setReceivername(rs.getString("receiver_name"));
                 pAckage.setDropoff(rs.getString("Dropoff_location"));
                 
	        	 
	             Payment payment = new Payment();
	             payment.setPaymentId(rs.getInt("Payment_ID"));
	             payment.setPackage(pAckage);
	             payment.setAmount(rs.getDouble("Payment_amount"));
	             payment.setMethod(rs.getString("Payment_method"));
	             payment.setTime(rs.getTimestamp("Payment_time"));
	             payment.setStatus(rs.getString("Payment_status"));
	             payment.setReceipt(rs.getString("Payment_receipt"));
	             payments.add(payment);
	         }
	     } catch (Exception e) {
	         e.printStackTrace();
	     } finally {
	         if (rs != null) rs.close();
	         if (stmt != null) stmt.close();
	         if (conn != null) conn.close();
	     }
	     return payments;
	 }

    
}
