package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PersonalInfo extends ActionBarActivity implements TaskCompleted {
    private static final String TAG = "PHM-PersonalInfo";

    private static Spinner genderSpinner;
    private ArrayAdapter<CharSequence> genderAdapter;
    private static String gender = null;
    private String weightUnit = null;
    private String heightUnit = null;
    private static int age = 0;
    private static int weightKG = 0;
    private static int weightLB = 0;
    private static int heightCM = 0;
    private static int heightFT = 0;
    private static int heightIN = 0;
    private static long date = 0;
    private EditText ageEditText;
    private EditText weightKGEditText;
    private EditText weightLBEditText;
    private EditText heightCMEditText;
    private EditText heightFTEditText;
    private EditText heightINEditText;
    private TextView heightFTtextView;
    private static TextView dateTextView;
    private TextView weightUnitTextView;
    private TextView heightUnitTextView;
    private TaskCompleted mCallback;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        genderSpinner = (Spinner)findViewById(R.id.genderSpinner);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        weightKGEditText = (EditText) findViewById(R.id.weightKGEditText);
        weightLBEditText = (EditText) findViewById(R.id.weightLBEditText);
        heightFTEditText = (EditText) findViewById(R.id.heightFTEditText);
        heightINEditText = (EditText) findViewById(R.id.heightINEditText);
        heightCMEditText = (EditText) findViewById(R.id.heightCMEditText);
        heightFTtextView = (TextView) findViewById(R.id.heightFTtextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        weightUnitTextView = (TextView) findViewById(R.id.weightUnitTextView);
        heightUnitTextView = (TextView) findViewById(R.id.heightUnitTextView);

        genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_unit,
                                            android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        weightUnit = getString(R.string.weight_unit_kg);
        heightUnit = getString(R.string.height_unit_cm);
        weightUnitTextView.setText(weightUnit);
        heightUnitTextView.setText(heightUnit);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dialog = ProgressDialog.show(PersonalInfo.this, "Data Syncing...",
                "Please wait");
        UserProfile userPref = new UserProfile(Constants.DynamoDBManagerType.LIST_ITEMS);
        new DynamoDBManagerTask().execute(userPref);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_personal_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.Weight_in_kg:
                weightUnit = getString(R.string.weight_unit_kg);
                break;
            case R.id.Weight_in_lb:
                weightUnit = getString(R.string.weight_unit_lb);
                break;
            case R.id.Height_in_cm:
                heightUnit = getString(R.string.height_unit_cm);
                break;
            case R.id.Height_in_ft_in:
                heightUnit = getString(R.string.height_unit_in);
                break;
            default:
                break;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        changeViewByUnit();

        return super.onOptionsItemSelected(item);
    }

    public void changeViewByUnit() {
        if (weightUnit.equals(getString(R.string.weight_unit_kg))) {
            weightKGEditText.setVisibility(View.VISIBLE);
            weightLBEditText.setVisibility(View.INVISIBLE);
        } else {
            weightKGEditText.setVisibility(View.INVISIBLE);
            weightLBEditText.setVisibility(View.VISIBLE);
        }
        weightUnitTextView.setText(weightUnit);

        if (heightUnit.equals(getString(R.string.height_unit_cm))) {
            heightFTEditText.setVisibility(View.INVISIBLE);
            heightINEditText.setVisibility(View.INVISIBLE);
            heightFTtextView.setVisibility(View.INVISIBLE);
            heightCMEditText.setVisibility(View.VISIBLE);
        } else {
            heightFTEditText.setVisibility(View.VISIBLE);
            heightINEditText.setVisibility(View.VISIBLE);
            heightFTtextView.setVisibility(View.VISIBLE);
            heightCMEditText.setVisibility(View.INVISIBLE);
        }
        heightUnitTextView.setText(heightUnit);
    }

    public void onSavePersonalInfoClick(View view) {
        if (date == 0) {
            Calendar cal = Calendar.getInstance();
            date = cal.getTimeInMillis();
        }
        if (ageEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input \"Age\" data.", Toast.LENGTH_LONG).show();
            return;
        }
        if (weightKGEditText.getText().toString().isEmpty() &&
                weightLBEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input \"Weight\" data.", Toast.LENGTH_LONG).show();
            return;
        }
        if (heightCMEditText.getText().toString().isEmpty() &&
                heightINEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please input \"Height\" data.", Toast.LENGTH_LONG).show();
            return;
        }

        long itemIndex = date / 1000000 * 1000000 + Constants.currUserID * 1000;
        dialog = ProgressDialog.show(PersonalInfo.this, "Data Syncing...",
                "Please wait");
        UserProfile userPref0 = new UserProfile(itemIndex++, Constants.currUserName, "gender",
                date, 0, gender, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
        new DynamoDBManagerTask().execute(userPref0);

        UserProfile userPref1 = new UserProfile(itemIndex++, Constants.currUserName, "age",
                date, Integer.parseInt(ageEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
        new DynamoDBManagerTask().execute(userPref1);

        UserProfile userPref2 = new UserProfile(itemIndex++, Constants.currUserName, "weight_unit",
                date, 0, weightUnit, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
        new DynamoDBManagerTask().execute(userPref2);

        UserProfile userPref3 = new UserProfile(itemIndex++, Constants.currUserName, "height_unit",
                date, 0, heightUnit, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
        new DynamoDBManagerTask().execute(userPref3);

        if (weightUnit.equals("kg")) {
            if (!weightKGEditText.getText().toString().isEmpty()) {
                UserProfile userPref4 = new UserProfile(itemIndex++, Constants.currUserName, "weight_kg",
                        date, Integer.parseInt(weightKGEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
                new DynamoDBManagerTask().execute(userPref4);
            } else {
                Toast.makeText(this, "Please input \"Weight\" data in KG.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (!weightLBEditText.getText().toString().isEmpty()) {
                UserProfile userPref4 = new UserProfile(itemIndex++, Constants.currUserName, "weight_lb",
                        date, Integer.parseInt(weightLBEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
                new DynamoDBManagerTask().execute(userPref4);
            } else {
                Toast.makeText(this, "Please input \"Weight\" data in LB.", Toast.LENGTH_LONG).show();
            }
        }
        if (heightUnit.equals("cm")) {
            if (!heightCMEditText.getText().toString().isEmpty()) {
                UserProfile userPref5 = new UserProfile(itemIndex++, Constants.currUserName, "height_cm",
                        date, Integer.parseInt(heightCMEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT);
                new DynamoDBManagerTask().execute(userPref5);
            } else {
                Toast.makeText(this, "Please input \"Height\" data in CM.", Toast.LENGTH_LONG).show();
            }
        } else {
            if (!heightFTEditText.getText().toString().isEmpty() && ! heightINEditText.getText().toString().isEmpty()) {
                int hight_in_exp = Integer.parseInt(heightFTEditText.getText().toString()) * 100 +
                        Integer.parseInt(heightINEditText.getText().toString());
                UserProfile userPref6 = new UserProfile(itemIndex++, Constants.currUserName, "height_in",
                        date, hight_in_exp, null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT);
                new DynamoDBManagerTask().execute(userPref6);
            } else {
                Toast.makeText(this, "Please input \"Height\" data in ft and in.", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        }

        /* Calculate BMI */
        double heightSquare = (double) heightCM * heightCM;
        double weightInDouble = (double) weightKG;
        if (heightSquare > 0) {
            DetailActivity.bmi = weightInDouble / (heightSquare/10000);
            DetailActivity.bmiUpdate = date;
        }

        return;
    }

    public void retrievePersonalInfo(
            ArrayList<UserProfile> localUP) {
        for (UserProfile res : localUP) {
            if ((res.getIndexNo() / 1000) % 1000 == Constants.currUserID) {
                String key = res.getItemName();
                if (key == null) {
                    continue;
                }
                /* Get latest data to show on screen */
                if (key.equals("gender")) {
                    if (res.getDate() > date) {
                        date = res.getDate();
                        gender = res.getSValue();
                    } else {
                        continue;
                    }
                }

                if (key.equals("age")) {
                    age = res.getNValue();
                } else if (key.equals("weight_kg")) {
                    weightKG = res.getNValue();
                } else if (key.equals("weight_lb")) {
                    weightLB = res.getNValue();
                } else if (key.equals("height_cm")) {
                    heightCM = res.getNValue();
                } else if (key.equals("height_ft")) {
                    heightFT = res.getNValue();
                } else if (key.equals("height_in")) {
                    heightIN = res.getNValue();
                } else if (key.equals("weight_unit")) {
                    weightUnit = res.getSValue();
                } else if (key.equals("height_unit")) {
                    heightUnit = res.getSValue();
                }
            }
        }
        /* Set up data */
        if ("Male".equals(gender)) {
            genderSpinner.setSelection(0);
        } else if ("Female".equals(gender)) {
            genderSpinner.setSelection(1);
        }

        if (age > 0) {
            ageEditText.setText(Integer.toString(age));
        }

        if (weightKG > 0) {
            weightKGEditText.setText(Integer.toString(weightKG));
        }

        if (weightLB > 0) {
            weightLBEditText.setText(Integer.toString(weightLB));
        }

        if (heightCM > 0) {
            heightCMEditText.setText(Integer.toString(heightCM));
        }

        if (heightFT > 0) {
            heightFTEditText.setText(Integer.toString(heightFT));
        }

        if (heightIN > 0) {
            heightINEditText.setText(Integer.toString(heightIN));
        }

        if (date > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date d = new Date(date);
            dateTextView.setText(dateFormat.format(d));
        } else {
            Calendar cal = Calendar.getInstance();
            date = cal.getTimeInMillis();
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date d = new Date(date);
            dateTextView.setText(dateFormat.format(d));
        }
        /* Set up unit */
        changeViewByUnit();

        /* Calculate BMI */
        double heightSquare = (double) heightCM * heightCM;
        double weightInDouble = (double) weightKG;
        if (heightSquare > 0) {
            DetailActivity.bmi = weightInDouble / (heightSquare/10000);
            DetailActivity.bmiUpdate = date;
        }
        return;
    }

    public void onPersonalInfoBackClick(View view) {
        super.onBackPressed();
    }

    public void onSetDateClick(View view) {
        PickDateDialogs pickDateDialogs = new PickDateDialogs();
        pickDateDialogs.show(getSupportFragmentManager(), "Please choose date.");
    }

    public static void showDate(long dateInMilli) {
        date = dateInMilli;
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date d = new Date(date);
        dateTextView.setText(dateFormat.format(d));
    }

    @Override
    public void onTaskComplete(Constants.DynamoDBManagerType ddbType,
                               ArrayList<UserProfile> localUP) {
        switch (ddbType) {
            case SYNC_PERSONAL_INFO_HEIGHT:
                dialog.dismiss();
                break;
            case LIST_ITEMS:
                retrievePersonalInfo(localUP);
                dialog.dismiss();
            default:
                break;
        }
    }

    private class DynamoDBManagerTask extends
            AsyncTask<UserProfile, Void, AWSDynamoDBManagerTaskResult> {
        @Override
        protected void onPreExecute() {
            mCallback = (TaskCompleted) PersonalInfo.this;
        }

        protected AWSDynamoDBManagerTaskResult doInBackground(
                UserProfile... userPref) {

            String tableStatus = DynamoDBManager.getTestTableStatus();
            Constants.DynamoDBManagerType actiontype = userPref[0].getActionType();

            AWSDynamoDBManagerTaskResult result = new AWSDynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(actiontype);

            if (actiontype== Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO ||
                    actiontype== Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT) {
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
            if (result.getTaskType() == Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO ||
                    result.getTaskType() == Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT ||
                    result.getTaskType() == Constants.DynamoDBManagerType.LIST_ITEMS) {
                mCallback.onTaskComplete(result.getTaskType(), result.getItemList());
            }
        }
    }

    public static void cleanUpData() {
        age = 0;
        weightKG = 0;
        weightLB = 0;
        heightCM = 0;
        heightFT = 0;
        heightIN = 0;
        date = 0;
    }
}
