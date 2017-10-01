package com.example.android.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.bakingapp.Ingredient;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.data.IngredientsContract;

import fr.arnaudguyon.logfilter.Log;

import static com.example.android.bakingapp.data.IngredientsContract.BASE_CONTENT_URI;
import static com.example.android.bakingapp.data.IngredientsContract.PATH_INGREDIENTS;


/**
 * Created by Daniel on 9/12/2017.
 * <p>
 * This class was inspired by the MyGarden app GridWidgetService class
 * <p>
 * Updates the list of ingredients on the widget by querying the ingredients database
 */
public class IngredientsWidgetService extends RemoteViewsService {

    // Store the class name for logging
    private static final String TAG = IngredientsWidgetService.class.getSimpleName();

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        // get the name of the recipe if it was passed in. This is used to select
        // the correct recipe in the app when the user clicks on the widget
        String recipeName = "";
        if (intent.hasExtra(getString(R.string.wiget_selected_recipe_name))) {
            recipeName = intent.getStringExtra(getString(R.string.wiget_selected_recipe_name));
        }

        Log.v(TAG, "Updating widget for the recipe: " + recipeName);

        return (new ListProvider(this.getApplicationContext(), recipeName));
    }

    /**
     * Retrieves ingredients data and applies it to the individual items in the widget
     */
    private class ListProvider implements RemoteViewsFactory {
        Cursor mCursor;
        private Context context = null;
        String recipeName = "";

        /**
         * Get the recipe name and the passed in context
         */
        public ListProvider(Context context, String recipeName) {
            this.context = context;
            this.recipeName = recipeName;
        }

        @Override
        public void onCreate() {

        }

        /**
         * When the data changes query the ingredients database to get the
         * ingredients for the selected recipe
         */
        @Override
        public void onDataSetChanged() {
            Log.v(TAG, "Data set changed, querying the ingredients database for the latest information");
            Uri INGREDIENTS_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

            if (mCursor != null) {
                mCursor.close();
            }
            mCursor = context.getContentResolver().query(
                    INGREDIENTS_URI,
                    null,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onDestroy() {
            // close the cursor if it isn't already null
            if (mCursor != null) {
                mCursor.close();
            }
        }

        /**
         * Get the number of ingredients that are in the selected recipe
         *
         * @return the number of ingredients in the selected recipe or 0 if no
         * recipe has been selected
         */
        @Override
        public int getCount() {
            if (mCursor != null) {
                return mCursor.getCount();
            }

            Log.v(TAG, "Widget cursor is empty returning 0 as the current number of ingredients");
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        /**
         * Get the view at the given position
         *
         * @param position the position of the ingredient to return the view for
         * @return the view to be displayed for the specified ingredient
         */
        @Override
        public RemoteViews getViewAt(int position) {

            Log.v(TAG, "Updating the widget ingredient at: " + position);

            // If there is no cursor to retrieve data from or if there are no ingredients return null
            if (mCursor == null || mCursor.getCount() == 0) {
                Log.e(TAG, "Widget is attempting to retrieve an existent ingredient in an unexpected manor, position = " + position);
                return null;
            }
            //move to the correction position and retrieve the data about the specified ingredient
            mCursor.moveToPosition(position);

            int quantityIndex = mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY);
            int nameIndex = mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_NAME);
            int measureIndex = mCursor.getColumnIndex(IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_MEASURE);

            Double quantity = mCursor.getDouble(quantityIndex);
            String name = mCursor.getString(nameIndex);
            String measure = mCursor.getString(measureIndex);

            // Create a remote view to display data about an individual ingredient
            final RemoteViews remoteView = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget_single_ingredient);

            // Format the information retrieved about the ingredient and display it in the ingredientInfo textView
            String ingredientInfo = Ingredient.formatIngredientText(quantity, measure, name);
            remoteView.setTextViewText(R.id.title, ingredientInfo);

            Log.v(TAG, "updating ingredient item to: " + ingredientInfo);

            // Whe an ingredient is clicked on take open the app and select the recipe the widget is displaying data for
            Intent fillInIntent = new Intent();
            if (!TextUtils.isEmpty(recipeName)) {
                Bundle extras = new Bundle();
                String widgetSelectedRecipeKey = context.getString(R.string.wiget_selected_recipe_name);
                extras.putString(widgetSelectedRecipeKey, recipeName);
                fillInIntent.putExtras(extras);
            } else {
                Log.e(TAG, "Ingredient with no name was trying to be displayed, ingredientInfo = " + ingredientInfo);
            }
            remoteView.setOnClickFillInIntent(R.id.widgetItem, fillInIntent);

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }
    }

}