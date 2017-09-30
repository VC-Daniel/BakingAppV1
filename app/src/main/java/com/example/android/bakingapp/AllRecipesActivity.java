package com.example.android.bakingapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.bakingapp.utilities.NetworkUtils;
import com.example.android.bakingapp.utilities.SimpleIdlingResource;

import java.util.ArrayList;

import fr.arnaudguyon.logfilter.Log;

/**
 * Display all recipes retrieved from the recipe data api.
 */
public class AllRecipesActivity extends AppCompatActivity implements RecipeDataAdapter.RecipeDataAdapterOnClickHandler {

    RecipeDataAdapter mRecipeDataAdapter;
    RecyclerView mRecyclerView;
    ProgressBar mDataLoadingProgressBar;
    TextView mErrorTextView;

    // Store the class name for logging
    private static final String TAG = NetworkUtils.class.getSimpleName();

    int recipeLoaderID = 100;

    // store the recipe the user has selected by clicking on a widget if one was selected
    String widgetChosenRecipe = "";

    // Indicates if the recipes have have been downloaded from the api and are properly
    // loaded into in the UI
    SimpleIdlingResource idlingResource;

    @VisibleForTesting
    @NonNull
    public SimpleIdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getIdlingResource();

        // While the recipes are being loaded notify any tests that the resource is not idle
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_recipes);

        // This TextView is used to display errors and will be hidden if there are no errors
        mErrorTextView = (TextView) findViewById(R.id.tv_error_message);
        mDataLoadingProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mRecyclerView = (RecyclerView) findViewById(R.id.recipe_list);

        // Display a grid of recipes, if the user is on a phone this will have a single column,
        // or if the app is being used on a tablet then it will display multiple columns of recipes
        int gridWidth = getResources().getInteger(R.integer.grid_width);
        GridLayoutManager layoutManager = new GridLayoutManager(this, gridWidth);
        mRecyclerView.setLayoutManager(layoutManager);

        // the child layouts size will not change in the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mRecipeDataAdapter = new RecipeDataAdapter(this);
        mRecyclerView.setAdapter(mRecipeDataAdapter);

        // If the widget has been clicked on then open the app get the name of the recipe the widget was displaying
        String widgetSelectedRecipeKey = getString(R.string.wiget_selected_recipe_name);
        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey(widgetSelectedRecipeKey)) {
            widgetChosenRecipe = getIntent().getExtras().getString(widgetSelectedRecipeKey);
        }

        // If the recipes have already been loaded then retrieve the data otherwise
        // call the api to get new data
        String allRecipesDataKey = getString(R.string.all_recipes_data);
        if (savedInstanceState != null && savedInstanceState.containsKey(allRecipesDataKey)) {
            Log.i(TAG, "Using previously loaded recipe data");
            ArrayList<RecipeData> savedData = savedInstanceState.getParcelableArrayList(allRecipesDataKey);
            mRecipeDataAdapter.setRecipeData(savedData);

            // Now that the recipes are being displayed set the resource to idle so the tests
            // can run
            if (idlingResource != null) {
                idlingResource.setIdleState(true);
            }

            if (!widgetChosenRecipe.equals("")) {
                // If a recipe was selected by clicking on the widget then continue to the recipe's details
                chooseRecipe(widgetChosenRecipe);
            }
        } else {
            Log.i(TAG, "No previously saved recipe data, loading data from the api");
            // if no previous data exists then retrieve recipe data to populate the grid
            loadRecipeData();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save the recipe data to be loaded when the activity is recreated
        String allRecipesDataKey = getString(R.string.all_recipes_data);
        outState.putParcelableArrayList(allRecipesDataKey, mRecipeDataAdapter.mRecipeData);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Click the recipe that matches the name passed in. This is used to select the recipe that the
     * user selected by clicking on a widget that was displaying a recipe's ingredients
     *
     * @param widgetSelectedRecipeName The name of the recipe to select
     */
    private void chooseRecipe(String widgetSelectedRecipeName) {
        // If the recipe name is invalid log it and cancel the attempt to load the recipe data
        if (widgetSelectedRecipeName == null || widgetSelectedRecipeName.equals("")) {
            Log.e(TAG, "The user selected a recipe on the widget with no name");
            return;
        }

        // Loop through all the recipes and check if any of their names match
        // the ones passed to the method
        ArrayList<RecipeData> recipes = mRecipeDataAdapter.mRecipeData;
        for (int recipeIndex = 0; recipeIndex < recipes.size(); recipeIndex++) {
            if (recipes.get(recipeIndex).name.equals(widgetSelectedRecipeName)) {
                // Pass the data about the selected recipe to the RecipeAllStepsListActivity
                // to display all the information about the recipe
                String recipeDataKey = getString(R.string.all_recipe_data);
                Intent intent = new Intent(this, RecipeAllStepsListActivity.class);
                intent.putExtra(recipeDataKey, recipes.get(recipeIndex));
                Log.v(TAG, "Handling widget click on the recipe " + recipes.get(recipeIndex).name);
                // launch the recipe steps activity
                startActivity(intent);
            }
        }
    }

    /**
     * Retrieve recipe data from the api and display it in the
     * recyclerView as a grid. While the data is loading display a loading indicator
     * and if there is an error it will be displayed
     */
    private void loadRecipeData() {
        // hide the error message text view while attempting to get new data
        mErrorTextView.setVisibility(View.INVISIBLE);

        // display the loading indicator while attempting to get recipe data
        displayLoadingIndicator(true);

        // clear out the existing recipe data
        mRecipeDataAdapter.setRecipeData(null);

        Log.v(TAG, "No previously saved recipe data, loading data from the api");
        // if no previous data exists then retrieve new data

        Bundle recipeDataBundle = new Bundle();
        getLoaderManager().initLoader(recipeLoaderID, recipeDataBundle, recipeDataLoaderCallbacks);
    }

    /**
     * Show a loading indicator or display the grid of recipe data
     *
     * @param isLoading if the loading indicator should be displayed and if the recyclerView should be hidden
     */
    private void displayLoadingIndicator(boolean isLoading) {
        if (isLoading) {
            mDataLoadingProgressBar.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            mDataLoadingProgressBar.setVisibility(View.INVISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private LoaderManager.LoaderCallbacks<ArrayList<RecipeData>> recipeDataLoaderCallbacks = new LoaderManager.LoaderCallbacks<ArrayList<RecipeData>>() {

        @Override
        public Loader<ArrayList<RecipeData>> onCreateLoader(int id, final Bundle args) {
            return new AsyncTaskLoader<ArrayList<RecipeData>>(getBaseContext()) {
                @Override
                public ArrayList<RecipeData> loadInBackground() {
                    try {

                        Log.v(TAG, "Retrieving recipe data from the api");
                        // get a response from recipe api
                        String recipeDataResponse = NetworkUtils.getResponseFromHttpUrl();

                        // parse the response into recipeData objects that contain information about each recipe
                        ArrayList<RecipeData> allRecipesData = BakingRemoteDataJsonUtils
                                .getRecipesDataFromJson(recipeDataResponse);
                        return allRecipesData;
                    } catch (Exception e) {
                        // if there was an issue log it and print the stack trace to help determine what the issue is
                        Log.e(TAG, "Error occurred while loading all the recipes from the api: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onStartLoading() {
                    // if we don't already have recipe data get the data from te api
                    if (mRecipeDataAdapter.getItemCount() == 0) {
                        forceLoad();
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ArrayList<RecipeData>> loader, ArrayList<RecipeData> data) {
            Log.v(TAG, "Displaying the recipes retrieved from the api");

            // If data was successfully retrieved hide the loading indicator and the error text view
            if (data != null) {
                mRecipeDataAdapter.setRecipeData(data);
                displayLoadingIndicator(false);
            } else {
                // if there was an issue retrieving data notify the user
                mErrorTextView.setVisibility(View.VISIBLE);
            }

            // The recipes have been loaded from the api and tests should now run
            if (idlingResource != null) {
                idlingResource.setIdleState(true);
            }

            // Check if the user has selected a recipe by clicking on an ingredient in the widget
            // and if so go to the recipe's details
            if (!widgetChosenRecipe.equals("")) {
                chooseRecipe(widgetChosenRecipe);
            }
        }

        @Override
        public void onLoaderReset(Loader<ArrayList<RecipeData>> loader) {

        }
    };

    @Override
    public void onClick(RecipeData recipeData) {
        // get the keys to store the recipe data in the intent
        String recipeDataKey = getString(R.string.all_recipe_data);

        // create an intent to go to the recipe detail activity
        Intent intent = new Intent(this, RecipeAllStepsListActivity.class);

        intent.putExtra(recipeDataKey, recipeData);

        Log.v(TAG, "Handling user click on the recipe " + recipeData.name);
        // launch the recipe detail activity
        startActivity(intent);
    }
}