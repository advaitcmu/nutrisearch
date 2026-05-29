package ds.edu.cmu.nutrisearch;

public class FoodItem {

    private String name;
    private String brand;
    private String nutriscore;
    private String imageUrl;
    private double calories;
    private double protein;
    private double carbs;
    private double sugar;
    private double sodium;
    private double fat;

    public FoodItem(String name, String brand, String nutriscore, String imageUrl, double calories,
                    double protein, double carbs, double sugar, double sodium, double fat) {
        this.name = name;
        this.brand = brand;
        this.nutriscore = nutriscore;
        this.imageUrl = imageUrl;
        this.calories = calories;
        this.protein = protein;
        this.carbs = carbs;
        this.sugar = sugar;
        this.sodium = sodium;
        this.fat = fat;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getNutriscore() {
        return nutriscore;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getSugar() {
        return sugar;
    }

    public double getSodium() {
        return sodium;
    }

    public double getFat() {
        return fat;
    }
}
