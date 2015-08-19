package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Statistics extends ActionBarActivity implements TaskCompleted {
    private String weightUnit = "kg";
    private String heightUnit = "cm";
    private static ArrayList<String> weightKG_X_Data;
    private static ArrayList<String> weightKG_Y_Data;
    private static ArrayList<String> weightLB_X_Data;
    private static ArrayList<String> weightLB_Y_Data;
    private static ArrayList<String> heightCM_X_Data;
    private static ArrayList<String> heightCM_Y_Data;
    private static ArrayList<String> heightIN_X_Data;
    private static ArrayList<String> heightIN_Y_Data;
    private static ArrayList<String> cal_X_Data;
    private static ArrayList<String> cal_Y_Data;
    private static ArrayList<String> sleep_X_Data;
    private static ArrayList<String> sleep_Y_Data;
    private ProgressDialog dialog;
    private TaskCompleted mCallback;
    private TextView statWeightUnitTextView;
    private TextView statHeightUnitTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        statWeightUnitTextView = (TextView) findViewById(R.id.statWeightUnitTextView);
        statHeightUnitTextView = (TextView) findViewById(R.id.statHeightUnitTextView);
        statWeightUnitTextView.setText("(in kg)");
        statHeightUnitTextView.setText("(in cm)");
        Button contextMenuButton = (Button) findViewById(R.id.statButtonBontextMenuWeight);
        registerForContextMenu(contextMenuButton);

        dialog = ProgressDialog.show(Statistics.this, "Data Syncing...",
                "Please wait");
        UserProfile userPref = new UserProfile(Constants.DynamoDBManagerType.LIST_ITEMS);
        new DynamoDBManagerTask().execute(userPref);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Weight_in_kg:
                weightUnit = getString(R.string.weight_unit_kg);
                statWeightUnitTextView.setText("(in kg)");
                break;
            case R.id.Weight_in_lb:
                weightUnit = getString(R.string.weight_unit_lb);
                statWeightUnitTextView.setText("(in lb)");
                break;
            case R.id.Height_in_cm:
                heightUnit = getString(R.string.height_unit_cm);
                statHeightUnitTextView.setText("(in cm)");
                break;
            case R.id.Height_in_ft_in:
                heightUnit = getString(R.string.height_unit_in);
                statHeightUnitTextView.setText("(in ft & in)");
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void retrieveStatData(
            ArrayList<UserProfile> localUP) {
        HashMap<Long, Integer> weightKGData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> weightLBData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> heightCMData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> heightINData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> calData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> sleepData = new HashMap<Long, Integer>();
        weightKG_X_Data = new ArrayList<String>();
        weightKG_Y_Data = new ArrayList<String>();
        weightLB_X_Data = new ArrayList<String>();
        weightLB_Y_Data = new ArrayList<String>();
        heightCM_X_Data = new ArrayList<String>();
        heightCM_Y_Data = new ArrayList<String>();
        heightIN_X_Data = new ArrayList<String>();
        heightIN_Y_Data = new ArrayList<String>();
        cal_X_Data = new ArrayList<String>();
        cal_Y_Data = new ArrayList<String>();
        sleep_X_Data = new ArrayList<String>();
        sleep_Y_Data = new ArrayList<String>();


        for (UserProfile res : localUP) {
            if ((res.getIndexNo() / 1000) % 1000 == Constants.currUserID) {
                String key = res.getItemName();
                if (key == null) {
                    continue;
                }
                if (key.equals("weight_kg")) {
                    weightKGData.put(res.getDate(), res.getNValue());
                } else if (key.equals("weight_lb")) {
                    weightLBData.put(res.getDate(), res.getNValue());
                } else if (key.equals("height_cm")) {
                    heightCMData.put(res.getDate(), res.getNValue());
                } else if (key.equals("height_in")) {
                    heightINData.put(res.getDate(), res.getNValue());
                } else if (key.equals("totalCal")) {
                    calData.put(res.getDate(), res.getNValue());
                } else if (key.equals("sleep")) {
                    sleepData.put(res.getDate(), res.getNValue());
                }
            }
        }
        if (weightKGData.size() > 0) {
            Map<Long, Integer> treeMap0 = new TreeMap<Long, Integer>(weightKGData);
            for (Map.Entry<Long, Integer> entry : treeMap0.entrySet()) {
                weightKG_X_Data.add(entry.getKey().toString());
                weightKG_Y_Data.add(entry.getValue().toString());

            }
        }
        if (weightLBData.size() > 0) {
            Map<Long, Integer> treeMap = new TreeMap<Long, Integer>(weightLBData);
            for (Map.Entry<Long, Integer> entry : treeMap.entrySet()) {
                weightLB_X_Data.add(entry.getKey().toString());
                weightLB_Y_Data.add(entry.getValue().toString());
            }
        }
        if (heightCMData.size() > 0) {
            Map<Long, Integer> treeMap = new TreeMap<Long, Integer>(heightCMData);
            for (Map.Entry<Long, Integer> entry : treeMap.entrySet()) {
                heightCM_X_Data.add(entry.getKey().toString());
                heightCM_Y_Data.add(entry.getValue().toString());
            }
        }
        if (heightINData.size() > 0) {
            Map<Long, Integer> treeMap = new TreeMap<Long, Integer>(heightINData);
            for (Map.Entry<Long, Integer> entry : treeMap.entrySet()) {
                heightIN_X_Data.add(entry.getKey().toString());
                heightIN_Y_Data.add(entry.getValue().toString());
            }
        }
        if (calData.size() > 0) {
            Map<Long, Integer> treeMap = new TreeMap<Long, Integer>(calData);
            for (Map.Entry<Long, Integer> entry : treeMap.entrySet()) {
                cal_X_Data.add(entry.getKey().toString());
                cal_Y_Data.add(entry.getValue().toString());
            }
        }
        if (sleepData.size() > 0) {
            Map<Long, Integer> treeMap = new TreeMap<Long, Integer>(sleepData);
            for (Map.Entry<Long, Integer> entry : treeMap.entrySet()) {
                sleep_X_Data.add(entry.getKey().toString());
                sleep_Y_Data.add(entry.getValue().toString());
            }
        }
    }
    public void onShowWeightStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        if (weightUnit.equals("kg")) {
            if (weightKG_X_Data.size() == 0 || weightKG_Y_Data.size() == 0) {
                Toast.makeText(this, "No data for show, please input data from personal info section first.", Toast.LENGTH_LONG).show();
                return;
            } else if (weightKG_X_Data.size() == 1 || weightKG_Y_Data.size() == 1) {
                Toast.makeText(this, "please provide more than one data set from personal info section for showing beautiful plots.", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, weightKG_X_Data);
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, weightKG_Y_Data);
            intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, weightKG_X_Data.size());
            intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, weightKG_Y_Data.size());
            intent.putExtra(DetailActivity.PLOT_TYPE, "Weight");
            intent.putExtra(DetailActivity.PLOT_UNIT, "KG");
        } else {
            if (weightLB_X_Data.size() == 0 || weightLB_Y_Data.size() == 0) {
                Toast.makeText(this, "No data for show, please input data from personal info section first.", Toast.LENGTH_LONG).show();
                return;
            } else if (weightLB_X_Data.size() == 1 || weightLB_Y_Data.size() == 1) {
                Toast.makeText(this, "Please provide more than one data record from personal info section for showing beautiful plots.", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, weightLB_X_Data);
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, weightLB_Y_Data);
            intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, weightLB_X_Data.size());
            intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, weightLB_Y_Data.size());
            intent.putExtra(DetailActivity.PLOT_TYPE, "Weight");
            intent.putExtra(DetailActivity.PLOT_UNIT, "LB");
        }
        startActivity(intent);
    }

    public void onShowHeightStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        if (heightUnit.equals("cm")) {
            if (heightCM_X_Data.size() == 0 || heightCM_Y_Data.size() == 0) {
                Toast.makeText(this, "No data for show, please input data from personal info section first.", Toast.LENGTH_LONG).show();
                return;
            } else if (heightCM_X_Data.size() == 1 || heightCM_Y_Data.size() == 1) {
                Toast.makeText(this, "Please provide more than one data record from personal info section for showing beautiful plots.", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, heightCM_X_Data);
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, heightCM_Y_Data);
            intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, heightCM_X_Data.size());
            intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, heightCM_Y_Data.size());
            intent.putExtra(DetailActivity.PLOT_TYPE, "Height");
            intent.putExtra(DetailActivity.PLOT_UNIT, "CM");
        } else {
            if (heightIN_X_Data.size() == 0 || heightIN_Y_Data.size() == 0) {
                Toast.makeText(this, "No data for show, please input data from personal info section first.", Toast.LENGTH_LONG).show();
                return;
            } else if (heightIN_X_Data.size() == 1 || heightIN_Y_Data.size() == 1) {
                Toast.makeText(this, "Please provide more than one data record from personal info section for showing beautiful plots.", Toast.LENGTH_LONG).show();
                return;
            }
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, heightIN_X_Data);
            intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, heightIN_Y_Data);
            intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, heightIN_X_Data.size());
            intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, heightIN_Y_Data.size());
            intent.putExtra(DetailActivity.PLOT_TYPE, "Height");
            intent.putExtra(DetailActivity.PLOT_UNIT, "IN");
        }
        startActivity(intent);
    }

    public void onShowCalStatclick(View view) {
        if (cal_X_Data.size() == 0 || cal_Y_Data.size() == 0) {
            Toast.makeText(this, "No data for show, please input data from Calories section first.", Toast.LENGTH_LONG).show();
            return;
        } else if (cal_X_Data.size() == 1 || cal_Y_Data.size() == 1) {
            Toast.makeText(this, "Only one day calories data currently, wait for your second data data for showing beautiful plots.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, PlotActivity.class);
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, cal_X_Data);
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, cal_Y_Data);
        intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, cal_X_Data.size());
        intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, cal_Y_Data.size());
        intent.putExtra(DetailActivity.PLOT_TYPE, "Calories");
        intent.putExtra(DetailActivity.PLOT_UNIT, "Cals");

        startActivity(intent);
    }

    public void onShowSleepStatclick(View view) {
        if (sleep_X_Data.size() == 0 || sleep_Y_Data.size() == 0) {
            Toast.makeText(this, "No data for show, please input data from Sleep management section first.", Toast.LENGTH_LONG).show();
            return;
        } else if (sleep_X_Data.size() == 1 || sleep_Y_Data.size() == 1) {
            Toast.makeText(this, "Please provide more than one data record from Sleep management section for showing beautiful plots.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, PlotActivity.class);
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, sleep_X_Data);
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, sleep_Y_Data);
        intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, sleep_X_Data.size());
        intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, sleep_Y_Data.size());
        intent.putExtra(DetailActivity.PLOT_TYPE, "Sleep time");
        intent.putExtra(DetailActivity.PLOT_UNIT, "Time");

        startActivity(intent);
    }

    public void onShowBackStatclick(View view) {
        super.onBackPressed();
    }

    @Override
    public void onTaskComplete(Constants.DynamoDBManagerType ddbType,
                               ArrayList<UserProfile> localUP) {
        switch (ddbType) {
            case LIST_ITEMS:
                retrieveStatData(localUP);
                dialog.dismiss();
                break;
            default:
                break;
        }
    }
    private class DynamoDBManagerTask extends
            AsyncTask<UserProfile, Void, AWSDynamoDBManagerTaskResult> {
        @Override
        protected void onPreExecute() {
            mCallback = (TaskCompleted) Statistics.this;
        }

        protected AWSDynamoDBManagerTaskResult doInBackground(
                UserProfile... userPref) {

            String tableStatus = DynamoDBManager.getTestTableStatus();
            Constants.DynamoDBManagerType actiontype = userPref[0].getActionType();

            AWSDynamoDBManagerTaskResult result = new AWSDynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(actiontype);

            if (actiontype == Constants.DynamoDBManagerType.LIST_ITEMS ||
                    actiontype == Constants.DynamoDBManagerType.CHECK_USER_EXISTENT ||
                    actiontype == Constants.DynamoDBManagerType.USER_LOGIN) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    result.setItemList(DynamoDBManager.getItemList());
                }
            }

            return result;
        }

        protected void onPostExecute(AWSDynamoDBManagerTaskResult result) {
            if (result.getTaskType() == Constants.DynamoDBManagerType.LIST_ITEMS) {
                mCallback.onTaskComplete(result.getTaskType(), result.getItemList());
            }
        }
    }
}
