<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<%@ page import="java.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="connection.ConnectionManager" %>
<%@ page session="true" %>
<%
    String adminName = (String) session.getAttribute("adminName");
    Integer adminId = (Integer) session.getAttribute("adminId");
    if (adminName == null) {
        response.sendRedirect("adminlogin.jsp?message=unauthorized");
        return;
    }

    // Database connection setup
    Connection connection = null;
    PreparedStatement ps = null;
    ResultSet rs = null;
    String profileImagePath = "images/default-profile.png"; // Default profile image path
    int totalCouriers = 0, totalCustomers = 0, totalCompletedDeliveries = 0;

    try {
        connection = ConnectionManager.getConnection();

        // Fetch total couriers
        ps = connection.prepareStatement("SELECT COUNT(*) FROM courier");
        rs = ps.executeQuery();
        if (rs.next()) totalCouriers = rs.getInt(1);

        // Fetch total customers
        ps = connection.prepareStatement("SELECT COUNT(*) FROM customer");
        rs = ps.executeQuery();
        if (rs.next()) totalCustomers = rs.getInt(1);

        // Fetch total completed deliveries
        ps = connection.prepareStatement("SELECT COUNT(*) FROM delivery WHERE Delivery_status = 'completed'");
        rs = ps.executeQuery();
        if (rs.next()) totalCompletedDeliveries = rs.getInt(1);

        // Fetch admin profile image
        ps = connection.prepareStatement("SELECT Admin_profileImage FROM admin WHERE Admin_ID = ?");
        ps.setInt(1, adminId);
        rs = ps.executeQuery();
        if (rs.next()) {
            profileImagePath = rs.getString("Admin_profileImage");
            if (profileImagePath == null || profileImagePath.isEmpty()) {
                profileImagePath = "images/default-profile.png";
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        response.sendRedirect("error.jsp?message=database_error");
    } finally {
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
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="projectStyles.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        body {
            display: flex;
            flex-direction: column;
            min-height: 100vh;
            margin: 0;
            font-family: Arial, sans-serif;
        }
        main {
            flex: 1;
            padding: 20px;
        }
        footer {
            text-align: center;
            background: #fff;
            padding: 10px 0;
            position: relative;
            bottom: 0;
            width: 100%;
        }
        .dashboard {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 20px;
        }
        .card {
            background: white;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
            padding: 20px;
            text-align: center;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }
        .card h3 {
            margin-bottom: 10px;
            font-size: 18px;
            color: #333;
        }
        .chart-container {
            width: 100%;
            max-width: 300px; /* Restrict maximum width */
            height: 250px;    /* Fixed height */
            display: flex;
            justify-content: center;
            align-items: center;
            margin: 0 auto;
        }
        canvas {
            max-width: 100%; /* Ensure the canvas doesn't exceed the container */
            height: auto;    /* Maintain aspect ratio */
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
        
        footer {
		    text-align: center;
		    padding: 20px;
		    background-color: #333;
		    color: white;
		    font-size: 14px;
		}
    </style>
</head>
<body>
    <header>
        <nav class="navbar">
            <div class="navbar__logo">
                <a href="adminHome.jsp">
                    <img src="images/project_logo.png" alt="Project Logo" id="project-logo">
                </a>
            </div>
            <ul class="navbar__menu">
                <li><a href="PackageListController">Package List</a></li>
                <li><a href="ManageAccountController">Manage Users Account</a></li>
                <li><a href="DeliveryReportController">Delivery Report</a></li>
            </ul>
            <div class="navbar__profile">
                <img src="<%= profileImagePath %>" alt="Profile Icon" id="profile-icon" onclick="toggleDropdown()">
	            <div id="profile-dropdown" class="profile-dropdown">
	                <a href="AdminProfileController">My Account</a>
	                <a href="AdminLogoutController">Log Out</a>
	            </div>
            </div>
        </nav>
    </header>
    <main>
        <h1>Welcome, <%= adminName.split(" ")[0] %>!</h1>
        <div class="dashboard">
            <div class="card">
                <h3>User Distribution</h3>
                <div class="chart-container">
                    <canvas id="trafficChart"></canvas>
                </div>
            </div>
            <div class="card">
                <h3>Completed Deliveries</h3>
                <div class="chart-container">
                    <canvas id="completedTasksChart"></canvas>
                </div>
            </div>
        </div>
    </main>

    <script>
        window.onload = function () {
            initCharts();
        };

        function initCharts() {
            // User Distribution Chart
            new Chart(document.getElementById('trafficChart').getContext('2d'), {
                type: 'doughnut',
                data: {
                    labels: ['Couriers', 'Customers'],
                    datasets: [{
                        data: [<%= totalCouriers %>, <%= totalCustomers %>],
                        backgroundColor: ['#4285F4', '#FB7E57']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'top'
                        }
                    }
                }
            });

            // Completed Deliveries Chart
            new Chart(document.getElementById('completedTasksChart').getContext('2d'), {
                type: 'bar',
                data: {
                    labels: ['Completed Deliveries'],
                    datasets: [{
                        label: 'Deliveries',
                        data: [<%= totalCompletedDeliveries %>],
                        backgroundColor: ['#34A853']
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'top'
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true
                        }
                    }
                }
            });
        }
    </script>
    <footer>
        <br>
        <p>&copy; 2025 Courier Management System. All Rights Reserved.</p>
        <br>
    </footer>
</body>
</html>
