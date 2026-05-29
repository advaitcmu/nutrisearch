package ds.edu.cmu.nutrisearch;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final Context context;
    private final List<FoodItem> foodList;

    public FoodAdapter(Context context, List<FoodItem> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    // Called by RecyclerView when it needs a new row view
    @NonNull
    @Override
    public FoodAdapter.FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    // Called by the RecyclerView for each visible row - fills in name, brand, image
    @Override
    public void onBindViewHolder(@NonNull FoodAdapter.FoodViewHolder holder, int position) {
        FoodItem item = foodList.get(position);

        holder.textViewName.setText(item.getName());
        holder.textViewBrand.setText(item.getBrand());

        // Glide loads the image from URL into the ImageView
        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .into(holder.imageViewFood);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);
            intent.putExtra("name",       item.getName());
            intent.putExtra("brand",      item.getBrand());
            intent.putExtra("nutriscore", item.getNutriscore());
            intent.putExtra("imageUrl",   item.getImageUrl());
            intent.putExtra("calories",   item.getCalories());
            intent.putExtra("protein",    item.getProtein());
            intent.putExtra("fat",        item.getFat());
            intent.putExtra("carbs",      item.getCarbs());
            intent.putExtra("sugar",      item.getSugar());
            intent.putExtra("sodium",     item.getSodium());
            context.startActivity(intent);
        });


    }


    // Tell RecyclerView how many rows there are
    @Override
    public int getItemCount() {
        return foodList.size();
    }

    // Holds references to the views inside a single row
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewFood;
        TextView textViewName;
        TextView textViewBrand;

        FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewFood = itemView.findViewById(R.id.imageViewFood);
            textViewName  = itemView.findViewById(R.id.textViewName);
            textViewBrand = itemView.findViewById(R.id.textViewBrand);
        }
    }
}
