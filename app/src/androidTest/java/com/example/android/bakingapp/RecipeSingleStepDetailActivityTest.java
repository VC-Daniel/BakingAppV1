package com.example.android.bakingapp;

import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isEnabled;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Daniel on 9/27/2017.
 *
 * Test the RecipeSingleStepDetailActivity to ensure the functionality such as the previous and
 * next step button and the description are all working as expected.
 */

@RunWith(AndroidJUnit4.class)
public class RecipeSingleStepDetailActivityTest {

    // Create hardcoded step descriptions to compare to the value being displayed to ensure the
    // data is correctly getting received and displayed in the activity
    private final String stepOneDescription = "long description for step one.";
    private final String stepTwoDescription = "long description for step two.";

    @Rule
    // Create a rule for the class we want to test but don't start the activity automatically so
    // we can pass data into it. This was inspired by the tutorial at:
    // http://www.vogella.com/tutorials/AndroidTestingEspresso/article.html#espresso_mockingintents
    public ActivityTestRule<RecipeSingleStepDetailActivity> rule = new ActivityTestRule<>(RecipeSingleStepDetailActivity.class, true, false);

    /**
     * Before each step pass in the recipe step data to the activity
     */
    @Before
    public void intentPrep() {
        final String selectedStepKey = "SelectedStep";
        final String allStepsDataKey = "AllRecipeSteps";

        // Create two steps with basic information
        ArrayList<RecipeStep> recipeSteps = new ArrayList<>();
        RecipeStep stepOne = new RecipeStep();
        stepOne.shortDescription = "Step one";
        stepOne.description = stepOneDescription;
        recipeSteps.add(stepOne);

        RecipeStep stepTwo = new RecipeStep();
        stepTwo.shortDescription = "Step two";
        stepTwo.videoURL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4";
        stepTwo.description = stepTwoDescription;
        recipeSteps.add(stepTwo);

        Intent intent = new Intent();
        intent.putExtra(selectedStepKey, 0);
        // Pass in sll the steps so navigation between steps can be tested
        intent.putParcelableArrayListExtra(allStepsDataKey, recipeSteps);
        rule.launchActivity(intent);
    }

    /**
     * Test if the step description is being properly set
     */
    @Test
    public void testDescription() {
        onView(withId(R.id.recipe_detail)).check(matches(withText(stepOneDescription)));
    }

    /**
     * Test if the next button properly goes to the next recipe step
     */
    @Test
    public void testNextButton() {
        onView((withId(R.id.next_step_button))).perform(click());
        onView(withId(R.id.recipe_detail)).check(matches(withText(stepTwoDescription)));
    }

    /**
     * Test if the previous button properly goes to the previous recipe step
     */
    @Test
    public void testPreviousButton() {
        onView((withId(R.id.next_step_button))).perform(click());
        onView(withId(R.id.recipe_detail)).check(matches(withText(stepTwoDescription)));
        onView((withId(R.id.previous_step_button))).perform(click());
        onView(withId(R.id.recipe_detail)).check(matches(withText(stepOneDescription)));
    }

    /**
     * Test if the previous button is properly disabled when on the first step and enabled
     * when on another step.
     */
    @Test
    public void testPreviousButtonDisabled() {
        onView((withId(R.id.previous_step_button))).check(matches(not(isEnabled())));
        onView((withId(R.id.next_step_button))).perform(click());
        onView((withId(R.id.previous_step_button))).check(matches((isEnabled())));
    }

    /**
     * Test if the next button is properly disabled when on the last step and enabled
     * when on another step.
     */
    @Test
    public void testNextButtonDisabled() {
        onView((withId(R.id.next_step_button))).check(matches(isEnabled()));
        onView((withId(R.id.next_step_button))).perform(click());
        onView((withId(R.id.next_step_button))).check(matches(not(isEnabled())));
    }
}
