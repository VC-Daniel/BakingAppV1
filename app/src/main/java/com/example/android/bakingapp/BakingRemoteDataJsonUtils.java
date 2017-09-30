package com.example.android.bakingapp;

import com.example.android.bakingapp.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.arnaudguyon.logfilter.Log;


/**
 * Created by Daniel on 8/30/2017.
 * <p>
 * This class is a modified version of the OpenWeatherJsonUtils class that was provided in the Sunshine app.
 * Utility functions to handle parsing data from the recipe JSON data.
 */
public final class BakingRemoteDataJsonUtils {

    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * Convert the passed in JSON into a list of RecipeData objects
     *
     * @param allRecipesJsonStr A JSON file as a string that contains recipe data
     * @return a list of recipes based on the information in the passed in JSON
     * @throws JSONException
     */
    public static ArrayList<RecipeData> getRecipesDataFromJson(String allRecipesJsonStr) throws JSONException {

        // Get all the recipes from the JSON
        JSONArray allRecipesArray = new JSONArray(allRecipesJsonStr);
        ArrayList<RecipeData> parsedRecipeData = new ArrayList<RecipeData>();

        Log.v(TAG, "Creating RecipeData objects for the " + allRecipesArray.length() + " recipes that were passed in by the api");

        if (allRecipesArray.length() <= 0) {
            Log.e(TAG, "No recipes where returned from the api");
        }

        // Loop through all the recipes and get information about each one
        for (int i = 0; i < allRecipesArray.length(); i++) {
            JSONObject singleRecipeJSON = allRecipesArray.getJSONObject(i);
            RecipeData singleRecipe = new RecipeData();

            singleRecipe.servings = singleRecipeJSON.getInt("servings");
            singleRecipe.name = singleRecipeJSON.getString("name");

            Log.v(TAG, "Created the RecipeData objects for the recipe " + singleRecipe.name);

            // Loop through all the steps for this recipe and store the information about each step
            JSONArray stepsArray = singleRecipeJSON.getJSONArray("steps");

            if (stepsArray.length() <= 0) {
                Log.e(TAG, "There are no steps in the recipe " + singleRecipe.name);
            }

            for (int j = 0; j < stepsArray.length(); j++) {
                JSONObject singleStepJSON = stepsArray.getJSONObject(j);
                RecipeStep singleRecipeStep = new RecipeStep();
                if (singleStepJSON.has("thumbnailURL")) {
                    singleRecipeStep.thumbnailURL = singleStepJSON.getString("thumbnailURL");
                }

                if (singleStepJSON.has("videoURL")) {
                    singleRecipeStep.videoURL = singleStepJSON.getString("videoURL");
                }

                singleRecipeStep.shortDescription = singleStepJSON.getString("shortDescription");
                singleRecipeStep.description = singleStepJSON.getString("description");
                singleRecipe.recipeSteps.add(singleRecipeStep);
            }

            // Loop through all the ingredients and store the information about each
            JSONArray ingredientsArray = singleRecipeJSON.getJSONArray("ingredients");

            if (ingredientsArray.length() <= 0) {
                Log.e(TAG, "There are no ingredients for the recipe " + singleRecipe.name);
            }

            for (int k = 0; k < ingredientsArray.length(); k++) {
                JSONObject singleIngredientJSON = ingredientsArray.getJSONObject(k);
                Ingredient singleIngredient = new Ingredient();
                singleIngredient.measure = singleIngredientJSON.getString("measure");
                singleIngredient.name = singleIngredientJSON.getString("ingredient");
                singleIngredient.quantity = singleIngredientJSON.getDouble("quantity");

                singleRecipe.ingredients.add(singleIngredient);
            }

            // Add the recipe to the list of all the recipes provided in the JSON
            parsedRecipeData.add(singleRecipe);
        }

        // Return all the retrieved recipes from the RecipeData
        return parsedRecipeData;
    }
}