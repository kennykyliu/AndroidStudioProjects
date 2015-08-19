package com.mycompany.personalhealthmanagement;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CaloriesMain extends ActionBarActivity implements ActionBar.TabListener, TaskCompleted {

    private final String TAG = "PHM-Cal-selection";
    ListView itemList;
    private TextView calTitleTextView;
    private TextView calResultTextView;
    private static ArrayList<String> foodCheckedList = null;
    private static ArrayList<String> beverageCheckedList = null;
    private static ArrayList<String> exerciseCheckedList = null;
    private static int totalCal = 0;
    private static int savedCal = 0;
    private long date = 0;
    private TaskCompleted mCallback;
    private ProgressDialog dialog;
    private CaloriesItems caloriesItems;

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

        caloriesItems = new CaloriesItems();
        // Add three tabs to the Action Bar for display
        ab.addTab(ab.newTab().setText("Food").setTabListener(this));
        ab.addTab(ab.newTab().setText("Beverage").setTabListener(this));
        ab.addTab(ab.newTab().setText("Exercise").setTabListener(this));
        if (foodCheckedList == null)
            foodCheckedList = new ArrayList<String>();
        if (beverageCheckedList == null)
            beverageCheckedList = new ArrayList<String>();
        if (exerciseCheckedList == null)
            exerciseCheckedList = new ArrayList<String>();
        calTitleTextView = (TextView) findViewById(R.id.calTitleTextView);
        calResultTextView = (TextView) findViewById(R.id.calResultTextView);

        dialog = ProgressDialog.show(CaloriesMain.this, "Data Syncing...",
                "Please wait");
        UserProfile userPref = new UserProfile(Constants.DynamoDBManagerType.LIST_ITEMS);
        new DynamoDBManagerTask().execute(userPref);
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
            return caloriesItems.getFoodNum();
        }

        @Override
        public String getItem(int position) {
            return caloriesItems.getFoodByPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return CaloriesItems.CalItemsFood[position].hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            /* position will be accessed from inner class, so need to be final */
            final int pos = position;


            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }
            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            if (foodCheckedList.contains(getItem(pos))) {
                calItemCheck.setChecked(true);
            } else {
                calItemCheck.setChecked(false);
            }
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked()) {
                        Log.i(TAG, getItem(pos) + " be checked!!!");
                        Log.i(TAG, "ADD " + pos);
                        foodCheckedList.add(getItem(pos));
                        totalCal += caloriesItems.getCalByFood(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    } else {
                        Log.i(TAG, getItem(pos) + " be unchecked...");
                        Log.i(TAG, "Remove " + pos);
                        if (foodCheckedList.contains(getItem(pos)))
                            foodCheckedList.remove(getItem(pos));
                        totalCal -= caloriesItems.getCalByFood(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    }
                }
            });

            ((TextView) convertView.findViewById(R.id.CalItemNameTextView))
                    .setText(caloriesItems.getFoodByPosition(position));
            ((TextView) convertView.findViewById(R.id.CalNumberTextView))
                    .setText(caloriesItems.getCalByFood(position) + " Calories");
            return convertView;
        }
    }

    private class CalItemsBeverageAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return caloriesItems.getBeverageNum();
        }

        @Override
        public String getItem(int position) {
            return caloriesItems.getBeverageByPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return caloriesItems.getBeverageByPosition(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final int pos = position;

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }
            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            if (beverageCheckedList.contains(getItem(pos))) {
                calItemCheck.setChecked(true);
            } else {
                calItemCheck.setChecked(false);
            }
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked()) {
                        Log.i(TAG, getItem(pos) + " be checked!!!");
                        beverageCheckedList.add(getItem(pos));
                        totalCal += caloriesItems.getCalByBeverage(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    } else {
                        Log.i(TAG, getItem(pos) + " be unchecked...");
                        if (beverageCheckedList.contains(getItem(pos))) {
                            beverageCheckedList.remove(getItem(pos));
                        }
                        totalCal -= caloriesItems.getCalByBeverage(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    }
                }
            });

            ((TextView) convertView.findViewById(R.id.CalItemNameTextView))
                    .setText(getItem(position));
            ((TextView) convertView.findViewById(R.id.CalNumberTextView))
                    .setText(caloriesItems.getCalByBeverage(position)  + " Calories");
            return convertView;
        }
    }

    private class CalItemsExerciseAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return caloriesItems.getExerciseNum();
        }

        @Override
        public String getItem(int position) {
            return caloriesItems.getExerciseByPosition(position);
        }

        @Override
        public long getItemId(int position) {
            return caloriesItems.getExerciseByPosition(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {
            final int pos = position;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.calories_list_item, container, false);
            }

            final CheckBox calItemCheck = (CheckBox) convertView.findViewById(R.id.CalItemCheck);
            if (exerciseCheckedList.contains(getItem(pos))) {
                calItemCheck.setChecked(true);
            } else {
                calItemCheck.setChecked(false);
            }
            calItemCheck.setOnClickListener(new CheckBox.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (calItemCheck.isChecked()) {
                        Log.i(TAG, getItem(pos) + " be checked!!!");
                        exerciseCheckedList.add(getItem(pos));
                        totalCal -= caloriesItems.getCalByExercise(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    } else {
                        Log.i(TAG, getItem(pos) + " be unchecked...");
                        if (exerciseCheckedList.contains(getItem(pos))) {
                            exerciseCheckedList.remove(getItem(pos));
                        }
                        totalCal += caloriesItems.getCalByExercise(pos);
                        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
                    }
                }
            });
            ((TextView) convertView.findViewById(R.id.CalItemNameTextView))
                    .setText(getItem(position));
            ((TextView) convertView.findViewById(R.id.CalNumberTextView))
                    .setText(caloriesItems.getCalByExercise(position)  + " Calories");
            return convertView;
        }
    }

    public void retrieveCalData(
            ArrayList<UserProfile> localUP) {
        for (UserProfile res : localUP) {
            if ((res.getIndexNo() / 1000) % 1000 == Constants.currUserID) {
                String key = res.getItemName();
                if (key == null) {
                    continue;
                } else if (key.equals("totalCal")) {
                    Calendar cal = Calendar.getInstance();
                    long todayInMillis = cal.getTimeInMillis();
                    Date today = new Date(todayInMillis);
                    Date dataDay = new Date(res.getDate());
                    if (today.getYear() == dataDay.getYear() &&
                            today.getMonth() == dataDay.getMonth() &&
                            today.getDay() == dataDay.getDay()) {
                        date = res.getDate();
                        totalCal = res.getNValue();
                        savedCal = totalCal;
                    }
                }
            }
        }

        if (date == 0) {
            Calendar cal = Calendar.getInstance();
            date = cal.getTimeInMillis();
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d = new Date(date);
        calTitleTextView.setText(dateFormat.format(d));
        calResultTextView.setText("Total Calories: " + Integer.toString(totalCal));
        DetailActivity.cal = totalCal;
        DetailActivity.calUpdate = date;
    }

    public void onCalSaveClick(View view) {
        if (date == 0) {
            Calendar cal = Calendar.getInstance();
            date = cal.getTimeInMillis();
        }

        long itemIndex = date / 1000000 * 1000000 + Constants.currUserID * 1000;
        Log.i(TAG, "save cal = " + savedCal + " to cal = " + totalCal);
        savedCal = totalCal;
        DetailActivity.cal = totalCal;
        DetailActivity.calUpdate = date;
        dialog = ProgressDialog.show(CaloriesMain.this, "Data Syncing...",
                "Please wait");
        UserProfile userPref = new UserProfile(itemIndex++, Constants.currUserName, "totalCal",
                date, totalCal, null, Constants.DynamoDBManagerType.SYNC_TOTAL_CAL);
        new DynamoDBManagerTask().execute(userPref);
    }

    public void onCalBackClick(View view) {
        onBackPressed();
    }

    @Override
    public void onTaskComplete(Constants.DynamoDBManagerType ddbType,
                               ArrayList<UserProfile> localUP) {
        switch (ddbType) {
            case SYNC_TOTAL_CAL:
                dialog.dismiss();
                break;
            case LIST_ITEMS:
                retrieveCalData(localUP);
                dialog.dismiss();
            default:
                break;
        }
    }

    private class DynamoDBManagerTask extends
            AsyncTask<UserProfile, Void, AWSDynamoDBManagerTaskResult> {
        @Override
        protected void onPreExecute() {
            mCallback = (TaskCompleted) CaloriesMain.this;
        }

        protected AWSDynamoDBManagerTaskResult doInBackground(
                UserProfile... userPref) {

            String tableStatus = DynamoDBManager.getTestTableStatus();
            Constants.DynamoDBManagerType actiontype = userPref[0].getActionType();

            AWSDynamoDBManagerTaskResult result = new AWSDynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(actiontype);

            if (actiontype== Constants.DynamoDBManagerType.SYNC_TOTAL_CAL) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.insertItem(userPref[0]);
                }
            } else if (actiontype == Constants.DynamoDBManagerType.LIST_ITEMS) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    result.setItemList(DynamoDBManager.getItemList());
                }
            }

            return result;
        }

        protected void onPostExecute(AWSDynamoDBManagerTaskResult result) {
            if (result.getTaskType() == Constants.DynamoDBManagerType.SYNC_TOTAL_CAL ||
                    result.getTaskType() == Constants.DynamoDBManagerType.LIST_ITEMS) {
                mCallback.onTaskComplete(result.getTaskType(), result.getItemList());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (savedCal != totalCal) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Save data?");
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage("Do you want to save data before leave?");
            builder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            onCalSaveClick(null);
                            Intent intent = new Intent(
                                    CaloriesMain.this,
                                    HomeActivity.class);
                            startActivity(intent);
                        }
                    }).setCancelable(false);
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.dismiss();
                            foodCheckedList = null;
                            beverageCheckedList = null;
                            exerciseCheckedList = null;

                            Intent intent = new Intent(
                                    CaloriesMain.this,
                                    HomeActivity.class);
                            startActivity(intent);
                        }
                    }).setCancelable(false);

            builder.show();
        } else {
            super.onBackPressed();
        }
    }
}
