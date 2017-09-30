package com.example.android.bakingapp.data;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeData;
import com.example.android.bakingapp.widget.IngredientsWidgetUpdateService;

/**
 * Created by Daniel on 9/17/2017.
 * <p>
 * Asynchronously makes requests to the content provider
 * <p>
 * I referenced the link below when creating this class to use an asyncqueryhandler to
 * perform content provider calls asynchronously
 * http://codetheory.in/using-asyncqueryhandler-to-access-content-providers-asynchronously-in-android/
 */
public class IngredientsQueryHandler extends AsyncQueryHandler {
    private RecipeData singleRecipe;
    private Context context;

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        Intent intent = new Intent(context, IngredientsWidgetUpdateService.class);

        // Pass the name of the selected recipe to the widgets
        if (singleRecipe != null) {
            intent.putExtra(context.getString(R.string.single_recipe_data), singleRecipe);
        }

        // update the ingredients widgets now that the query has been completed
        context.startService(intent);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        // update the widgets so they no longer display data about
        // the previously selected recipe
        Intent intent = new Intent(context, IngredientsWidgetUpdateService.class);
        context.startService(intent);
    }

    /**
     * Used to perform async requests on the content provider when you
     * want to supply the recipe that has been selected
     *
     * @param cr
     * @param context
     * @param singleRecipe The selected recipe
     */
    public IngredientsQueryHandler(ContentResolver cr, Context context, RecipeData singleRecipe) {
        super(cr);
        this.context = context;
        this.singleRecipe = singleRecipe;
    }

    /**
     * Used to perform async requests on the content provider
     *
     * @param cr
     * @param context
     */
    public IngredientsQueryHandler(ContentResolver cr, Context context) {
        super(cr);
        this.context = context;

    }
}