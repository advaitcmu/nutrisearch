<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="org.bson.Document" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>NutriSearch Dashboard</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/dashboard.css">
</head>
<body>

<h1>🥗 NutriSearch Dashboard</h1>

<%-- Analytics Cards --%>
<div class="section">
    <h2>Analytics</h2>
    <div class="cards">
        <div class="card">
            <h3>🔥 Top Search Query</h3>
            <p>${topQuery}</p>
        </div>
        <div class="card">
            <h3>⚡ Avg Response Time (24h)</h3>
            <p>${avgDuration} ms</p>
        </div>
        <div class="card">
            <h3>❌ Error Rate</h3>
            <p>${errorRate}%</p>
        </div>
    </div>

    <%-- Top 5 Queries Table --%>
    <h3>Top 5 Most Searched Queries</h3>
    <table>
        <tr>
            <th>Query</th>
            <th>Search Count</th>
        </tr>
        <%
            List<Document> topQueries = (List<Document>) request.getAttribute("topQueries");
            if (topQueries != null) {
                for (Document d : topQueries) {
        %>
        <tr>
            <td><%= d.getString("_id") %></td>
            <td><%= d.getInteger("count") %></td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>

<%-- Recent Logs --%>
<div class="section">
    <h2>Recent Logs (last 50)</h2>
    <table>
        <tr>
            <th>Timestamp</th>
            <th>Query</th>
            <th>Client IP</th>
            <th>Status</th>
            <th>Results</th>
            <th>Duration</th>
        </tr>
        <%
            List<Document> recentLogs = (List<Document>) request.getAttribute("recentLogs");
            if (recentLogs != null) {
                for (Document log : recentLogs) {
                    int status = log.getInteger("responseStatus", 0);
                    String statusClass = (status == 200) ? "status-200" : "status-err";
        %>
        <tr>
            <td><%= log.getString("timestamp") %></td>
            <td><%= log.getString("query") %></td>
            <td><%= log.getString("clientIp") %></td>
            <td class="<%= statusClass %>"><%= status %></td>
            <td><%= log.getInteger("resultCount", 0) %></td>
            <td><%= log.getLong("durationMs") %> ms</td>
        </tr>
        <%
                }
            }
        %>
    </table>
</div>

</body>
</html>
