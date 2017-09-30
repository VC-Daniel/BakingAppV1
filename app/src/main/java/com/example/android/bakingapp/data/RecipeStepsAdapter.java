package com.example.android.bakingapp.data;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStep;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel on 9/26/2017.
 * <p>
 * Facilitates displaying all the steps in a recipe. This class was inspired by the TeaMenuAdapter
 * class in the TeaTime app
 */

public class RecipeStepsAdapter extends ArrayAdapter<RecipeStep> {

    private Context mContext;
    private int layoutResourceId;
    private List<RecipeStep> data = new ArrayList();


    public RecipeStepsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<RecipeStep> data) {
        super(context, resource, data);

        // store the passed in data
        this.layoutResourceId = resource;
        this.mContext = context;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Build out the view for a specific recipe step
        singleStepViewHolder holder;

        // get the data for this specific step
        RecipeStep currentRecipeStep = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);
            holder = new singleStepViewHolder();
            holder.mStepDescription = (TextView) convertView.findViewById(R.id.recipe_step_description);
            convertView.setTag(holder);
        } else {
            holder = (singleStepViewHolder) convertView.getTag();
        }

        // Display the description for the current step
        holder.mStepDescription.setText(currentRecipeStep.shortDescription);
        return convertView;
    }

    // Contains data for a single step
    private class singleStepViewHolder {
        private TextView mStepDescription;

        @Override
        public String toString() {
            return super.toString() + " '" + mStepDescription.getText() + "'";
        }
    }
}