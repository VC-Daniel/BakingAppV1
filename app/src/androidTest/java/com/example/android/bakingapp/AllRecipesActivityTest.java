package com.example.android.bakingapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

/**
 * Created by Daniel on 9/25/2017.
 * <p>
 * Tests the AllRecipesActivity to make sure the recipes are being correctly loaded and displayed.
 * This class is partially based off the MenuActivityScreenTest and IdlingResourceMenuActivityTest
 * in the TeaTime app.
 */

@RunWith(AndroidJUnit4.class)
public class AllRecipesActivityTest {

    // The expected recipe name and steps to test against
    private static final String RECIPE_NAME = "Nutella Pie";
    private static final String FIRST_STEP = "Recipe Introduction";
    private static final String SECOND_STEP = "Starting prep";

    // Stores if the recipes have been loaded from the api and are being displayed in the UI
    private IdlingResource mIdlingResource;

    @Rule
    public ActivityTestRule<AllRecipesActivity> mActivityTestRule = new ActivityTestRule<>(AllRecipesActivity.class);

    // Registers the resource so the tests won't be run until the recipes have been loaded
    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);

    }

    // Unregister resources when not needed
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    /**
     * Clicks on a recipe list item and checks if it opens up the RecipeAllStepsListActivity
     * and displayed the correct data.
     */
    @Test
    public void clickRecipe_List_OpensRecipeAllStepsListActivity() {

        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recipe list item and clicks it.
        onView(withId(R.id.recipe_list)).perform(RecyclerViewActions.actionOnItem(hasDescendant(withText(RECIPE_NAME)), click()));

        // Check to make sure the recipe steps have been correctly passed to and are being displayed
        // in the RecipeAllStepsListActivity
        onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(0).check(matches(hasDescendant(withText(FIRST_STEP))));
        onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(1).check(matches(hasDescendant(withText(SECOND_STEP))));
    }
}