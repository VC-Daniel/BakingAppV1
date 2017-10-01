package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by Daniel on 8/30/2017.
 * <p>
 * Display data about the recipes
 */
public class RecipeDataAdapter extends RecyclerView.Adapter<RecipeDataAdapter.RecipeAdapterViewHolder> {

    /**
     * Stores the data for all the recipes
     */
    ArrayList<RecipeData> mRecipeData = new ArrayList<>();

    /**
     * Handles when a recipe is clicked on
     */
    private final RecipeDataAdapterOnClickHandler mClickHandler;

    /**
     * when the view is created display the recipes information
     **/
    @Override
    public void onBindViewHolder(RecipeAdapterViewHolder holder, int position) {
        // Get the RecipeData for this view
        RecipeData recipeData = mRecipeData.get(position);

        // Build the string to display by filling in the information for this recipe
        String numberOfIngredientsLabel = context.getString(R.string.ingredients_label, recipeData.ingredients.size());
        String numberOfServingsLabel = context.getString(R.string.servings_label, recipeData.servings);
        String numberOfStepsLabel = context.getString(R.string.steps_label, recipeData.recipeSteps.size());

        // Display the recipe information
        holder.mRecipeNameTextView.setText(recipeData.name);
        holder.mNumberOfServingsTextView.setText(numberOfServingsLabel);
        holder.mNumberOfIngredientsTextView.setText(numberOfIngredientsLabel);
        holder.mNumberOfStepsTextView.setText(numberOfStepsLabel);

        // If there is a thumbnail for the recipe display it, otherwise hide the thumbnail view
        if (!TextUtils.isEmpty(recipeData.thumbnailLocation)) {
            Picasso.with(context).load(recipeData.thumbnailLocation).into(holder.mThumbnailImageView);
        } else {
            holder.mThumbnailImageView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Creates a RecipeDataAdapter and instantiates the clickHandler
     *
     * @param clickHandler
     */
    public RecipeDataAdapter(RecipeDataAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface RecipeDataAdapterOnClickHandler {
        void onClick(RecipeData recipeData);
    }

    Context context;

    @Override
    public RecipeAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.single_recipe_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new RecipeAdapterViewHolder(view);
    }

    class RecipeAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * Used to display the recipe information
         */
        final TextView mRecipeNameTextView;
        final TextView mNumberOfIngredientsTextView;
        final TextView mNumberOfStepsTextView;
        final TextView mNumberOfServingsTextView;
        final ImageView mThumbnailImageView;

        RecipeAdapterViewHolder(View view) {
            super(view);
            // Get the text views and set the on click listener so we can determine when
            // the user has clicked on the recipe and go to the recipes details
            mRecipeNameTextView = (TextView) view.findViewById(R.id.recipeName);
            mNumberOfIngredientsTextView = (TextView) view.findViewById(R.id.singleRecipeNumberOfIngredients);
            mNumberOfStepsTextView = (TextView) view.findViewById(R.id.singleRecipeNumberOfSteps);
            mNumberOfServingsTextView = (TextView) view.findViewById(R.id.singleRecipeServing);
            mThumbnailImageView = (ImageView) view.findViewById(R.id.recipeThumbnailImage);
            view.setOnClickListener(this);
        }

        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked
         */
        @Override
        public void onClick(View v) {
            // Pass the recipe information to the on click handler
            int adapterPosition = getAdapterPosition();
            RecipeData singleRecipe = mRecipeData.get(adapterPosition);
            mClickHandler.onClick(singleRecipe);
        }
    }

    /**
     * @return The total number of recipes we have data for
     */
    @Override
    public int getItemCount() {
        if (mRecipeData != null) {
            return mRecipeData.size();
        }
        return 0;
    }

    /**
     * Change the data that is saved in the adapter
     *
     * @param recipeData The recipes to save in the adapter
     */
    public void setRecipeData(ArrayList<RecipeData> recipeData) {
        mRecipeData = recipeData;
        notifyDataSetChanged();
    }
}