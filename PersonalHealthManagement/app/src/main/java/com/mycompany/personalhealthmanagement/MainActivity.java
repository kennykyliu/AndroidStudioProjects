package com.mycompany.personalhealthmanagement;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "PHM-Login";
    private static final String KEY_DATASET_NAME = "dataset_name";

    EditText usernameInput;
    EditText pwdInput;
    ImageView titleImage;
    private CognitoSyncManager client;
    private AWSDataHandler awsDataHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        pwdInput = (EditText) findViewById(R.id.pwdInput);
        titleImage = (ImageView) findViewById(R.id.titleImage);
        titleImage.setImageResource(R.drawable.health_guy);

        /**
         * Initializes the Amazon Cognito sync client.
         * This must be call before you can use it.
         */
        CognitoSyncClientManager.init(this);
        client = CognitoSyncClientManager.getInstance();
        awsDataHandler = AWSDataHandler.getInstance();
        awsDataHandler.refreshDatasetMetadata(MainActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void signUp(View view) {
        String username = usernameInput.getText().toString();
        String pwd = pwdInput.getText().toString();

        /* Check username input */
        if (username.isEmpty()) {
            Toast.makeText(this, "Empty username!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        /* Check password input */
        if (pwd.isEmpty()) {
            Toast.makeText(this, "Empty password!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        Dataset accountDataset = awsDataHandler.getDataSet("account");
        for (Record record : accountDataset.getAllRecords()) {
            if (record.getKey().equals(username)) {
                Toast.makeText(this, "User [" + username + "] is already exist. " +
                               "Please try again", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i(TAG, "   Key = " + record.getKey());
            Log.i(TAG, "   Value = " + record.getValue());
        }
        accountDataset.put(username, pwd);
        Dataset personalDataset = client.openOrCreateDataset("Dataset_" + username);
        personalDataset.put("username", username);
        awsDataHandler.refreshDatasetMetadata(MainActivity.this);
        usernameInput.getText().clear();
        pwdInput.getText().clear();
    }

    public void signIn(View view) {
        String username = usernameInput.getText().toString();
        String pwd = pwdInput.getText().toString();

        Dataset accountDataset = awsDataHandler.getDataSet("account");
        for (Record record : accountDataset.getAllRecords()) {
            if (record.getKey().equals(username)) {
                if (record.getValue().equals(pwd)) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    intent.putExtra(KEY_DATASET_NAME, "Dataset_" + username);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Wrong password.\nPlease try again.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        Toast.makeText(this, "Invalid user!!!\n\nPlease create an account first.", Toast.LENGTH_LONG).show();
        pwdInput.getText().clear();
    }
}
