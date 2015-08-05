package com.mycompany.personalhealthmanagement;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;


public class PersonalInfo extends ActionBarActivity {
    private static final String TAG = "PHM-PersonalInfo";
    private static final String KEY_DATASET_NAME = "dataset_name";

    private Spinner genderSpinner;
    private Spinner weightSpinner;
    private Spinner heightSpinner;
    private ArrayAdapter<CharSequence> genderAdapter;
    private ArrayAdapter<CharSequence> weightAdapter;
    private ArrayAdapter<CharSequence> heightAdapter;
    private String gender = null;
    private String weightUnit = null;
    private String heightUnit = null;
    private EditText ageEditText;
    private EditText weightKGEditText;
    private EditText weightLBEditText;
    private EditText heightCMEditText;
    private EditText heightFTEditText;
    private EditText heightINEditText;
    private TextView heightFTtextView;
    private AWSDataHandler awsDataHandler;
    private Dataset personalDataset;
    private String datasetName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Bundle bundle = getIntent().getExtras();
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

        awsDataHandler = AWSDataHandler.getInstance();
        datasetName = bundle.getString(KEY_DATASET_NAME);
        personalDataset = awsDataHandler.getDataSet(datasetName);
        getPersonalInfo();
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
        personalDataset.put("gender", gender);
        personalDataset.put("age", ageEditText.getText().toString());
        personalDataset.put("weight_unit", weightUnit);
        personalDataset.put("height_unit", heightUnit);
        if (weightUnit.equals("kg")) {
            personalDataset.put("weight_kg", weightKGEditText.getText().toString());
        } else {
            personalDataset.put("weight_lb", weightLBEditText.getText().toString());
        }
        if (heightUnit.equals("cm")) {
            personalDataset.put("height_cm", heightCMEditText.getText().toString());
        } else {
            personalDataset.put("height_ft", heightFTEditText.getText().toString());
            personalDataset.put("height_in", heightINEditText.getText().toString());
        }
        awsDataHandler.refreshDatasetMetadata(PersonalInfo.this);

        return;
    }

    public void getPersonalInfo() {
        for (Record record : personalDataset.getAllRecords()) {
            String key = record.getKey();
            if (key.equals("gender")) {
                if (record.getValue().equals("Male")) {
                    genderSpinner.setSelection(0);
                } else {
                    genderSpinner.setSelection(1);
                }
            } else if (key.equals("age")) {
                ageEditText.setText(record.getValue());
            } else if (key.equals("weight_kg")) {
                weightKGEditText.setText(record.getValue());
            } else if (key.equals("weight_lb")) {
                weightLBEditText.setText(record.getValue());
            } else if (key.equals("height_cm")) {
                heightCMEditText.setText(record.getValue());
            } else if (key.equals("height_ft")) {
                heightFTEditText.setText(record.getValue());
            } else if (key.equals("height_in")) {
                heightINEditText.setText(record.getValue());
            }
        }

        return;
    }

    public void onPersonalInfoBackClick(View view) {
        super.onBackPressed();
    }
}
