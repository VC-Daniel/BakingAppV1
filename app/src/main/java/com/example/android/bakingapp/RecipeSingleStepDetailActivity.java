package com.example.android.bakingapp;

import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.widget.Button;

import java.util.ArrayList;

import fr.arnaudguyon.logfilter.Log;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeAllStepsListActivity}.
 */
public class RecipeSingleStepDetailActivity extends AppCompatActivity {

    String SELECTEDSTEPKEY;
    String ALLSTEPSDATAKEY;

    // Stores the current step the user is viewing. This is used to facilitate navigating
    // between steps using the next and previous button
    int SelectedStep = 0;
    String stepDescription;

    // Stores the passed in data about the currently selected step and all the steps
    ArrayList<RecipeStep> allRecipeStepsData = new ArrayList<>();
    RecipeStep selectedRecipeData = new RecipeStep();

    ActionBar actionBar;
    Button previousButton;
    Button nextButton;

    private static final String TAG = RecipeSingleStepDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "Creating the activity that contains the details of a single recipe");

        SELECTEDSTEPKEY = getString(R.string.selected_recipe_step);
        ALLSTEPSDATAKEY = getString(R.string.all_recipe_steps);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_single_step_detail);

        // Get the navigation buttons to allow the user to go between steps in a recipe
        previousButton = (Button) findViewById(R.id.previous_step_button);
        nextButton = (Button) findViewById(R.id.next_step_button);

        // Show the Up button in the action bar.
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // Get the data passed in to the activity
            SelectedStep = getIntent().getIntExtra(SELECTEDSTEPKEY, 0);
            allRecipeStepsData = getIntent().getParcelableArrayListExtra(ALLSTEPSDATAKEY);
            selectedRecipeData = allRecipeStepsData.get(SelectedStep);

            // Create a fragment that displays the details of the selected step
            changeFragment();
        } else {
            // Get the data saved during the onSaveInstanceState
            SelectedStep = savedInstanceState.getInt(SELECTEDSTEPKEY, 0);
            allRecipeStepsData = savedInstanceState.getParcelableArrayList(ALLSTEPSDATAKEY);
            if (allRecipeStepsData != null) {
                selectedRecipeData = allRecipeStepsData.get(SelectedStep);
            }
            else
            {
                Log.e(TAG,"The step data was null for the step " + SelectedStep);
            }
            stepDescription = selectedRecipeData.shortDescription;
        }
        actionBar.setTitle(stepDescription);
        setButtonStatus();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v(TAG, "Storing the selected step");

        // Store the number of the currently selected step and all the recipe steps
        outState.putInt(SELECTEDSTEPKEY, SelectedStep);
        outState.putParcelableArrayList(ALLSTEPSDATAKEY, allRecipeStepsData);
    }

    private void changeFragment() {
        Log.v(TAG, "Changing the currently displayed step details to the step " + selectedRecipeData.shortDescription);

        // Display the short description of the selected step
        stepDescription = selectedRecipeData.shortDescription;
        actionBar.setTitle(stepDescription);

        // Pass the data for the currently selected step to the RecipeSingleStepDetailFragment
        // fragment to be displayed
        Bundle stepDetailArguments = new Bundle();
        final String stepDataKey = getString(R.string.recipe_step_data);
        stepDetailArguments.putParcelable(stepDataKey, selectedRecipeData);
        RecipeSingleStepDetailFragment stepDetailFragment = new RecipeSingleStepDetailFragment();
        stepDetailFragment.setArguments(stepDetailArguments);

        // Create the detail fragment and add it to the activity
        // using a fragment transaction.
        getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, stepDetailFragment)
                .commit();
    }

    /**
     * Navigate to the previous step in the recipe
     *
     * @param view
     */
    public void OnPreviousStep(View view) {
        Log.v(TAG, "Going to the previous step");
        if (SelectedStep - 1 >= 0) {
            SelectedStep = SelectedStep - 1;
        }

        RefreshStepDetails();
    }

    /**
     * Navigate to the next step in the recipe
     *
     * @param view
     */
    public void OnNextStep(View view) {
        Log.v(TAG, "Going to the next step");

        if (SelectedStep + 1 < allRecipeStepsData.size()) {
            SelectedStep = SelectedStep + 1;
        }

        RefreshStepDetails();
    }

    private void RefreshStepDetails() {
        // Get the data for the new step and display it in a fragment
        selectedRecipeData = allRecipeStepsData.get(SelectedStep);
        stepDescription = selectedRecipeData.shortDescription;
        changeFragment();

        // Set the enabled status of the navigation buttons so the user can't
        // go past the last step or before the first step
        setButtonStatus();
    }

    /**
     * Set if the previous and next buttons are enabled. If the first step is being displayed
     * then the previous button shouldn't be enabled and if we are on the last step the
     * next button shouldn't be enabled
     */
    private void setButtonStatus() {

        if (SelectedStep == 0) {
            previousButton.setEnabled(false);
            Log.v(TAG, "Disabling the previous button because the first step is being displayed");
        } else {
            previousButton.setEnabled(true);
        }

        if (SelectedStep + 1 >= allRecipeStepsData.size()) {
            nextButton.setEnabled(false);
            Log.v(TAG, "Disabling the next button because the last step is being displayed");
        } else {
            nextButton.setEnabled(true);
        }
    }

}