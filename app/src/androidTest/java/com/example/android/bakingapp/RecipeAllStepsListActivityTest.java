package com.example.android.bakingapp;

import android.content.Intent;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Daniel on 9/27/2017.
 * <p>
 * Tests the RecipeAllStepsListActivity UI to make sure the functionality such as the viewing the
 * ingredients and individual recipe steps.
 */

public class RecipeAllStepsListActivityTest {

    // Create descriptions for the steps to be used to ensure the current data is displayed
    private final String stepOneDescription = "long description for step one.";
    private final String stepTwoDescription = "long description for step two.";
    final String firstStep = "Step one";
    String ingredientsText;

    private IdlingResource mIdlingResource;

    @Rule
    // Create a rule for the class we want to test but don't start the activity automatically so
    // we can pass data into it. This was inspired by the tutorial at:
    // http://www.vogella.com/tutorials/AndroidTestingEspresso/article.html#espresso_mockingintents
    public ActivityTestRule<RecipeAllStepsListActivity> mRecipeAllStepsListActivityTestRule = new ActivityTestRule<>(RecipeAllStepsListActivity.class, true, false);

    /**
     * Before each test is started generate and pass in data for a single recipe
     */
    @Before
    public void intentPrep() {
        final String allRecipeDataKey = "allRecipeData";

        // Create two recipe steps
        ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
        RecipeStep stepOne = new RecipeStep();
        stepOne.shortDescription = firstStep;
        stepOne.description = stepOneDescription;
        recipeSteps.add(stepOne);

        RecipeStep stepTwo = new RecipeStep();
        stepTwo.shortDescription = "Step two";
        stepTwo.description = stepTwoDescription;
        recipeSteps.add(stepTwo);

        RecipeData recipeData = new RecipeData();
        recipeData.recipeSteps = recipeSteps;
        recipeData.name = "Recipe One";

        // Create two recipes to test if the correct recipe ingredients are being displayed.
        // Create one recipe that only has a name, and another that supplies a quantity and measure
        Ingredient ingredientOne = new Ingredient();
        ingredientOne.name = "Ingredient one";

        Ingredient ingredientTwo = new Ingredient();
        ingredientTwo.name = "Ingredient Two";
        ingredientTwo.measure = "Bits";
        ingredientTwo.quantity = 1;

        // Build out the ingredients string that should be displayed to be used during testing
        String ingredientOneText = Ingredient.formatIngredientText(ingredientOne.quantity, ingredientOne.measure, ingredientOne.name);
        String ingredientTwoText = Ingredient.formatIngredientText(ingredientTwo.quantity, ingredientTwo.measure, ingredientTwo.name);
        ingredientsText = ingredientOneText + "\n" + ingredientTwoText + "\n";

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredientOne);
        ingredients.add(ingredientTwo);
        recipeData.ingredients = ingredients;

        // Pass in all the recipe data
        Intent intent = new Intent();
        intent.putExtra(allRecipeDataKey, recipeData);
        mRecipeAllStepsListActivityTestRule.launchActivity(intent);

        // Wait until the recipe steps are being displayed before running the test
        mIdlingResource = mRecipeAllStepsListActivityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    // Unregister resources now that they are not needed
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    /**
     * Test if the ingredients are properly displayed when the user clicks ingredients
     */
    @Test
    public void testIngredients() {
        onView(withId(R.id.ingredientsLinearLayout)).perform(click());
        onView(withId(R.id.ingredients_tv)).check(matches(withText(ingredientsText)));
    }

    /**
     * Verify that the selected step information is currently displayed when clicked on
     */
    @Test
    public void testClickStep() {
        onData(anything()).inAdapterView(withId(R.id.recipe_list)).atPosition(0).perform(click());
        onView(withId(R.id.recipe_detail)).check(matches(withText(stepOneDescription)));
    }
}