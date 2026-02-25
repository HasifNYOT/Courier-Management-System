<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Delivery Report</title>
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
        /* Container */
        .container {
            width: 90%;
            max-width: 1200px;
            margin: 30px auto;
            background: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
        }

        h1 {
            text-align: center;
            color: #333;
        }

        /* Delivery Table */
        .delivery-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        .delivery-table th, .delivery-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        .delivery-table th {
            background-color: #FF5F1F;
            color: white;
        }

        .delivery-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }

        /* Expandable Details Section */
        .details-row {
            display: none;
            background: #eef2ff;
        }

        .details-container {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 10px;
            padding: 15px;
            background: white;
            border-radius: 8px;
            box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.1);
        }

        .details-container p {
            margin: 5px 0;
        }

        .details-container strong {
            color: #333;
        }

        .expand-button {
            background-color: #28a745;
            padding: 6px 12px;
            color: white;
            border: none;
            cursor: pointer;
            border-radius: 5px;
            font-weight: bold;
        }

        .expand-button:hover {
            background-color: #218838;
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
        
        /*Button*/
    	.btn-search {
	        background-color: green;
	        color: white;
	        padding: 4px 12px;
	        border: none;
	        cursor: pointer;
	        font-size: 13px;
	        border-radius: 5px;
	    }
	    
	    .btn-search:hover {
		    background: darkgreen; 
		    /*border-color: #ff5722;
		    color: #ff5722;*/
		}
    </style>
</head>
<body>
    <!-- Navbar -->
    <nav class="navbar">
        <div class="navbar__logo">
            <a href="adminHome.jsp">
                <img src="images/project_logo.png" alt="Project Logo" id="project-logo">
            </a>
        </div>
        <ul class="navbar__menu">
            <li><a href="PackageListController">Package List</a></li>
            <li><a href="ManageAccountController">Manage Account</a></li>
            <li><a href="DeliveryReportController">Delivery Report</a></li>
        </ul>
        <div class="navbar__profile">
            <img src="${profileImagePath}" alt="Profile Icon" id="profile-icon" onclick="toggleDropdown()">
	            <div id="profile-dropdown" class="profile-dropdown">
	                <a href="AdminProfileController">My Account</a>
	                <a href="AdminLogoutController">Log Out</a>
	            </div>
        </div>
    </nav>

    <!-- Main Content -->
    <div class="container">
        <h1>All Delivery Reports</h1>

        <!-- Search Form -->
        <form action="DeliveryReportController" method="GET"><br>
            <label for="deliveryID">Search by Delivery ID:</label>
            <input type="text" id="deliveryID" name="deliveryID" placeholder="Enter Delivery ID">
            <button type="submit" class="btn-search">Search</button>
        </form>

        <c:if test="${not empty deliveryReportList}">
            <table class="delivery-table">
                <thead>
                    <tr>
                        <th>Delivery ID</th>
                        <th>Delivery Date</th>
                        <th>Pickup Location</th>
                        <th>Courier Name</th>
                        <th>Customer Name</th>
                        <th>Payment Amount</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="report" items="${deliveryReportList}">
                        <!-- Main Row -->
                        <tr>
                            <td>${report.deliveryID}</td>
                            <td>${report.deliveryDate}</td>
                            <td>${report.pickupLocation}</td>
                            <td>${report.courierName}</td>
                            <td>${report.customerName}</td>
                            <td>${report.paymentAmount}</td>
                            <td>
                                <button class="expand-button" onclick="toggleDetails('details-${report.deliveryID}')">
                                    View Details
                                </button>
                            </td>
                        </tr>

                        <!-- Details Row -->
                        <tr id="details-${report.deliveryID}" class="details-row">
                            <td colspan="7">
                                <div class="details-container">
                                    <p><strong>Courier Email:</strong> ${report.courierEmail}</p>
                                    <p><strong>Courier Phone:</strong> ${report.courierPhone}</p>
                                    <p><strong>Customer Email:</strong> ${report.customerEmail}</p>
                                    <p><strong>Customer Phone:</strong> ${report.customerPhone}</p>
                                    <p><strong>Customer Address:</strong> ${report.customerAddress}</p>
                                    <p><strong>Receiver Name:</strong> ${report.receiverName}</p>
                                    <p><strong>Dropoff Location:</strong> ${report.dropoffLocation}</p>
                                    <p><strong>Payment Method:</strong> ${report.paymentMethod}</p>
                                    <p><strong>Payment Status:</strong> ${report.paymentStatus}</p>
                                    <p><strong>Payment Time:</strong> ${report.paymentTime}</p>
                                    <p><strong>Payment Receipt:</strong> ${report.paymentReceipt}</p>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <c:if test="${empty deliveryReportList}">
            <p style="text-align:center; color: red;">No delivery reports available</p>
        </c:if>
    </div>

    <script>
        function toggleDetails(rowId) {
            const detailsRow = document.getElementById(rowId);
            detailsRow.style.display = (detailsRow.style.display === "none" || detailsRow.style.display === "") 
                ? "table-row" 
                : "none";
        }
    </script>
</body>
</html>
