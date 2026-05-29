import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONObject;

public class FoodSearchServlet extends HttpServlet {

  private final OpenFoodFactsClient foodClient = new OpenFoodFactsClient();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.setHeader("Access-Control-Allow-Origin", "*");

    String query = req.getParameter("q");
    String clientIp = req.getRemoteAddr();
    long startTime = System.currentTimeMillis();

    PrintWriter out = resp.getWriter();

    if (query == null || query.trim().isEmpty()) {
      resp.setStatus(400);
      out.print(new JSONObject()
          .put("error", "Missing required parameter: q")
          .toString());
      logRequest("", clientIp, 400, 0, startTime);
      return;
    }

    query = query.trim();

    try {
      JSONArray results = foodClient.search(query);

      if (results.isEmpty()) {
        resp.setStatus(404);
        JSONObject body = new JSONObject()
            .put("error", "No products found for: " + query)
            .put("query", query)
            .put("results", new JSONArray());
        out.print(body.toString());
        logRequest(query, clientIp, 404, 0, startTime);
        return;
      }

      resp.setStatus(200);
      JSONObject body = new JSONObject()
          .put("query", query)
          .put("count", results.length())
          .put("results", results);
      out.print(body.toString());
      logRequest(query, clientIp, 200, results.length(), startTime);
    } catch (Exception e) {
      resp.setStatus(500);
      out.print(new JSONObject()
          .put("error", "Internal server error: " + e.getMessage())
          .toString());
      logRequest(query, clientIp, 500, 0, startTime);
    }
  }

private void logRequest(String query, String clientIp, int status, int count, long startTime) {
  long duration = System.currentTimeMillis() - startTime;
  LogEntry entry = new LogEntry(query, clientIp, status, count, duration);
  try {
    MongoDBClient.getInstance().insertLog(entry);
  } catch (Exception e) {
    System.err.println("Failed to write log: " + e.getMessage());
  }
}
}
