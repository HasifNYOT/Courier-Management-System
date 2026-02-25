<%@ page session="true" %>
<%@ page import="java.util.List" %>
<%@ page import="procourier.model.Courier" %>
<%@ page import="procourier.model.Customer" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Accounts</title>
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
    	.btn-delete { 
	        background-color: #DC3545;
	        color: white;
	        padding: 4px 12px;
	        border: none;
	        cursor: pointer;
	        font-size: 13px;
	        border-radius: 5px;
	    }
	    
	    .btn-delete:hover {
		    background: #B71C1C; 
		    /*border-color: #ff5722;
		    color: #ff5722;*/
		}
    </style>
</head>
<body>
    <!-- Navbar Section -->
    <nav class="navbar">
        <!-- Project Logo -->
        <div class="navbar__logo">
            <a href="adminHome.jsp">
                <img src="images/project_logo.png" alt="Project Logo" id="project-logo">
            </a>
        </div>
        <!-- Navigation Links -->
        <ul class="navbar__menu">
            <li><a href="PackageListController">Package List</a></li>
            <li><a href="ManageAccountController">Manage Account</a></li>
            <li><a href="DeliveryReportController">Delivery Report</a></li>
        </ul>
        <!-- Profile Icon -->
        <div class="navbar__profile">
            <img src="${profileImagePath}" alt="Profile Icon" id="profile-icon" onclick="toggleDropdown()">
            <div id="profile-dropdown" class="profile-dropdown">
                <a href="AdminProfileController">My Account</a>
                <a href="AdminLogoutController">Log Out</a>
            </div>
        </div>
    </nav>

    <!-- Main Content Section -->
    <div class="container">
        <h1 class="title">Manage Accounts</h1>

        <!-- Couriers Table -->
        <br><br><br>
        <h2>Couriers</h2>
        <div class="table-container">
            <table class="delivery-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone Number</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty courierList}">
                            <c:forEach var="courier" items="${courierList}">
                                <tr>
                                    <td>${courier.id}</td>
                                    <td>${courier.name}</td>
                                    <td>${courier.email}</td>
                                    <td>${courier.phone}</td>   
                                   <td>
                                        <form action="ManageAccountController" method="POST">
                                            <input type="hidden" name="id" value="${courier.id}">
                                            <input type="hidden" name="type" value="courier">
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5">No couriers found.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
        <br><br><br><br>

        <!-- Customers Table -->
        <h2>Customers</h2>
        <div class="table-container">
            <table class="delivery-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone Number</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${not empty customerList}">
                            <c:forEach var="customer" items="${customerList}">
                                <tr>
                                    <td>${customer.id}</td>
                                    <td>${customer.name}</td>
                                    <td>${customer.email}</td>
                                    <td>${customer.phone}</td>
                                    <td>
                                        <form action="ManageAccountController" method="POST">
                                            <input type="hidden" name="id" value="${customer.id}">
                                            <input type="hidden" name="type" value="customer">
                                            <button type="submit" class="btn-delete">Delete</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr>
                                <td colspan="5">No customers found.</td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
