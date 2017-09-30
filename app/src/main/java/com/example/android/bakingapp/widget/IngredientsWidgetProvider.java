package com.example.android.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.android.bakingapp.AllRecipesActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeData;

import fr.arnaudguyon.logfilter.Log;

/**
 * Created by Daniel on 9/12/2017.
 * <p>
 * <p>
 * Updates all ingredients widget
 * This class is based off of the PlantWidgetProvider class in the MyGarden app
 */
public class IngredientsWidgetProvider extends AppWidgetProvider {

    // Store the class name for logging
    private static final String TAG = IngredientsWidgetProvider.class.getSimpleName();

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, RecipeData recipeData) {
        Log.v(TAG, "Updating the widget: " + appWidgetId);

        // set the layout to show on the widget
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);

        // pass in the name of the selected recipe. This is used to select the correct recipe
        // if the uer clicks on the widget which reopens the app
        Intent ingredientsListIntent = new Intent(context, IngredientsWidgetService.class);
        if (recipeData != null) {
            Log.v(TAG, "updating the widget for the recipe: " + recipeData.name);

            // set the recipe name textView to the selected recipe
            if (!recipeData.name.equals("")) {
                String ingredientsLabel = context.getString(R.string.ingredients_header_label, recipeData.name);
                remoteViews.setTextViewText(R.id.recipeName, ingredientsLabel);
            } else {
                Log.e(TAG, "A recipe with an empty name was supplied, this may cause unexpected behavior");
                String fallbackRecipeName = context.getString(R.string.fall_back_recipe_name);
                String ingredientsLabel = context.getString(R.string.ingredients_header_label, fallbackRecipeName);
                remoteViews.setTextViewText(R.id.recipeName, ingredientsLabel);
            }

            String widgetSelectedRecipeKey = context.getString(R.string.wiget_selected_recipe_name);
            ingredientsListIntent.putExtra(widgetSelectedRecipeKey, recipeData.name);
        } else {
            Log.v(TAG, "Updating the widget to clear the previously selected ingredient");

            // display the default text if no recipe data was passed in
            String noRecipeDescription = context.getString(R.string.widget_no_recipe_selected_hint);

            remoteViews.setTextViewText(R.id.recipeName, noRecipeDescription);
        }

        // Set the uri intent scheme to trigger an update on the widget's ingredients list
        // This logic is inspired by the stack overflow post at
        // https://stackoverflow.com/questions/39587137/how-to-pass-data-and-open-an-activity-from-widget-android
        ingredientsListIntent.setData(Uri.parse(ingredientsListIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // set the adapter for the list of ingredients
        remoteViews.setRemoteAdapter(R.id.WidgetIngredientsListView, ingredientsListIntent);

        //setting an empty view in case of no ingredients
        remoteViews.setEmptyView(R.id.WidgetIngredientsListView, R.id.empty_view);

        // set up the pending intent so if an ingredient is clicked the app will be opened and the recipe details will be shown
        if (recipeData != null) {
            Intent appIntent = new Intent(context, AllRecipesActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.WidgetIngredientsListView, appPendingIntent);
        }

        // update the app widget to the created remoteView
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

    }

    static public void updateAllWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds, RecipeData recipeData) {
        Log.v(TAG, "Updating all widgets");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeData);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.WidgetIngredientsListView);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}