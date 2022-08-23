package com.android.backgroundservice;
import android.content.Context;
import android.os.Handler;
import java.util.TimerTask;

public class CustomTimes extends TimerTask {
    private Context context;
    private Handler mHandler = new Handler();
    private  Task task = new Task();

    public CustomTimes(Context con){
        this.context = con;
    }

    @Override
    public void run() {
        new Thread(new Runnable() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                       task.sendJsonPostRequest(context);
                    }
                });
            }
        }).start();
    }
}
