package com.mycompany.personalhealthmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


public class Statistics extends ActionBarActivity {
    private static ArrayList<String> weightKG_X_Data;
    private static ArrayList<String> weightKG_Y_Data;
    private static ArrayList<Long> weightLB_X_Data;
    private static ArrayList<Integer> weightLB_Y_Data;
    private static ArrayList<Long> heightCM_X_Data;
    private static ArrayList<Integer> heightCM_Y_Data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_statistics, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static void retrieveStatData(
            ArrayList<UserProfile> localUP) {
        HashMap<Long, Integer> weightKGData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> weightLBData = new HashMap<Long, Integer>();
        HashMap<Long, Integer> heightCMData = new HashMap<Long, Integer>();
        weightKG_X_Data = new ArrayList<String>();
        weightKG_Y_Data = new ArrayList<String>();
        weightLB_X_Data = new ArrayList<Long>();
        weightLB_Y_Data = new ArrayList<Integer>();
        heightCM_X_Data = new ArrayList<Long>();
        heightCM_Y_Data = new ArrayList<Integer>();


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
                }
            }
        }
        Map<Long, Integer> treeMap0 = new TreeMap<Long, Integer>(weightKGData);
        for (Map.Entry<Long, Integer> entry : treeMap0.entrySet()) {
            Log.i("Data weight KG =  ", entry.getKey() + ": " + entry.getValue());
            weightKG_X_Data.add(entry.getKey().toString());
            weightKG_Y_Data.add(entry.getValue().toString());

        }
        Map<Long, Integer> treeMap1 = new TreeMap<Long, Integer>(weightLBData);
        for (Map.Entry<Long, Integer> entry : treeMap1.entrySet()) {
            Log.i("Data weight LB =  ", entry.getKey() + ": " + entry.getValue());
            weightLB_X_Data.add(entry.getKey());
            weightLB_Y_Data.add(entry.getValue());
        }
        Map<Long, Integer> treeMap2 = new TreeMap<Long, Integer>(heightCMData);
        for (Map.Entry<Long, Integer> entry : treeMap2.entrySet()) {
            Log.i("Data height CM =  ", entry.getKey() + ": " + entry.getValue());
            heightCM_X_Data.add(entry.getKey());
            heightCM_Y_Data.add(entry.getValue());
        }
    }
    public void onShowWeightStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_X, weightKG_X_Data);
        //intent.putExtra(DetailActivity.PLOT_DATA_X, weightKG_X_Data.toArray());
        intent.putStringArrayListExtra(DetailActivity.PLOT_DATA_Y, weightKG_Y_Data);
        //intent.putExtra(DetailActivity.PLOT_DATA_Y, weightKG_Y_Data.toArray());
        intent.putExtra(DetailActivity.PLOT_DATA_X_SIZE, weightKG_X_Data.size());
        intent.putExtra(DetailActivity.PLOT_DATA_Y_SIZE, weightKG_Y_Data.size());
        startActivity(intent);
    }

    public void onShowHeightStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        startActivity(intent);
    }

    public void onShowCalStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        startActivity(intent);
    }

    public void onShowSleepStatclick(View view) {
        Intent intent = new Intent(this, PlotActivity.class);
        startActivity(intent);
    }

    public void onShowBackStatclick(View view) {
        super.onBackPressed();
    }
}
