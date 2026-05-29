import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenFoodFactsClient {

  private static final String API_URL = "https://world.openfoodfacts.org/cgi/search.pl";
  private static final String FIELDS = "product_name,brands,nutriscore_grade,nutriments,image_url";

  private static final int MAX_RETRIES = 5;
  private static final int CONNECT_TIMEOUT = 12000;
  private static final int READ_TIMEOUT = 15000;
  private static final long INITIAL_BACKOFF_MS = 1000;

  public JSONArray search(String query) throws Exception {

    int attempt = 0;
    while (true) {
      attempt++;

      String encoded = URLEncoder.encode(query, StandardCharsets.UTF_8);
      String urlString = API_URL
          + "?search_terms=" + encoded
          + "&json=true"
          + "&page_size=10"
          + "&fields=" + FIELDS;

      URL url = new URL(urlString);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("GET");
      conn.setRequestProperty("User-Agent", "FoodServiceApp/1.0");
      conn.setConnectTimeout(CONNECT_TIMEOUT);
      conn.setReadTimeout(READ_TIMEOUT);

      int status = conn.getResponseCode();

      if (status == 200) {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) sb.append(line);
        reader.close();

        JSONObject json = new JSONObject(sb.toString());
        JSONArray products = json.getJSONArray("products");

        JSONArray cleaned = new JSONArray();
        for (int i = 0; i < products.length(); i++) {
          JSONObject p = products.getJSONObject(i);
          String name = p.optString("product_name", "").trim();
          if (name.isEmpty()) continue;

          JSONObject item = new JSONObject();
          item.put("name",       name);
          item.put("brand",      p.optString("brands", "N/A"));
          item.put("nutriscore", p.optString("nutriscore_grade", "N/A").toUpperCase());
          item.put("image_url",  p.optString("image_url", ""));

          JSONObject nutriments = p.optJSONObject("nutriments");
          if (nutriments != null) {
            item.put("calories_100g", nutriments.optDouble("energy-kcal_100g", -1));
            item.put("protein_100g",  nutriments.optDouble("proteins_100g", -1));
            item.put("fat_100g",      nutriments.optDouble("fat_100g", -1));
            item.put("carbs_100g",    nutriments.optDouble("carbohydrates_100g", -1));
            item.put("sugar_100g",    nutriments.optDouble("sugars_100g", -1));
            item.put("sodium_100g",   nutriments.optDouble("sodium_100g", -1));
          }

          cleaned.put(item);
        }
        return cleaned;
      }

      if (status == 503 && attempt < MAX_RETRIES) {
        try {
          Thread.sleep(INITIAL_BACKOFF_MS + attempt * 500L);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
        }
        continue;
      }

      throw new Exception("Open Food Facts returned HTTP " + status);
    }
  }
}
