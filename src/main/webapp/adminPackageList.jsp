<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="procourier.model.Package" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Packages List</title>
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
    	/*Button*/
    	.btn-choosecourier {
	        background-color: darkorange;
	        color: white;
	        padding: 5px 12px;
	        border: none;
	        cursor: pointer;
	        font-size: 14px;
	        border-radius: 5px;
	    }
	    
	    .btn-choosecourier:hover {
			    background: #E65100; 
			    /*border-color: #ff5722;
			    color: #ff5722;*/
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
        <h1 class="title">Packages List</h1>
    	<div class="table-container">
            <table class="delivery-table">
                <thead>
                    <tr>
		                <th>Package ID</th>
		                <th>Action</th>
		            </tr>
                </thead>
		        <tbody>
		            <%-- Loop through the unassignedPackages attribute to display package data --%>
		            <%
		            List<procourier.model.Package> unassignedPackages = (List<procourier.model.Package>) request.getAttribute("unassignedPackages");
		            if (unassignedPackages != null && !unassignedPackages.isEmpty()) {
		                for (procourier.model.Package pkg : unassignedPackages) {
		            %>
		            <tr>
		                <td><%= pkg.getPackageId() %></td>
		                <td>
		                    <form action="AssignCourierController" method="get">
		                        <input type="hidden" name="packageId" value="<%= pkg.getPackageId() %>" />
		                        <button type="submit" class="btn-choosecourier">Choose Courier</button>
		                    </form>
		                </td>
		            </tr>
		            <%  
		                    }
		                } else {
		            %>
		            <tr>
		                <td colspan="2">No unassigned packages available.</td>
		            </tr>
		            <% } %>
		        </tbody>
    		</table>
    	</div>
    </div>
</body>
</html>