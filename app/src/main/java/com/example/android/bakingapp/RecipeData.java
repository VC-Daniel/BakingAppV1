package com.example.android.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


/**
 * Created by Daniel on 8/30/2017.
 * <p>
 * Information about a single recipe
 */
public class RecipeData implements Parcelable {
    public String name;
    int servings;
    public ArrayList<Ingredient> ingredients = new ArrayList<>();
    ArrayList<RecipeStep> recipeSteps = new ArrayList<>();

    RecipeData() {
    }

    /**
     * Create a RecipeData from a Parcel representation
     *
     * @param in
     */
    RecipeData(Parcel in) {
        name = in.readString();
        servings = in.readInt();
        ingredients = in.createTypedArrayList(Ingredient.CREATOR);
        recipeSteps = in.createTypedArrayList(RecipeStep.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(servings);
        dest.writeTypedList(ingredients);
        dest.writeTypedList(recipeSteps);
    }

    public static final Creator<RecipeData> CREATOR = new Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel in) {
            return new RecipeData(in);
        }

        @Override
        public RecipeData[] newArray(int size) {
            return new RecipeData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
