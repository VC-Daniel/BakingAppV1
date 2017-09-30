package com.example.android.bakingapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fr.arnaudguyon.logfilter.Log;

import static com.example.android.bakingapp.data.IngredientsContract.IngredientsEntry.TABLE_NAME;

/**
 * Created by Daniel on 9/14/2017.
 * Facilitates interacting with the SQLite database that stores information about the ingredients needed for the selected recipe
 * <p>
 * This class is loosely based off of the swipe to delete lesson (T09.07)
 * <p>
 * The bulk insert logic was inspired by the tutorial at:
 * http://notes.theorbis.net/2010/02/batch-insert-to-sqlite-on-android.html
 */
public class IngredientsContentProvider extends ContentProvider {

    // Store the class name for logging
    private static final String TAG = IngredientsContentProvider.class.getSimpleName();

    // Used to interact with all the ingredients in the table
    public static final int INGREDIENTS = 100;

    // Used  to get a specific ingredient based on the id
    public static final int INGREDIENTS_WITH_ID = 101;

    // Used to determine how to respond to the supplied Uri
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    // Helps this class to interact with the database
    private IngredientsDBHelper mIngredientsDbHelper;

    /**
     * Initialize a new matcher object without any matches,
     * then use .addURI(String authority, String path, int match) to add matches
     * for all the ingredients or for a specific ingredient identified by it's id
     */
    public static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches by passing in NO_MATCH to the constructor
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        //add matches for the ingredient directory and a single ingredient by ID.
        uriMatcher.addURI(IngredientsContract.AUTHORITY, IngredientsContract.PATH_INGREDIENTS, INGREDIENTS);
        uriMatcher.addURI(IngredientsContract.AUTHORITY, IngredientsContract.PATH_INGREDIENTS + "/#", INGREDIENTS_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mIngredientsDbHelper = new IngredientsDBHelper(context);
        return true;
    }

    /*
    Query the ingredients database to get either all ingredients or a specific ingredient
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Log.v(TAG, "Get a readable database to query");
        // Get a readable database since we are only performing a query we don't need a writable database
        final SQLiteDatabase db = mIngredientsDbHelper.getReadableDatabase();

        // match the  uri to determine what query should be performed
        int match = sUriMatcher.match(uri);

        // Used to return the results of the query
        Cursor resultsCursor;

        // perform the requested query
        switch (match) {
            // Query for the entire ingredients directory
            case INGREDIENTS:

                Log.v(TAG, "Retrieving all ingredients based on the supplied options");
                resultsCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            // Query for getting the specific ingredient that matches the supplied id if there is one
            case INGREDIENTS_WITH_ID:
                String id = uri.getPathSegments().get(1);
                Log.v(TAG, "Retrieving the ingredient with id=" + id);
                resultsCursor = db.query(TABLE_NAME,
                        projection,
                        IngredientsContract._ID + "=?",
                        new String[]{id},
                        null,
                        null,
                        null);
                break;
            // Throw an exception if the uri did match a known query
            default:
                Log.e(TAG, "No matching query operation was found for the uri: " + uri);
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        resultsCursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the desired Cursor
        return resultsCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /*
    Insert an ingredient into the Ingredients directory
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        Log.v(TAG, "Get a writable database to insert a new ingredient");
        // Get access to the ingredients database that we can write new data to
        final SQLiteDatabase db = mIngredientsDbHelper.getWritableDatabase();

        // check the desired operation
        int match = sUriMatcher.match(uri);

        // match the  uri to ensure a supported type of insert should be performed
        Uri returnUri = null;

        try {
            switch (match) {
                // Insert data about a new ingredient
                case INGREDIENTS:
                    long id = db.insert(TABLE_NAME, null, values);
                    Log.v(TAG, "Inserted a new ingredient with the id " + id);

                    // if a new row was inserted successfully create a uri that points to the row
                    // otherwise throw an exception
                    if (id > 0) {
                        returnUri = ContentUris.withAppendedId(IngredientsContract.IngredientsEntry.CONTENT_URI, id);
                    } else {
                        Log.v(TAG, "Error occurred while inserting a new ingredient");
                        throw new android.database.SQLException("Failed to insert row into " + uri);
                    }
                    break;
                // Throw an exception if the uri did match a known insert operation
                default:
                    Log.e(TAG,"No known insert to perform for the uri" + uri);
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        } catch (SQLException e) {
            Log.v(TAG, "Error occurred while inserting an ingredient: " + e.getMessage());
        } finally {
            // Close the database regardless of if the insert was successful
            db.close();
        }

        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri that points to the newly inserted row of data
        return returnUri;
    }

    /*
    Delete an ingredient or all ingredients
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Log.v(TAG, "Get a readable database to delete an ingredient");
        // Get access to the writable database
        final SQLiteDatabase db = mIngredientsDbHelper.getWritableDatabase();

        // Identify what the uri specifies to delete
        int match = sUriMatcher.match(uri);

        // stores the number of rows deleted
        int count = 0;

        try {
            switch (match) {
                // delete all the ingredients in the database
                case INGREDIENTS:
                    Log.v(TAG, "Deleting all ingredients");
                    // passing in 1 to get a count of rows deleted, otherwise I would just pass in null
                    count = db.delete(TABLE_NAME, "1", null);
                    break;
                // delete a specific ingredient based on the supplied id
                case INGREDIENTS_WITH_ID:
                    String id = uri.getPathSegments().get(1);
                    Log.v(TAG, "Deleting the ingredient with the id " + id);
                    // Insert new values into the database
                    // Inserting values into the table
                    count = db.delete(TABLE_NAME, IngredientsContract._ID + "=?", new String[]{id});

                    break;
                // Throw an exception if the uri did match a known delete operation
                default:
                    Log.e(TAG,"No supported delete for the uri: "  + uri);
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }

            // If more then one record has been deleted send a notification based on the supplied uri
            if (count != 0) {
                Log.v(TAG, "successfully deleted " + count + " ingredients");
                getContext().getContentResolver().notifyChange(uri, null);
            }
        } catch (SQLException e) {
            Log.v(TAG, "Error occurred while deleting ingredient(s): " + e.getMessage());
        } finally {
            // Close the database regardless of if the insert was successful
            db.close();
        }

        // Return the number of rows that were deleted
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    /*
    Insert multiple ingredients at one time. This is optimized
    for inserting more then one ingredient and is preferable to
    inserting multiple ingredients one at a time.
     */
    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        Log.v(TAG, "Get a writable database to insert all the supplied ingredients");

        // Get access to the ingredients database that we can write new data to
        final SQLiteDatabase db = mIngredientsDbHelper.getWritableDatabase();

        // match the uri to verify a supported insert is intended
        int match = sUriMatcher.match(uri);

        // stores the number records inserted
        int insertedRecords = 0;

        switch (match) {
            // Insert data about a new ingredient
            case INGREDIENTS:
                try {
                    db.beginTransaction();
                    for (ContentValues value : values) {
                        long id = db.insert(TABLE_NAME, null, value);
                        Log.v(TAG, "Inserting a new ingredient with the id " + id);
                        // if a new row was inserted successfully increment the count of the number
                        // of records inserted otherwise throw an exception
                        if (id > 0) {
                            insertedRecords = insertedRecords + 1;
                        } else {
                            Log.v(TAG, "Error occurred while inserting a new ingredient");
                            throw new android.database.SQLException("Failed to insert row into " + uri);
                        }
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(TAG, "Error occurred while inserting all the new ingredients: " + e.getMessage());
                } finally {
                    // End the transaction and close the database regardless of if the insert was successful
                    db.endTransaction();
                    db.close();
                }

                break;
            // Throw an exception if the uri did match a known insert operation
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver if the uri has been changed, and return the number of inserted ingredients
        getContext().getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return insertedRecords;
    }
}