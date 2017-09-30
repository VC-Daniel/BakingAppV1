package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;

import fr.arnaudguyon.logfilter.Log;

/**
 * Display all the ingredients in a recipe in an encapsulated IngredientsListActivityFragment
 */
public class IngredientsListActivity extends AppCompatActivity {

    // Store the class name for logging
    private static final String TAG = IngredientsListActivity.class.getSimpleName();

    ArrayList<Ingredient> mIngredients;
    String mRecipeName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ingredients_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get the passed in ingredient information
        String ingredientsDataKey = getString(R.string.recipe_ingredients_data);
        String ingredientsRecipeKey = getString(R.string.recipe_ingredients_recipe_name);
        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(ingredientsDataKey)) {
            Log.v(TAG,"Retrieving ingredients information");
            mIngredients = startingIntent.getParcelableArrayListExtra(ingredientsDataKey);
            mRecipeName = startingIntent.getStringExtra(ingredientsRecipeKey);
        }
        else
        {
            Log.e(TAG,"No ingredients where supplied for this recipe");
        }

        Log.v(TAG,"Displaying ingredients information for the recipe: " + mRecipeName);
        // Display the recipe name
        toolbar.setTitle(mRecipeName);

        // Pass the ingredients information to the IngredientsListActivityFragment to be displayed
        IngredientsListActivityFragment fragment = (IngredientsListActivityFragment) getSupportFragmentManager().findFragmentById(R.id.ingredientsListFragment);
        fragment.setIngredients(mIngredients);
    }
}