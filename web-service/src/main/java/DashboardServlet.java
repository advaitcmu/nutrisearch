import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bson.Document;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class DashboardServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException, ServletException {
    resp.setContentType("text/html");
    resp.setCharacterEncoding("UTF-8");

    MongoDBClient db = MongoDBClient.getInstance();

    List<Document> recentLogs  = db.getRecentLogs(50);
    List<Document> topQueries  = db.getTopQueries();
    double avgDuration         = db.getAvgDurationLast24h();
    double errorRate           = db.getErrorRate();

    String topQuery = topQueries.isEmpty() ? "N/A" : topQueries.get(0).getString("_id");

    // Set as request attributes for the JSP
    req.setAttribute("recentLogs",  recentLogs);
    req.setAttribute("topQueries",  topQueries);
    req.setAttribute("avgDuration", String.format("%.0f", avgDuration));
    req.setAttribute("errorRate",   String.format("%.1f", errorRate));
    req.setAttribute("topQuery",    topQuery);

    // Forward to view
    req.getRequestDispatcher("/index.jsp").forward(req, resp);
  }
}