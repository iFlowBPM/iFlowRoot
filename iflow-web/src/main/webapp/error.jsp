<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Error Page</title>
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #f5f5f5;
      margin: 0;
      padding: 0;
    }

    .container {
      max-width: 500px;
      margin: 0 auto;
      padding: 20px;
      background-color: #fff;
      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
      border-radius: 4px;
      margin-top: 50px;
    }

    h1 {
      color: #333;
      font-size: 24px;
      margin-bottom: 20px;
    }

    p {
      color: #666;
      margin-bottom: 10px;
    }
  </style>
</head>
<body>
<div class="container">
  <h1>Unik BPM Platform Error Page Description</h1>
  <p>HTTP Error Status Code: ${status}</p>
  <p>HTTP Error Status Code Description: ${description}</p>
</div>
</body>
</html>
