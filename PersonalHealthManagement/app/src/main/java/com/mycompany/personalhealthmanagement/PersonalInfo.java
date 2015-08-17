package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class PersonalInfo extends FragmentActivity implements TaskCompleted {
    private static final String TAG = "PHM-PersonalInfo";

    private static Spinner genderSpinner;
    private Spinner weightSpinner;
    private Spinner heightSpinner;
    private ArrayAdapter<CharSequence> genderAdapter;
    private ArrayAdapter<CharSequence> weightAdapter;
    private ArrayAdapter<CharSequence> heightAdapter;
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
    private TaskCompleted mCallback;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        genderSpinner = (Spinner)findViewById(R.id.genderSpinner);
        weightSpinner = (Spinner)findViewById(R.id.weightSpinner);
        heightSpinner = (Spinner)findViewById(R.id.heightSpinner);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        weightKGEditText = (EditText) findViewById(R.id.weightKGEditText);
        weightLBEditText = (EditText) findViewById(R.id.weightLBEditText);
        heightFTEditText = (EditText) findViewById(R.id.heightFTEditText);
        heightINEditText = (EditText) findViewById(R.id.heightINEditText);
        heightCMEditText = (EditText) findViewById(R.id.heightCMEditText);
        heightFTtextView = (TextView) findViewById(R.id.heightFTtextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_unit,
                                            android.R.layout.simple_spinner_item);
        weightAdapter = ArrayAdapter.createFromResource(this, R.array.weight_unit,
                                            android.R.layout.simple_spinner_item);
        heightAdapter = ArrayAdapter.createFromResource(this, R.array.height_unit,
                                            android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        heightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        weightSpinner.setAdapter(weightAdapter);
        heightSpinner.setAdapter(heightAdapter);

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
        }

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        weightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weightUnit = parent.getItemAtPosition(position).toString();
                if (weightUnit.equals("kg")) {
                    weightKGEditText.setVisibility(view.VISIBLE);
                    weightLBEditText.setVisibility(view.INVISIBLE);
                } else {
                    weightKGEditText.setVisibility(view.INVISIBLE);
                    weightLBEditText.setVisibility(view.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        heightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                heightUnit = parent.getItemAtPosition(position).toString();
                if (heightUnit.equals("cm")) {
                    heightFTEditText.setVisibility(view.INVISIBLE);
                    heightINEditText.setVisibility(view.INVISIBLE);
                    heightFTtextView.setVisibility(view.INVISIBLE);
                    heightCMEditText.setVisibility(view.VISIBLE);
                } else {
                    heightFTEditText.setVisibility(view.VISIBLE);
                    heightINEditText.setVisibility(view.VISIBLE);
                    heightFTtextView.setVisibility(view.VISIBLE);
                    heightCMEditText.setVisibility(view.INVISIBLE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSavePersonalInfoClick(View view) {
        if (date == 0) {
            Calendar cal = Calendar.getInstance();
            date = cal.getTimeInMillis();
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
            UserProfile userPref4 = new UserProfile(itemIndex++, Constants.currUserName, "weight_kg",
                    date, Integer.parseInt(weightKGEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
            new DynamoDBManagerTask().execute(userPref4);
        } else {
            UserProfile userPref4 = new UserProfile(itemIndex++, Constants.currUserName, "weight_lb",
                    date, Integer.parseInt(weightLBEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO);
            new DynamoDBManagerTask().execute(userPref4);
        }
        if (heightUnit.equals("cm")) {
            UserProfile userPref5 = new UserProfile(itemIndex++, Constants.currUserName, "height_cm",
                    date, Integer.parseInt(heightCMEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT);
            new DynamoDBManagerTask().execute(userPref5);
        } else {
            UserProfile userPref5 = new UserProfile(itemIndex++, Constants.currUserName, "height_ft",
                    date, Integer.parseInt(heightFTEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT);
            new DynamoDBManagerTask().execute(userPref5);
            UserProfile userPref6 = new UserProfile(itemIndex++, Constants.currUserName, "height_in",
                    date, Integer.parseInt(heightINEditText.getText().toString()), null, Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT);
            new DynamoDBManagerTask().execute(userPref6);
        }

        return;
    }

    public static void retrievePersonalInfo(
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
                }
            }
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
            } else if (actiontype == Constants.DynamoDBManagerType.LIST_ITEMS ||
                    actiontype == Constants.DynamoDBManagerType.CHECK_USER_EXISTENT ||
                    actiontype == Constants.DynamoDBManagerType.USER_LOGIN) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    result.setItemList(DynamoDBManager.getItemList());
                }
            }

            return result;
        }

        protected void onPostExecute(AWSDynamoDBManagerTaskResult result) {
            if (result.getTaskType() == Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO ||
                    result.getTaskType() == Constants.DynamoDBManagerType.SYNC_PERSONAL_INFO_HEIGHT) {
                mCallback.onTaskComplete(result.getTaskType(), result.getItemList());
            }
        }
    }
}
