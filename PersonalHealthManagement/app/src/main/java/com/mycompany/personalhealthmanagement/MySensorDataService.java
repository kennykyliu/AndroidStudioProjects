package com.mycompany.personalhealthmanagement;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MySensorDataService extends Service {
    private SensorManager sensorMgr = null;
    private Sensor orSensor = null;

    public MySensorDataService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
        // register for orientation sensor events:
/*        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
                orSensor = sensor;
            }
        }

        // if we can't access the orientation sensor then exit:
        if (orSensor == null) {
            System.out.println("Failed to attach to orSensor.");
            cleanup();
        }*/

        //sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);
    }
    private void cleanup() {
        // aunregister with the orientation sensor before exiting:
        //sensorMgr.unregisterListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Toast.makeText(getApplicationContext(), "unbind", Toast.LENGTH_SHORT).show();
        // If the alarm has been set, cancel it.
        return super.onUnbind(intent);
    }

    public class MyLocalBinder extends Binder {
        MySensorDataService getService() {
            return MySensorDataService.this;
        }
    }
}
