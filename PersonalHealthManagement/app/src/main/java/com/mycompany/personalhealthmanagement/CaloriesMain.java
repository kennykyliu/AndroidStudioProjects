package com.mycompany.personalhealthmanagement;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This sample shows you how to use ActionBarCompat with a customized theme. It utilizes a split
 * action bar when running on a device with a narrow display, and show three tabs.
 *
 * This Activity extends from {@link ActionBarActivity}, which provides all of the function
 * necessary to display a compatible Action Bar on devices running Android v2.1+.
 *
 * The interesting bits of this sample start in the theme files
 * ('res/values/styles.xml' and 'res/values-v14</styles.xml').
 *
 * Many of the drawables used in this sample were generated with the
 * 'Android Action Bar Style Generator': http://jgilfelt.github.io/android-actionbarstylegenerator
 */
public class CaloriesMain extends ActionBarActivity implements ActionBar.TabListener {
    ListView itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_calories_main);
        setContentView(R.layout.activity_calories_item_select);
        //itemList = (ListView) findViewById(R.id.itemList);
        //itemList.setAdapter(new MyAdapter());

        // Set the Action Bar to use tabs for navigation
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add three tabs to the Action Bar for display
        ab.addTab(ab.newTab().setText("Tab 1").setTabListener(this));
        ab.addTab(ab.newTab().setText("Tab 2").setTabListener(this));
        ab.addTab(ab.newTab().setText("Tab 3").setTabListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu from menu resource (res/menu/main)
        //getMenuInflater().inflate(R.menu.menu_calories_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a tab is selected.
        int pos = tab.getPosition();
        //TextView infoText = (TextView) findViewById(R.id.infoText);
        itemList = (ListView) findViewById(R.id.itemList);
        switch (pos) {
            case 0:
                itemList.setAdapter(new CalItemsFoodAdapter());
                break;
            case 1:
                itemList.setAdapter(new CalItemsBeverageAdapter());
                break;
            case 2:
                itemList.setAdapter(new CalItemsExerciseAdapter());
                break;
            default:
        }
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is unselected.
    }

    // Implemented from ActionBar.TabListener
    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // This is called when a previously selected tab is selected again.
    }

    /**
     * A simple array adapter that creates a list of cheeses.
     */
    private class CalItemsFoodAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CaloriesItems.CalItemsFood.length;
        }

        @Override
        public String getItem(int position) {
            return CaloriesItems.CalItemsFood[position];
        }

        @Override
        public long getItemId(int position) {
            return CaloriesItems.CalItemsFood[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final int pos = position;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }
            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked())
                        Log.i("calmain", getItem(pos) + " be clicked!!!");
                    else
                        Log.i("calmain", getItem(pos) + " be unclicked...");
                }
            });

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }
    }

    private class CalItemsBeverageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CaloriesItems.CalItemBeverage.length;
        }

        @Override
        public String getItem(int position) {
            return CaloriesItems.CalItemBeverage[position];
        }

        @Override
        public long getItemId(int position) {
            return CaloriesItems.CalItemBeverage[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final int pos = position;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }
            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked())
                        Log.i("calmain", getItem(pos) + " be clicked!!!");
                    else
                        Log.i("calmain", getItem(pos) + " be unclicked...");
                }
            });

            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }
    }

    private class CalItemsExerciseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return CaloriesItems.CalItemExercise.length;
        }

        @Override
        public String getItem(int position) {
            return CaloriesItems.CalItemExercise[position];
        }

        @Override
        public long getItemId(int position) {
            return CaloriesItems.CalItemExercise[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final int pos = position;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }

            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked())
                        Log.i("calmain", getItem(pos) + " be clicked!!!");
                    else
                        Log.i("calmain", getItem(pos) + " be unclicked...");
                }
            });
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(getItem(position));
            return convertView;
        }
    }
}
