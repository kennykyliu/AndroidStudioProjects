package com.mycompany.boundservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.widget.TextView;
import com.mycompany.boundservice.MyService.MyLocalBinder;


public class MainActivity extends ActionBarActivity {

    MyService buckysService;
    boolean isBound = false;
    Messenger mService = null;

    public void showTime(View view) {
        //String currentTime =  buckysService.getCurrentTime();
        //TextView buckysText = (TextView) findViewById(R.id.buckysText);
        //buckysText.setText(currentTime);
        if (!isBound)
            return;
        // Create and send a message to the service, using a supported 'what' value
        Message msg = Message.obtain(null, MyService.MSG_SAY_HELLO, 0, 0);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this, MyService.class);
        bindService(i, buckysConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection buckysConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //MyLocalBinder binder = (MyLocalBinder) service;
            //buckysService = binder.getService();
            mService = new Messenger(service);
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            isBound = false;
        }
    };
}
