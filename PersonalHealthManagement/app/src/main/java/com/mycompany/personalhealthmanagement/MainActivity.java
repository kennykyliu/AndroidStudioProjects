package com.mycompany.personalhealthmanagement;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.services.s3.AmazonS3Client;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

public class MainActivity extends ActionBarActivity implements TaskCompleted {

    private static final String TAG = "PHM-Login";

    private EditText usernameInput;
    private EditText pwdInput;
    private static Button signUpButton;
    private static Button signInButton;
    private ImageView titleImage;
    private String username = null;
    private TaskCompleted mCallback;
    private boolean bucketExists = false;
    private boolean bucketChecked = false;
    private File mFile;
    String pwd = null;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        pwdInput = (EditText) findViewById(R.id.pwdInput);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        signInButton = (Button) findViewById(R.id.signInButton);
        titleImage = (ImageView) findViewById(R.id.titleImage);
        titleImage.setImageResource(R.drawable.health_guy);

        DynamoDBManager.init(MainActivity.this);
        new CheckS3BucketExists().execute();
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
        username = usernameInput.getText().toString();
        pwd = pwdInput.getText().toString();
        // Check username input
        if (username.isEmpty()) {
            Toast.makeText(this, "Empty username!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        // Check password input
        if (pwd.isEmpty()) {
            Toast.makeText(this, "Empty password!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        dialog = ProgressDialog.show(MainActivity.this, "Data Syncing...",
                "Please wait");

        UserProfile userPref = new UserProfile(Constants.DynamoDBManagerType.CHECK_USER_EXISTENT);
        new DynamoDBManagerTask().execute(userPref);
    }

    public void signIn(View view) {
        username = usernameInput.getText().toString();
        pwd = pwdInput.getText().toString();
        // Check username input
        if (username.isEmpty()) {
            Toast.makeText(this, "Empty username!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        // Check password input
        if (pwd.isEmpty()) {
            Toast.makeText(this, "Empty password!\nPlease try again.", Toast.LENGTH_LONG).show();
            usernameInput.getText().clear();
            pwdInput.getText().clear();
            return;
        }

        UserProfile userPref = new UserProfile(Constants.DynamoDBManagerType.USER_LOGIN);
        new DynamoDBManagerTask().execute(userPref);
    }

    @Override
    public void onTaskComplete(Constants.DynamoDBManagerType ddbType,
                               ArrayList<UserProfile> localUP) {
        int currUserCount = 0;

        switch (ddbType) {
            case CHECK_USER_EXISTENT:
                for (UserProfile res : localUP) {
                    /* Index 1 ~ 999 stores account info for all users */
                    if (res.getIndexNo() > 0 && res.getIndexNo() <= Constants.MAX_SIGNUP_USER
                            && username.equals(res.getUserName())) {
                        Toast.makeText(this, "User [" + username + "] is already exist. " +
                                "Please try again", Toast.LENGTH_LONG).show();
                        return;
                    }
                    /* Index 0 stores current user numbers */
                    if (res.getIndexNo() == 0) {
                        currUserCount = res.getNValue();
                    }
                }

                // Index 10stores current user numbers
                UserProfile userPref0 = new UserProfile(0, null, null, 0, currUserCount + 1, null, Constants.DynamoDBManagerType.INSERT_ITEM);
                new DynamoDBManagerTask().execute(userPref0);
                /* Index 1 ~ 999 stores account info for all users */
                UserProfile userPref1 = new UserProfile(currUserCount + 1,
                        username, null, 0, (currUserCount + 1) * 1000, pwd, Constants.DynamoDBManagerType.INSERT_ITEM);
                new DynamoDBManagerTask().execute(userPref1);
                Toast.makeText(this, "New account [" + username + "] is created successfully!!! ",
                                                Toast.LENGTH_LONG).show();
                dialog.dismiss();
                usernameInput.getText().clear();
                pwdInput.getText().clear();
                break;
            case USER_LOGIN:
                for (UserProfile res : localUP) {
                    if (res.getIndexNo() > 0 && res.getIndexNo() <= Constants.MAX_SIGNUP_USER &&
                            username.equals(res.getUserName())) {
                        if (pwd.equals(res.getSValue())) {
                            Constants.currUserName = username;
                            Constants.currUserID = res.getIndexNo();
                            Intent intent = new Intent(this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(this, "Wrong password.\nPlease try again.", Toast.LENGTH_LONG).show();
                        }
                        return;
                    }
                }
                Toast.makeText(this, "Invalid user!!!\n\nPlease create an account first.", Toast.LENGTH_LONG).show();
                pwdInput.getText().clear();
                break;
            case INSERT_ITEM:
                break;
            case LIST_ITEMS:
                break;
            default:
                break;
        }
    }

    public static void refreshDataFail() {
        signUpButton.setEnabled(false);
        signInButton.setEnabled(false);
        return;
    }

    private class DynamoDBManagerTask extends
            AsyncTask<UserProfile, Void, AWSDynamoDBManagerTaskResult> {
        @Override
        protected void onPreExecute() {
            mCallback = (TaskCompleted) MainActivity.this;

        }

        protected AWSDynamoDBManagerTaskResult doInBackground(
                UserProfile... userPref) {

            String tableStatus = DynamoDBManager.getTestTableStatus();
            Constants.DynamoDBManagerType actiontype = userPref[0].getActionType();

            AWSDynamoDBManagerTaskResult result = new AWSDynamoDBManagerTaskResult();
            result.setTableStatus(tableStatus);
            result.setTaskType(actiontype);

            if (actiontype == Constants.DynamoDBManagerType.CREATE_TABLE) {
                if (tableStatus.length() == 0) {
                    DynamoDBManager.createTable();
                }
            } else if (actiontype == Constants.DynamoDBManagerType.INSERT_ITEM) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.insertItem(userPref[0]);
                }
            } else if (actiontype == Constants.DynamoDBManagerType.LIST_ITEMS ||
                        actiontype == Constants.DynamoDBManagerType.CHECK_USER_EXISTENT ||
                        actiontype == Constants.DynamoDBManagerType.USER_LOGIN) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    result.setItemList(DynamoDBManager.getItemList());
                }
            } else if (actiontype == Constants.DynamoDBManagerType.GET_ITEM) {
                if (tableStatus.equalsIgnoreCase("ACTIVE")) {
                    DynamoDBManager.getItemList();
                }
            }

            return result;
        }

        protected void onPostExecute(AWSDynamoDBManagerTaskResult result) {
            if (result.getTaskType() == Constants.DynamoDBManagerType.LIST_ITEMS ||
                result.getTaskType() == Constants.DynamoDBManagerType.CHECK_USER_EXISTENT ||
                    result.getTaskType() == Constants.DynamoDBManagerType.USER_LOGIN) {
                mCallback.onTaskComplete(result.getTaskType(), result.getItemList());
            }
        }
    }

    private class CheckS3BucketExists extends AsyncTask<Object, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            AmazonS3Client sS3Client = Util.getS3Client(getApplicationContext());
            return Util.doesBucketExist();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            bucketChecked = true;
            bucketExists = result;
            Log.i(TAG, "Check bucket exist result = " + bucketExists);
        }
    }

    private void addUserIntoFile(String username, String pwd) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(Constants.USER_LIST_FILE_NAME)));
            try {
                pw.println(username + "," + pwd);
            } finally {
                pw.close();
            }

            mFile = new File(Constants.USER_LIST_FILE_NAME);
            FileOutputStream is = new FileOutputStream(mFile);
            OutputStreamWriter osw = new OutputStreamWriter(is);
            Writer w = new BufferedWriter(osw);
            w.write(username + "," + pwd);
            w.close();
        } catch (IOException e) {
            Log.e(TAG, "", e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
