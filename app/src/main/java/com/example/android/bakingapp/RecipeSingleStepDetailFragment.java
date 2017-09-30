package com.example.android.bakingapp;

import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import fr.arnaudguyon.logfilter.Log;

/**
 * A fragment representing a single Recipe detail screen. Displays the recipe information such as
 * a video and text description.
 * This fragment is either contained in a {@link RecipeAllStepsListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeSingleStepDetailActivity}
 * on handsets.
 * <p>
 * This is partially based off the QuizActivity class in the classicalMusicQuiz app
 */
public class RecipeSingleStepDetailFragment extends Fragment implements ExoPlayer.EventListener {

    SimpleExoPlayerView mSimplePlayerView;
    private SimpleExoPlayer mExoPlayer;

    // the data for the selected recipe step
    private RecipeStep mItem;

    private static final String TAG = RecipeSingleStepDetailFragment.class.getSimpleName();

    // Get the default bandwidth meter to pass into the exoplayer
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    // The application name to pass into the userAgent
    String mApplicationName = "BakingApp";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeSingleStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.v(TAG, "Displaying the details of the a recipe step");

        // Load the recipe step data if it was passed in
        final String stepDataKey = getString(R.string.recipe_step_data);
        if (getArguments().containsKey(stepDataKey)) {
            mItem = getArguments().getParcelable(stepDataKey);
        }
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the step detail video to play.
     */
    private void initializePlayer(String mediaUri) {

        // check if there is a video to display for the selected step. If the is a video display it,
        // otherwise hide the simple player
        if (mediaUri != null && !mediaUri.equals("")) {
            Log.v(TAG, "Initializing the media player to play the step details from: " + mediaUri);

            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);

            // add this class as the listener so we can properly handle when events are triggered
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getContext(), mApplicationName);

            mSimplePlayerView.requestFocus();
            mSimplePlayerView.setUseArtwork(false);
            mSimplePlayerView.setPlayer(mExoPlayer);

            DataSource.Factory dataSourceFactory =
                    new DefaultHttpDataSourceFactory(userAgent, BANDWIDTH_METER);
            DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(mediaUri),
                    dataSourceFactory, extractorsFactory, null, null);

            // Prepare the media and begin playing as soon as the video can
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        } else {
            Log.v(TAG, "No video was provided for this step");

            // if there isn't a video for this step hide the simple player
            mSimplePlayerView.setVisibility(View.GONE);

        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        Log.v(TAG, "Releasing the media player");
        if (mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
        if(mSimplePlayerView != null)
        {
            mSimplePlayerView.setEnabled(false);
            mSimplePlayerView = null;
        }
    }

    @Override
    public void onDestroy() {
        releasePlayer();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.recipe_single_step_detail, container, false);
        mSimplePlayerView = (SimpleExoPlayerView) rootView.findViewById(R.id.playerView);

        // If the user is on a phone and is in landscape orientation then display the video as full screen
        // The logic to set the size of the simple player is based off the stack overflow question at:
        // https://stackoverflow.com/questions/3144940/set-imageview-width-and-height-programmatically
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        if (getActivity().getClass() == RecipeSingleStepDetailActivity.class && (display.getRotation() == Surface.ROTATION_90 || display.getRotation() == Surface.ROTATION_270)) {
            Log.i(TAG, "Displaying the recipe step video in full screen mode");
            display.getSize(size);
            mSimplePlayerView.getLayoutParams().height = (int) (size.y * 0.8);
        }

        // Display the passed in recipe step's description and video
        if (mItem != null) {
            if (mItem.description.equals("") && mItem.videoURL.equals("")) {
                Log.e(TAG, "The recipe step contains no video or description. Step short Description: " + mItem.shortDescription);
            }

            ((TextView) rootView.findViewById(R.id.recipe_detail)).setText(mItem.description);
            initializePlayer(mItem.videoURL);
        }

        return rootView;
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

}