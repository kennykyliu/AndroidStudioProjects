package com.mycompany.personalhealthmanagement;


import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Displays a grid of items which an image and title. When the
 * user clicks on an item, {@link DetailActivity} is launched, using the Activity Scene Transitions
 * framework to animatedly do so.
 */
public class HomeActivity extends Activity implements AdapterView.OnItemClickListener {
    private static final String TAG = "PHM-Home";
    private static final String KEY_DATASET_NAME = "dataset_name";

    private GridView mGridView;
    private GridAdapter mAdapter;
    private AWSDataHandler awsDataHandler;
    private Dataset personalDataset;
    private String datasetName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid);

        // Setup the GridView and set the adapter
        mGridView = (GridView) findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new GridAdapter();
        mGridView.setAdapter(mAdapter);

        Bundle bundle = getIntent().getExtras();
        awsDataHandler = AWSDataHandler.getInstance();
        datasetName = bundle.getString(KEY_DATASET_NAME);
        Log.i("Home", "datasetName = " + datasetName);
        personalDataset = awsDataHandler.getDataSet(datasetName);
        for (Record record : personalDataset.getAllRecords()) {
            Log.i("Home", record.getKey() + "/ " + record.getValue());
        }
    }

    /**
     * Called when an item in the {@link android.widget.GridView} is clicked. Here will launch the
     * {@link DetailActivity}, using the Scene Transition animation functionality.
     */
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Item item = (Item) adapterView.getItemAtPosition(position);

        com.mycompany.personalhealthmanagement.Item mItem =
                com.mycompany.personalhealthmanagement.Item.getItem(item.getId());
        Log.i(TAG, "Name = " + mItem.getName());
        if (mItem.getName().equals("Personal Info")) {
            Intent intent = new Intent(this, PersonalInfo.class);
            intent.putExtra(KEY_DATASET_NAME, datasetName);
            startActivity(intent);
        } else {
            // Construct an Intent as normal
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_PARAM_ID, item.getId());

            // BEGIN_INCLUDE(start_activity)
            /**
             * Now create an {@link android.app.ActivityOptions} instance using the
             * {@link ActivityOptionsCompat#makeSceneTransitionAnimation(Activity, Pair[])} factory
             * method.
             */
            ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,

                    // Now we provide a list of Pair items which contain the view we can transitioning
                    // from, and the name of the view it is transitioning to, in the launched activity
                    new Pair<View, String>(view.findViewById(R.id.imageview_item),
                            DetailActivity.VIEW_NAME_HEADER_IMAGE),
                    new Pair<View, String>(view.findViewById(R.id.textview_name),
                            DetailActivity.VIEW_NAME_HEADER_TITLE));

            // Now we can start the Activity, providing the activity options as a bundle
            ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
            // END_INCLUDE(start_activity)
        }
    }

    /**
     * {@link android.widget.BaseAdapter} which displays items.
     */
    private class GridAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Item.ITEMS.length;
        }

        @Override
        public Item getItem(int position) {
            return Item.ITEMS[position];
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            final Item item = getItem(position);

            // Load the thumbnail image
            ImageView image = (ImageView) view.findViewById(R.id.imageview_item);
            Picasso.with(image.getContext()).load(item.getThumbnailId()).into(image);

            // Set the TextView's contents
            TextView name = (TextView) view.findViewById(R.id.textview_name);
            name.setText(item.getName());

            return view;
        }
    }
}
