<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Login</title>
    <link rel="stylesheet" href="projectStyles.css">
    <style>
        body {
            background: linear-gradient(to right, #4caf50, #2e7d32);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
            font-family: Arial, sans-serif;
        }

        .login-box {
            background: white;
            border-radius: 15px;
            box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
            padding: 30px;
            width: 100%;
            max-width: 400px;
            text-align: center;
            border: 2px solid #4caf50;
        }

        .login-box h1 {
            font-size: 28px;
            margin-bottom: 20px;
            color: #2e7d32;
        }

        .login-box form {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .login-box .input-field input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ddd;
            border-radius: 8px;
            font-size: 16px;
        }

        .login-box .input-field input:focus {
            border-color: #4caf50;
            outline: none;
        }

        .login-box button {
            background: #4caf50;
            color: white;
            padding: 12px;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            transition: background 0.3s;
        }

        .login-box button:hover {
            background: #2e7d32;
        }

        .login-box p {
            margin-top: 10px;
            font-size: 14px;
            color: #666;
        }

        .login-box a {
            font-size: 14px;
            color: #4caf50;
            text-decoration: none;
            cursor: pointer;
        }

        .login-box a:hover {
            text-decoration: underline;
        }

        .password-toggle label {
            font-size: 14px;
            color: #555;
        }

        /* Error Notification Styles */
        .error-box {
            /*background: #ffcccc;
            color: #b71c1c;
            border: 1px solid #f44336;
            border-radius: 8px;
            padding: 10px;
            margin-bottom: 15px;
            text-align: center;
            font-size: 14px;*/
            
            color: red;
		    font-size: 12px;
		    margin-top: 10px; /* Add space below the input field */
		    text-align: middle;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <h1>Admin Log In</h1>
        <form action="AdminLoginController" method="POST">
            <div class="input-field">
                <input type="text" id="adminEmail" name="adminEmail" placeholder="Email" required>
            </div>

            <div class="input-field">
                <input type="password" id="adminPassword" name="adminPassword" placeholder="Password" required>
                <%-- Error message display --%>
		        <% if (request.getAttribute("errorMessage") != null) { %>
		            <div class="error-box">
		                <%= request.getAttribute("errorMessage") %>
		            </div>
		        <% } %>
            </div>

            <div class="password-toggle">
                <input type="checkbox" id="show-password" onclick="togglePasswordVisibility()">
                <label for="show-password">Show Password</label>
            </div>

            <button type="submit">Log In</button>
        </form>
    </div>

    <script>
        function togglePasswordVisibility() {
            const passwordField = document.getElementById('adminPassword');
            passwordField.type = passwordField.type === 'password' ? 'text' : 'password';
        }
    </script>
</body>
</html>
