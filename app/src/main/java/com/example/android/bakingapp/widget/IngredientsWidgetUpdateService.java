package com.example.android.bakingapp.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.Nullable;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeData;

import fr.arnaudguyon.logfilter.Log;

/**
 * Created by Daniel on 9/12/2017.
 * <p>
 * Facilitates updating the ingredients widget
 */
public class IngredientsWidgetUpdateService extends IntentService {

    // Store the class name for logging
    private static final String TAG = IngredientsWidgetUpdateService.class.getSimpleName();

    public IngredientsWidgetUpdateService() {
        super(IngredientsWidgetUpdateService.class.getSimpleName());
    }

    // Update the ingredients widgets
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v(TAG, "Update of all the ingredients widgets has been triggered");
        
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        // Get the id's of all the ingredient widgets
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidgetProvider.class));

        // Retrieve the data passed in about the selected recipe
        RecipeData recipeData = null;
        String recipeDataKey = getString(R.string.single_recipe_data);
        if (intent.hasExtra(recipeDataKey)) {
            recipeData = intent.getParcelableExtra(recipeDataKey);
        }

        // Now update all widgets
        IngredientsWidgetProvider.updateAllWidgets(this, appWidgetManager, appWidgetIds, recipeData);
    }
}