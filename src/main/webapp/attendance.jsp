<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>

<%
    if (request.getAttribute("logs") == null) {
        response.sendRedirect("fingerprint-data?page=logs");
        return;
    }
%>

<html>
<head>
    <title>Attendance Logs</title>

    <style>
        body {
            font-family: Arial;
            background-color: #f4f6f9;
        }

        h2 {
            color: #333;
        }

        table {
            width: 80%;
            border-collapse: collapse;
            background: white;
        }

        table, th, td {
            border: 1px solid #ddd;
        }

        th {
            background: #28a745;
            color: white;
            padding: 10px;
        }

        td {
            padding: 10px;
            text-align: center;
        }

        tr:hover {
            background: #f1f1f1;
        }

        .success {
            color: green;
            font-weight: bold;
        }

        .error {
            color: red;
            font-weight: bold;
        }
    </style>
</head>

<body>

<h2>📋 Fingerprint Attendance Logs</h2>

<%
    List<String> logs = (List<String>) request.getAttribute("logs");

    if (logs != null && !logs.isEmpty()) {
%>

<table>
    <tr>
        <th>#</th>
        <th>Log Details</th>
    </tr>

    <%
        int i = 1;
        for (String log : logs) {
    %>

    <tr>
        <td><%= i++ %></td>
        <td>
            <% if (log.contains("❌")) { %>
            <span class="error"><%= log %></span>
            <% } else if (log.contains("✅")) { %>
            <span class="success"><%= log %></span>
            <% } else { %>
            <%= log %>
            <% } %>
        </td>
    </tr>

    <%
        }
    %>

</table>

<%
} else {
%>

<p>No attendance logs found</p>

<%
    }
%>

</body>
</html>