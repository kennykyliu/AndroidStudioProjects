/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mycompany.personalhealthmanagement;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.transition.Transition;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Our secondary Activity which is launched from {@link MainActivity}. Has a simple detail UI
 * which has a large banner image, title and body text.
 */
public class DetailActivity extends Activity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_ID = "detail:_id";

    // View name of the header image. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_IMAGE = "detail:header:image";

    // View name of the header title. Used for activity scene transitions
    public static final String VIEW_NAME_HEADER_TITLE = "detail:header:title";

    // Plot data
    public static final String PLOT_DATA_X = "detail:plot_x";
    public static final String PLOT_DATA_Y = "detail:plot_y";
    public static final String PLOT_DATA_X_SIZE = "detail:plot_x_size";
    public static final String PLOT_DATA_Y_SIZE = "detail:plot_y_size";
    public static final String PLOT_TYPE = "detail:plot_type";
    public static final String PLOT_UNIT = "detail:plot_unit";

    public static double bmi = 0;
    public static long bmiUpdate = 0;
    public static int cal = 0;
    public static long calUpdate = 0;

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;
    private TextView mItemContent;

    private com.mycompany.personalhealthmanagement.Item mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);

        // Retrieve the correct Item instance, using the ID provided in the Intent
        mItem = com.mycompany.personalhealthmanagement.Item.getItem(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        mHeaderImageView = (ImageView) findViewById(R.id.imageview_header);
        mHeaderTitle = (TextView) findViewById(R.id.textview_title);
        mItemContent = (TextView) findViewById(R.id.itemContent);

        // BEGIN_INCLUDE(detail_set_view_name)
        /**
         * Set the name of the view's which will be transition to, using the static values above.
         * This could be done in the layout XML, but exposing it via static variables allows easy
         * querying from other Activities
         */
        ViewCompat.setTransitionName(mHeaderImageView, VIEW_NAME_HEADER_IMAGE);
        ViewCompat.setTransitionName(mHeaderTitle, VIEW_NAME_HEADER_TITLE);
        // END_INCLUDE(detail_set_view_name)

        loadItem();
    }

    public void onDetailBackClick(View view) {
        super.onBackPressed();
    }

    private void loadItem() {
        // Set the title TextView to the item's name and author
        mHeaderTitle.setText(getString(R.string.image_header, mItem.getName()));
        String str = "";
        if ("Summary".equals(mItem.getName())) {
            if (bmi > 0 && bmiUpdate > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date d = new Date(bmiUpdate);
                DecimalFormat df = new DecimalFormat("#.##");
                str = getString(R.string.bmi_intro) + "\n\n\n\n" +
                        "\t\t\t\t\t\tYour BMI is " + df.format(bmi) + "    " + dateFormat.format(d) + "\n";
            /* Underweight = < 18.5
               Normal weight = 18.5 ~ 24.9
               Overweight = 25 ~ 29.9
               Obesity = BMI of 30 or greater */
                if (bmi < 18.5) {
                    str += "\t\t\t\t\t\tYou are underweight";
                } else if (bmi >= 18.5 && bmi < 25) {
                    str += "\t\t\t\t\t\tYou are normal weight";
                } else if (bmi >= 25 && bmi < 30) {
                    str += "\t\t\t\t\t\tYou are overweight. Time to exercise.";
                } else if (bmi >= 30) {
                    str += "\t\t\t\t\t\tYou are obesity. Time to control diet and exercise more.";
                }
            } else {
                str += getString(R.string.bmi_intro);
            }

            if (cal != 0 && calUpdate > 0) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                Date d = new Date(calUpdate);
                str += "\n\n\n\n" + getString(R.string.cal_intro) + "\n\n\n\n" +
                        "\t\t\t\t\t\tYour calories is " + cal + "    " + dateFormat.format(d) + "\n";
            } else {
                str += "\n\n\n\n" + getString(R.string.cal_intro);
            }
            mItemContent.setText(str);
        } else {
            mItemContent.setText("Under construction. Coming soon......");
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && addTransitionListener()) {
            // If we're running on Lollipop and we have added a listener to the shared element
            // transition, load the thumbnail. The listener will load the full-size image when
            // the transition is complete.
            loadThumbnail();
        } else {
            // If all other cases we should just load the full-size image now
            loadFullSizeImage();
        }
    }

    /**
     * Load the item's thumbnail image into our {@link ImageView}.
     */
    private void loadThumbnail() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem.getThumbnailId())
                .noFade()
                .into(mHeaderImageView);
    }

    /**
     * Load the item's full-size image into our {@link ImageView}.
     */
    private void loadFullSizeImage() {
        Picasso.with(mHeaderImageView.getContext())
                .load(mItem.getPhotoId())
                .noFade()
                .noPlaceholder()
                .into(mHeaderImageView);
    }

    /**
     * Try and add a {@link Transition.TransitionListener} to the entering shared element
     * {@link Transition}. We do this so that we can load the full-size image after the transition
     * has completed.
     *
     * @return true if we were successful in adding a listener to the enter transition
     */
    private boolean addTransitionListener() {
        final Transition transition = getWindow().getSharedElementEnterTransition();

        if (transition != null) {
            // There is an entering shared element transition so add a listener to it
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    // As the transition has ended, we can now load the full-size image
                    loadFullSizeImage();

                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    // Make sure we remove ourselves as a listener
                    transition.removeListener(this);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // No-op
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // No-op
                }
            });
            return true;
        }

        // If we reach here then we have not added a listener
        return false;
    }

}
