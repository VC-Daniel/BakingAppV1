package com.example.android.bakingapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Daniel on 9/14/2017.
 * <p>
 * Stores constants used to create and interact with the ingredients database
 */
public class IngredientsContract implements BaseColumns {

    public static final String AUTHORITY = "com.example.android.bakingapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_INGREDIENTS = "Ingredients";

    // Ingredients table and column names
    public static final class IngredientsEntry implements BaseColumns {
        public static final String TABLE_NAME = "Ingredients";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INGREDIENTS).build();

        public static final String COLUMN_INGREDIENT_NAME = "ingredient_name";
        public static final String COLUMN_INGREDIENT_QUANTITY = "ingredient_quantity";
        public static final String COLUMN_INGREDIENT_MEASURE = "ingredient_measure";
    }
}