package com.example.android.bakingapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Daniel on 9/14/2017.
 * <p>
 * Helps to interact with the ingredients database. The ingredients database
 * stores all the ingredients data about the currently selected recipe
 */
public class IngredientsDBHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "ingredientsDb.db";

    // If you change the database schema, you must increment the database version
    private static final int VERSION = 1;

    // Constructor
    IngredientsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    /*
    Create the ingredients database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // create the database with the columns in the IngredientsContract
        final String CREATE_TABLE = "CREATE TABLE " + IngredientsContract.IngredientsEntry.TABLE_NAME + " (" +
                IngredientsContract._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_QUANTITY + " REAL, " +
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_NAME + " TEXT NOT NULL, " +
                IngredientsContract.IngredientsEntry.COLUMN_INGREDIENT_MEASURE + " TEXT"
                + ");";

        db.execSQL(CREATE_TABLE);
    }

    /*
    Upgrade the ingredients database, currently not implemented
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
