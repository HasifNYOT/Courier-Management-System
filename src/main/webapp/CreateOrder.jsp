<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>
<%@ page session="true" %>
<%@ page import="java.sql.*" %>
<%@ page import="connection.ConnectionManager" %>
<%
//Check if the courier is logged in
	String customerName = (String) session.getAttribute("customerName");
	Integer customerId = (Integer) session.getAttribute("customerId"); // Assuming courierId is stored in session
	if (customerName == null || customerId == null) {
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
	    String sql = "SELECT Customer_profileImage FROM customer WHERE Customer_ID = ?";
	    ps = connection.prepareStatement(sql);
	    ps.setInt(1, customerId); // Use courierId from session
	    rs = ps.executeQuery();
	
		// Check if courier exists and retrieve the profile image
		if (rs.next()) {
		    profileImagePath = rs.getString("Customer_profileImage");
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
    <title>Create Order</title>
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
    </script>
    <style>
        .container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            background-color: #fff;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            border-radius: 10px;
        }

        h1.title {
            font-size: 28px;
            color: #333;
            text-align: center;
            margin-bottom: 20px;
        }

        .form-container {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        label {
            font-size: 14px;
            color: #555;
        }

        input, textarea {
            padding: 10px;
            border-radius: 5px;
            border: 1px solid #ddd;
            font-size: 16px;
            width: 100%;
            box-sizing: border-box;
        }

        textarea {
            resize: none;
            min-height: 100px;
            overflow: hidden;
        }

        input:focus, textarea:focus {
            outline: none;
            border-color: #ff914d;
            box-shadow: 0 0 5px rgba(255, 145, 77, 0.3);
        }

        .form-actions {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            margin-top: 20px;
        }

        .btn {
            padding: 12px 25px;
            font-size: 16px;
            font-weight: bold;
            border-radius: 8px;
            text-decoration: none;
            width: 48%;
            text-align: center;
            cursor: pointer;
            border: none;
            transition: all 0.3s ease;
        }

        .btn-confirm {
            background-color: #28A745;
            color: white;
        }

        .btn-confirm:hover {
            background-color: #218838;
            transform: scale(1.05);
        }

        .btn-cancel {
            background-color: #DC3545;
            color: white;
        }

        .btn-cancel:hover {
            background-color: #C82333;
            transform: scale(1.05);
        }

        @media (max-width: 600px) {
            .form-actions {
                flex-direction: column;
            }
            .btn {
                width: 100%;
                margin-bottom: 10px;
            }
        }
        /*new*/
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
    <!-- Navbar Section -->
    <nav class="navbar">
        <div class="navbar__logo">
            <a href="customerHome.jsp">
                <img src="images/project_logo.png" alt="Project Logo" id="project-logo">
            </a>
        </div>
        <ul class="navbar__menu">
            <li><a href="CreateOrder.jsp">Create Order</a></li>
            <li><a href="OrderListController">Draft Orders</a></li>
        </ul>
        <div class="navbar__profile">
            <img src="<%= profileImagePath %>" alt="Profile Icon" id="profile-icon" onclick="toggleDropdown()">
                <div id="profile-dropdown" class="profile-dropdown">
                    <a href="CustomerProfileController">My Account</a>
                    <a href="UserLogoutController">Log Out</a>
                </div>
        </div>
    </nav>
    <!-- Main Content Section -->
    <div class="container">
        <h1 class="title">Create Order</h1>
        <form action="CreateDeliveryController" method="POST" enctype="multipart/form-data" class="form-container" onsubmit="return validateForm()">
            <input type="hidden" name="customerId" value="<%= customerId %>">

            <label for="receiverName">Receiver Name:</label>
            <input type="text" id="receiverName" name="receiverName" required><br>

            <label for="dropOffLocation">Drop-off Location:</label>
            <input type="text" id="dropOffLocation" name="dropOffLocation" required><br>

            <label for="weight">Package Weight (kg):</label>
            <input type="number" step="0.01" id="weight" name="weight" required 
			    min="0.1" max="70" placeholder="Max 70kg" oninput="validateWeight()">
			<small id="weightError" style="color: red; display: none;">Weight must be between 0.1 and 70 kg</small><br>
            
            <label for="dimension">Dimension (wide x length x height):</label>
            <input type="text" id="dimension" name="dimension" required 
			    placeholder="e.g., 10x20x30" oninput="validateDimension()">
			<small id="dimensionError" style="color: red; display: none;">Invalid format! Use width x length x height</small><br>
					
            <label for="description">Content Description:</label>
            <textarea id="description" name="description" required></textarea><br>

            <label for="packageImage">Package Image:</label>
            <input type="file" id="packageImage" name="packageImage" accept="image/*" required><br>

            <div class="form-actions">
                <button type="submit" class="btn btn-confirm">Confirm</button>
                <button type="button" class="btn btn-cancel" onclick="window.location.href='customerHome.jsp'">Cancel</button>
            </div>
        </form>
    </div>
</body>
<script>
	function validateDimension() {
	    let dimensionInput = document.getElementById("dimension");
	    let errorText = document.getElementById("dimensionError");
	    let pattern = /^[0-9]+x[0-9]+x[0-9]+$/;  // Regular expression to match "width x length x height"
	
	    if (!pattern.test(dimensionInput.value)) {
	        errorText.style.display = "inline";
	        dimensionInput.style.borderColor = "red";
	    } else {
	        errorText.style.display = "none";
	        dimensionInput.style.borderColor = "#ddd";
	    }
	}
	
	function checkDimension() {
	    let dimensionInput = document.getElementById("dimension").value;
	    let pattern = /^[0-9]+x[0-9]+x[0-9]+$/; 
	
	    if (!pattern.test(dimensionInput)) {
	        alert("Invalid dimension format! Use width x length x height.");
	        return false;  // Prevent form submission
	    }
	    return true;  // Allow form submission
	}
	
	function validateWeight() {
	    let weightInput = document.getElementById("weight");
	    let weightError = document.getElementById("weightError");
	
	    if (weightInput.value > 70 || weightInput.value < 0.1) {
	        weightError.style.display = "inline";
	        weightInput.style.borderColor = "red";
	    } else {
	        weightError.style.display = "none";
	        weightInput.style.borderColor = "#ddd";
	    }
	}
	
	function checkWeight() {
	    let weightValue = document.getElementById("weight").value;
	    
	    if (weightValue > 70 || weightValue < 0.1) {
	        alert("Invalid weight! The maximum allowed weight is 70kg.");
	        return false; // Prevent form submission
	    }
	    return true; // Allow form submission
	}
	
	function validateForm() {
	    return checkWeight() && checkDimension();
	}
</script>
	<% 
        String message = request.getParameter("message");
        if ("packageCreated".equals(message)) {
    %>
        <script>
            alert("Order Created!");
        </script>
    <% } %>

</html>
