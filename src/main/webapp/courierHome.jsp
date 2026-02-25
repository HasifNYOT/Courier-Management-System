<%@ page session="true" %>
<%@ page import="java.sql.*" %>
<%@ page import="connection.ConnectionManager" %>

<% 
	//Check if the courier is logged in
	String courierName = (String) session.getAttribute("courierName");
	Integer courierId = (Integer) session.getAttribute("courierId"); // Assuming courierId is stored in session
	if (courierName == null || courierId == null) {
	    response.sendRedirect("userlogin.jsp?message=unauthorized");
	    return;
	}
	
	// Database connection setup
	Connection connection = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	String profileImagePath = "images/default-profile.png"; // Default profile image path
	
	try {
	    // Get a connection from your connection manager
	    connection = ConnectionManager.getConnection();
	
	    // Query to get the courier profile image from the database
	    String sql = "SELECT Courier_profileImage FROM courier WHERE Courier_ID = ?";
	    ps = connection.prepareStatement(sql);
	    ps.setInt(1, courierId); // Use courierId from session
	    rs = ps.executeQuery();
	
	    // Check if courier exists and retrieve the profile image
	    if (rs.next()) {
	        profileImagePath = rs.getString("Courier_profileImage");
	        if (profileImagePath == null || profileImagePath.isEmpty()) {
	            profileImagePath = "images/default-profile.png"; // Fallback if no image in database
	        }
	    }

    } catch (SQLException e) {
        e.printStackTrace();
        response.sendRedirect("error.jsp?message=database_error");
    } finally {
        // Close the resources
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Courier Dashboard</title>
    <link rel="stylesheet" href="projectStyles.css">
    <script>
        function toggleDropdown() {
            document.getElementById("profile-dropdown").classList.toggle("show");
        }

        // Close the dropdown if clicked outside
        window.onclick = function(event) {
            if (!event.target.matches('#profile-icon')) {
                var dropdown = document.getElementById("profile-dropdown");
                if (dropdown.classList.contains("show")) {
                    dropdown.classList.remove("show");
                }
            }
        }
        
        document.addEventListener("DOMContentLoaded", function () {
            let currentAdIndex = 0;
            const ads = document.querySelectorAll(".ads img");
            
            function showAd(index) {
                ads.forEach((ad, i) => {
                    ad.style.opacity = i === index ? "1" : "0";
                });
            }
            
            showAd(currentAdIndex);
            
            function rotateAds() {
                currentAdIndex = (currentAdIndex + 1) % ads.length;
                showAd(currentAdIndex);
            }
            
            setInterval(rotateAds, 3000);
        });
    </script>
    <style>
    	body {
	    font-family: Arial, sans-serif;
	    margin: 0;
	    padding: 0;
	    background: #f4f4f4;
	    animation: fadeIn 1s ease-in-out;
	}
	
	@keyframes fadeIn {
	    from { opacity: 0; }
	    to { opacity: 1; }
	}
	
	.container {
	    max-width: 1200px;
	    margin: auto;
	    padding: 20px;
	    text-align: center;
	}
	
	.section {
	    background: white;
	    padding: 40px;
	    margin: 20px 0;
	    border-radius: 10px;
	    box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
	    animation: slideUp 1s ease-in-out;
	}
	
	@keyframes slideUp {
	    from { transform: translateY(50px); opacity: 0; }
	    to { transform: translateY(0); opacity: 1; }
	}
	
	/* Advertisement Section */
	.ads {
	    position: relative;
	    width: 100%;
	    height: 250px;
	    display: flex;
	    align-items: center;
	    justify-content: center;
	    overflow: hidden;
	    border-radius: 10px;
	}
	
	.ads img {
	    max-width: 100%;
	    max-height: 100%;
	    object-fit: cover;
	    position: absolute;
	    opacity: 0;
	    transition: opacity 1s ease-in-out;
	}
	
	.ads img:first-child {
	    opacity: 1;
	}
	
	/* Staff Images Hover Effect */
	.staff {
	    display: flex;
	    flex-wrap: wrap;
	    justify-content: center;
	}
	
	.staff img {
	    width: 120px;
	    height: 120px;
	    border-radius: 50%;
	    margin: 10px;
	    transition: transform 0.3s ease, box-shadow 0.3s ease;
	}
	
	.staff img:hover {
	    transform: scale(1.1);
	    box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.3);
	}
    
    
        .navbar__profile {
            position: relative;
            display: inline-block;
        }
        .profile-dropdown {
            display: none;
            position: absolute;
            right: 0;
            background-color: white;
            min-width: 150px;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.2);
            border-radius: 5px;
            overflow: hidden;
            z-index: 1000;
        }
        .profile-dropdown a {
            display: block;
            padding: 10px;
            color: black;
            text-decoration: none;
        }
        .profile-dropdown a:hover {
            background-color: #f1f1f1;
        }
        .show {
            display: block;
        }
    </style>
</head>
<body>
    <header>
        <nav class="navbar">
            <!-- Project Logo -->
            <div class="navbar__logo">
                <a href="courierHome.jsp">
                    <img src="images/project_logo.png" alt="Project Logo" id="project-logo">
                </a>
            </div>
            <!-- Navigation Links -->
            <ul class="navbar__menu">
                <li><a href="ReceiveReqController">Receive Request</a></li>
                <li><a href="OngoingDeliveryController">Ongoing Delivery</a></li>
            </ul>
             <!-- Profile Icon -->
            <div class="navbar__profile">
                <img src="<%= profileImagePath %>" alt="Profile Icon" id="profile-icon" onclick="toggleDropdown()">
                <div id="profile-dropdown" class="profile-dropdown">
                    <a href="CourierProfileController">My Account</a>
                    <a href="UserLogoutController">Log Out</a>
                </div>
            </div>
        </nav>
    </header>
    <main>
        <h1>Welcome, <%= courierName.split(" ")[0] %>!</h1>
        <p>Select a task from the navigation bar to get started.</p>
        <div class="section ads">
            <h2>Latest Offers</h2>
            <img src="images/banner1.jpg" alt="Special Promotion 1">
            <img src="images/banner2.jpg" alt="Special Promotion 2">
            <img src="images/banner3.jpg" alt="Special Promotion 3">
        </div>
    	<div class="section company-background">
            <h2>About Our Company</h2>
            <img src="images/company.jpg" alt="About Our Company" style="width:100%; margin-top:20px; border-radius:10px;"><br>
            <p style="text-align: justify;">We have been dedicated to delivering exceptional courier services with speed, security, and reliability. 
               Our extensive network ensures that parcels reach their destinations efficiently,
               whether locally or internationally. We prioritize customer satisfaction by offering real-time tracking,
               responsive customer support, and a commitment to handling every shipment with the utmost care. 
               Whether it's urgent documents or valuable packages, 
               we go the extra mile to ensure safe and timely delivery, making us a trusted partner in logistics.</p>
        </div>
        
        <div class="section staff">
            <div>
            	<h2 style="text-align: center;">Meet Our Team</h2><br>
                <img src="images/staff1.jpg" alt="Staff Member 1">
                <img src="images/staff2.jpg" alt="Staff Member 2">
                <img src="images/staff3.jpg" alt="Staff Member 3">
                <img src="images/staff4.jpg" alt="Staff Member 4">
            </div>
        </div>
    </main>

    <footer>
        <p>&copy; 2025 Courier Management System. All Rights Reserved.</p>
    </footer>
</body>
</html>