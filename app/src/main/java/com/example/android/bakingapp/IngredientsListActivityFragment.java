package com.example.android.bakingapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fr.arnaudguyon.logfilter.Log;

/**
 * Displays all the ingredients for a single recipe
 */
public class IngredientsListActivityFragment extends Fragment {

    // Store the class name for logging
    private static final String TAG = IngredientsListActivityFragment.class.getSimpleName();

    TextView ingredientsTV;
    ArrayList<Ingredient> mIngredients = new ArrayList<>();

    public IngredientsListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // assign the ingredients list layout
        return inflater.inflate(R.layout.fragment_ingredients_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the textView to display the recipe's ingredients in
        ingredientsTV = (TextView) getView().findViewById(R.id.ingredients_tv);
        setIngredients(mIngredients);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get all the ingredients passed in for the selected recipe
        String ingredientsDataKey = getString(R.string.recipe_ingredients_data);
        if (getArguments() != null && getArguments().containsKey(ingredientsDataKey)) {
            mIngredients = getArguments().getParcelableArrayList(ingredientsDataKey);
            Log.v(TAG, "Successfully retrieved the ingredients data for the selected recipe");
        }
    }

    /**
     * Display all the recipe's ingredients
     *
     * @param mIngredients the list of ingredients to display
     */
    public void setIngredients(ArrayList<Ingredient> mIngredients) {
        Log.v(TAG, "Displaying all " + mIngredients.size() + " ingredients for the selected recipe");
        // Get the formatted version of each ingredient and add display it
        for (Ingredient ingredient : mIngredients) {
            String ingredientsText = ingredientsTV.getText() + ingredient.toString() + "\n";
            ingredientsTV.setText(ingredientsText);
        }
    }
}