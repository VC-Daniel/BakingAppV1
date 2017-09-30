package com.example.android.bakingapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.bakingapp.data.IngredientsContract;
import com.example.android.bakingapp.data.RecipeStepsAdapter;
import com.example.android.bakingapp.utilities.SimpleIdlingResource;
import com.example.android.bakingapp.widget.IngredientsWidgetUpdateService;

import fr.arnaudguyon.logfilter.Log;

/**
 * An activity representing a list of all the steps in a specific Recipe as well as the ingredients. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeSingleStepDetailActivity} representing
 * the individual steps details. On tablets, the activity presents the list of items and
 * step details side-by-side using two vertical panes.
 */
public class RecipeAllStepsListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Integer> {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    // The data about the recipe the user selected
    RecipeData mSingleRecipe;
    int ingredientsUpdateLoader = 100;

    private static final String TAG = RecipeAllStepsListActivity.class.getSimpleName();

    // Indicates if the recipe steps have have been properly loaded into in the UI
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

        // While the recipe steps are being loaded notify any tests that the resource is not idle
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_all_steps_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Get the list view to display the recipe steps in
        ListView recipeStepsListView = (ListView) findViewById(R.id.recipe_list);
        assert recipeStepsListView != null;

        // Get the passed in recipe data
        String recipeDataKey = getString(R.string.all_recipe_data);
        Intent startingIntent = getIntent();
        if (startingIntent != null && startingIntent.hasExtra(recipeDataKey)) {
            mSingleRecipe = startingIntent.getParcelableExtra(recipeDataKey);
            setupAllStepsListView(recipeStepsListView);
        }

        if (findViewById(R.id.recipe_detail_container) != null) {
            Log.v(TAG, "The device is using the two pane layout");

            // The detail container view will be present only in the
            // large-screen layouts (res/values-w700dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // If the device is using the tablet display remove the toolbar and display
        // the recipe name on the actionbar, if it is using the phone layout display the name of the selected recipe
        if (!mTwoPane) {
            toolbar.setTitle(mSingleRecipe.name);
        } else {
            toolbar.setVisibility(View.GONE);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(actionBar.getTitle() + " - " + mSingleRecipe.name);
        }

        Log.v(TAG, "Displaying all the steps in the recipe " + mSingleRecipe.name);

        // Clear the previous data from the widget and insert the ingredients for the selected recipe
        getSupportLoaderManager().restartLoader(ingredientsUpdateLoader, null, RecipeAllStepsListActivity.this);
    }

    private void setupAllStepsListView(@NonNull ListView allStepsListView) {
        // Create an adapter to use to display the recipe steps
        RecipeStepsAdapter adapter = new RecipeStepsAdapter(this, R.layout.recipe_step_content, mSingleRecipe.recipeSteps);
        allStepsListView.setAdapter(adapter);

        // Now that the recipe steps have been loaded set the resource to idle to allow tests to run
        if (idlingResource != null) {
            idlingResource.setIdleState(true);
        }

        // Set up the click listener for the step views
        allStepsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.v(TAG, "Handling on click for the step " + position);

                final String stepDataKey = getString(R.string.recipe_step_data);
                final String selectedStepKey = getString(R.string.selected_recipe_step);
                final String allStepsDataKey = getString(R.string.all_recipe_steps);

                // Depending on the layout either launch a new activity to display the ingredients in,
                // or display them in the detail pane
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    RecipeStep recipeStep = (RecipeStep) adapterView.getItemAtPosition(position);
                    arguments.putParcelable(stepDataKey, recipeStep);
                    RecipeSingleStepDetailFragment fragment = new RecipeSingleStepDetailFragment();
                    fragment.setArguments(arguments);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeSingleStepDetailActivity.class);
                    intent.putExtra(selectedStepKey, position);

                    Log.v(TAG, "Launching an activity to display the step information for the step " + position);

                    // Pass in sll the steps so the user can navigate between them using a previous and next button
                    intent.putParcelableArrayListExtra(allStepsDataKey, mSingleRecipe.recipeSteps);
                    context.startActivity(intent);
                }
            }
        });
    }

    /**
     * Display the ingredients for the recipe when the user click on the recipe button
     *
     * @param v
     */
    public void viewIngredients(View v) {
        Log.v(TAG, "User selected to view the ingredients");

        final String ingredientsDataKey = getString(R.string.recipe_ingredients_data);

        // Either display the ingredients ina  new activity if the user is on
        // a phone, or display them in the detail container if they are on a tablet.
        // Pass in the ingredients information
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelableArrayList(ingredientsDataKey, mSingleRecipe.ingredients);
            IngredientsListActivityFragment fragment = new IngredientsListActivityFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment)
                    .commit();
        } else {
            Context context = v.getContext();
            Intent intent = new Intent(context, IngredientsListActivity.class);
            String ingredientsRecipeKey = getString(R.string.recipe_ingredients_recipe_name);
            intent.putExtra(ingredientsRecipeKey, mSingleRecipe.name);
            intent.putExtra(ingredientsDataKey, mSingleRecipe.ingredients);

            Log.v(TAG, "Launching activity to display the ingredients");
            context.startActivity(intent);
        }
    }

    //
    // Clear out any existing ingredients in the database and insert the ingredients for the
    // selected recipe. Then update all the widgets
    //
    @Override
    public Loader<Integer> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Integer>(this) {
            @Override
            protected void onStartLoading() {
                Log.v(TAG, "Inserting the ingredients data to be displayed by the widget");

                forceLoad();
            }

            @Override
            public Integer loadInBackground() {

                // Delete existing ingredients
                getBaseContext().getContentResolver().delete(IngredientsContract.IngredientsEntry.CONTENT_URI, null, null);

                // Put all the ingredients into an array of content values in order to bulk insert them into the database
                ContentValues[] allIngredients = new ContentValues[mSingleRecipe.ingredients.size()];
                int ingredientIndex = 0;
                for (Ingredient ingredient : mSingleRecipe.ingredients) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_MEASURE, ingredient.measure);
                    contentValues.put(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_NAME, ingredient.name);
                    contentValues.put(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY, ingredient.quantity);
                    allIngredients[ingredientIndex] = contentValues;
                    ingredientIndex++;
                }

                // Insert all the ingredients for the selected recipe into the database
                return getContentResolver().bulkInsert(IngredientsContract.IngredientsEntry.CONTENT_URI, allIngredients);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Integer> loader, Integer data) {
        Log.v(TAG, "Completed inserting the ingredients for the selected recipe, refreshing the widget");

        // When the ingredients information has been inserted into the database reload all the widgets
        Intent intent = new Intent(getBaseContext(), IngredientsWidgetUpdateService.class);
        intent.putExtra(getString(R.string.single_recipe_data), mSingleRecipe);
        getBaseContext().startService(intent);
    }

    @Override
    public void onLoaderReset(Loader<Integer> loader) {
    }
}