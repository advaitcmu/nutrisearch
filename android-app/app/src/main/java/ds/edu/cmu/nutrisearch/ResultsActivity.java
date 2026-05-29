package ds.edu.cmu.nutrisearch;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textViewError;
    private TextView textViewQuery;
    private TextView textViewCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        textViewError = findViewById(R.id.textViewError);
        textViewQuery = findViewById(R.id.textViewQuery);
        textViewCount = findViewById(R.id.textViewCount);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String query = getIntent().getStringExtra("query");
        textViewQuery.setText("Results for \"" + query + "\"");

        fetchResults(query);
    }

    private void fetchResults(String query) {
        progressBar.setVisibility(View.VISIBLE);
        textViewError.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            List<FoodItem> results = new ArrayList<>();
            String errorMessage = null;

            try {
               URI uri = new URI(
                       "https",
                       "scaling-waddle-q75vg74q9wwr3x4ww-8080.app.github.dev",
                       "/api/search",
                       "q=" + query.trim(),
                       null);

               URL url = uri.toURL();

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setInstanceFollowRedirects(true);
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                conn.setRequestProperty("Accept", "application/json");
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                int status = conn.getResponseCode();
//                errorMessage = "Status: " +  status  + "url: " + conn.getURL().toString();

                if (status == 200) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    JSONArray products = json.getJSONArray("results");

                    for (int i = 0; i < products.length(); i++) {
                        JSONObject p = products.getJSONObject(i);
                        results.add(new FoodItem(
                                p.optString("name", "N/A"),
                                p.optString("brand", "N/A"),
                                p.optString("nutriscore", "N/A"),
                                p.optString("image_url", ""),
                                p.optDouble("calories_100g", -1),
                                p.optDouble("protein_100g", -1),
                                p.optDouble("fat_100g", -1),
                                p.optDouble("carbs_100g", -1),
                                p.optDouble("sugar_100g", -1),
                                p.optDouble("sodium_100g", -1)
                        ));
                    }
                } else if (status == 404) {
                    errorMessage = "No products found for \"" + query + "\"";
                } else {
                    errorMessage = "Server error. Please try again.";
                }

            } catch (Exception e) {
//                errorMessage = "Could not connect to server. Check your connection.";
                errorMessage = e.getClass().getName() + ": " + e.getMessage();
            }

            final List<FoodItem> finalResults = results;
            final String finalError = errorMessage;

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);

                if (finalError != null) {
                    textViewError.setText(finalError);
                    textViewError.setVisibility(View.VISIBLE);
                    return;
                }

                textViewCount.setText(finalResults.size() + " items");
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(new FoodAdapter(this, finalResults));

                android.util.Log.d("NutriSearch", "Result count: " + finalResults.size());
            });
        });
    }
}
