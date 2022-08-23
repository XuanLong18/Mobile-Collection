package com.android.backgroundservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    public static String TAG = MyService.class.getName();
    Timer timer = new Timer();
    TimerTask timerTask = new CustomTimes(MyService.this);
    private static Context context;

    public MyService() {
    }

    @Override
    public void onCreate() {
        // The service is being created
        super.onCreate();
        context = this;
        Log.d(TAG, "onCreate");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        long time_loop = 60*1000;
        timer.scheduleAtFixedRate(timerTask,100, time_loop);
        return START_STICKY;
    }
    public boolean stopService(Intent name){
        Toast.makeText(this.getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        return super.stopService(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(TAG, "onBind");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(TAG, "onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        timer.cancel();
    }

}
