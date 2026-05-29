package ds.edu.cmu.nutrisearch;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import ds.edu.cmu.nutrisearch.R;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ImageView imageViewDetail    = findViewById(R.id.imageViewDetail);
        TextView textViewDetailName  = findViewById(R.id.textViewDetailName);
        TextView textViewDetailBrand = findViewById(R.id.textViewDetailBrand);
        TextView textViewNutriscore  = findViewById(R.id.textViewNutriscore);

        String name       = getIntent().getStringExtra("name");
        String brand      = getIntent().getStringExtra("brand");
        String nutriscore = getIntent().getStringExtra("nutriscore");
        String imageUrl   = getIntent().getStringExtra("imageUrl");
        double calories   = getIntent().getDoubleExtra("calories", -1);
        double protein    = getIntent().getDoubleExtra("protein",  -1);
        double fat        = getIntent().getDoubleExtra("fat",      -1);
        double carbs      = getIntent().getDoubleExtra("carbs",    -1);
        double sugar      = getIntent().getDoubleExtra("sugar",    -1);
        double sodium     = getIntent().getDoubleExtra("sodium",   -1);

        // Fill in basic info
        textViewDetailName.setText(name);
        textViewDetailBrand.setText(brand);
        textViewNutriscore.setText("Nutriscore: " + nutriscore.toUpperCase());

        // Load image with Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(imageViewDetail);


        setNutritionRow(R.id.rowCalories, "Calories",    calories, "kcal");
        setNutritionRow(R.id.rowProtein,  "Protein",     protein,  "g");
        setNutritionRow(R.id.rowFat,      "Fat",         fat,      "g");
        setNutritionRow(R.id.rowCarbs,    "Carbs",       carbs,    "g");
        setNutritionRow(R.id.rowSugar,    "Sugar",       sugar,    "g");
        setNutritionRow(R.id.rowSodium,   "Sodium",      sodium,   "g");
    }

    private void setNutritionRow(int rowId, String label, double value, String unit) {
        android.view.View row     = findViewById(rowId);
        TextView textLabel        = row.findViewById(R.id.textLabel);
        TextView textValue        = row.findViewById(R.id.textValue);

        textLabel.setText(label);
        textValue.setText(value >= 0 ? String.format("%.1f %s", value, unit) : "N/A");
    }
}