package com.example.android.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by Daniel on 9/1/2017.
 * <p>
 * Stores data about a single ingredient
 */
public class Ingredient implements Parcelable {
    String measure;
    double quantity;
    String name;

    protected Ingredient() {
    }

    /**
     * Get a human readable representation of the properties that make up an ingredient
     *
     * @param quantity The quantity of the ingredient required
     * @param measure  The unit of measurement for the quantity
     * @param name     The name of the ingredient
     * @return Return a human readable representation of the properties that make up an ingredient
     */
    public static String formatIngredientText(double quantity, String measure, String name) {
        // If a quantity and measure where supplied use them to build the string representation
        // otherwise simply return the name
        if (quantity > 0 && measure != null) {
            return formatDouble(quantity) + " " + measure + " of " + name;
        } else {
            return name;
        }
    }

    /**
     * Format the quantity to only contain the necessary amount of digits after the decimal place
     * for example 1 instead of 1.0 and 0.5 would remain 0.5
     * <p>
     * Inspired by the stack overflow question: //https://stackoverflow.com/a/14126736
     *
     * @param d The number to format
     * @return the string representation of the number that only contains a decimal value if required
     */
    public static String formatDouble(double d) {
        if (d == (long) d)
            return String.format(Locale.getDefault(), "%d", (long) d);
        else
            return String.format(Locale.getDefault(), "%s", d);
    }

    /**
     * Get the ingredient as a string
     *
     * @return a string that contains all the information about the
     * ingredient in a human readable form
     */
    @Override
    public String toString() {
        return formatIngredientText(quantity, measure, name);
    }

    /**
     * Parcelable implementation in order to pass this object in an intent
     **/


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(measure);
        dest.writeDouble(quantity);
        dest.writeString(name);
    }

    protected Ingredient(Parcel in) {
        measure = in.readString();
        quantity = in.readDouble();
        name = in.readString();
    }

    public static final Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {
        @Override
        public Ingredient createFromParcel(Parcel in) {
            return new Ingredient(in);
        }

        @Override
        public Ingredient[] newArray(int size) {
            return new Ingredient[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}