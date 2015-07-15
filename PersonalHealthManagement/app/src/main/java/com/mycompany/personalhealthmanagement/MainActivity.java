package com.mycompany.personalhealthmanagement;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    EditText usernameInput;
    EditText pwdInput;
    ImageView titleImage;
    static int userCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameInput = (EditText) findViewById(R.id.usernameInput);
        pwdInput = (EditText) findViewById(R.id.pwdInput);
        titleImage = (ImageView) findViewById(R.id.titleImage);
        titleImage.setImageResource(R.drawable.health_guy);
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
        SharedPreferences sharedPerf = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPerf.edit();
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

        editor.putString("username" + userCount, usernameInput.getText().toString());
        editor.putString("password" + userCount, pwdInput.getText().toString());
        editor.apply();
        usernameInput.getText().clear();
        pwdInput.getText().clear();
        userCount++;
        Toast.makeText(this, "Saved: " + userCount, Toast.LENGTH_LONG).show();
    }

    public void signIn(View view) {
        SharedPreferences sharedPerf = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String username = usernameInput.getText().toString();
        String pwd = pwdInput.getText().toString();

        for (int i = 0; i < userCount; i++) {
            if (sharedPerf.getString("username" + i, "").equals(username)) {
                if (sharedPerf.getString("password" + i, "").equals(pwd)) {
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Wrong password.\nPlease try again.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
        Toast.makeText(this, "Invalid user!!!\n\nPlease create an account first.", Toast.LENGTH_LONG).show();
    }
}
