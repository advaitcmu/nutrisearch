package ds.edu.cmu.nutrisearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private EditText editTextSearch;
    private Button buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        editTextSearch = findViewById(R.id.editTextSearch);
        buttonSearch = findViewById(R.id.buttonSearch);

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = editTextSearch.getText().toString().trim();

                if (query.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter a food to search",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(MainActivity.this, ResultsActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
            }
        });
    }
}