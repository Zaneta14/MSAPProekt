package com.example.androidproekt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import java.util.Timer;
import java.util.TimerTask;

public class Service1 extends Service {

    public static final String RESTART_INTENT = "com.example.androidproekt.RESTART_INTENT";
    protected static final int NOTIFICATION_ID = 12345;
    private int counter = 0;

    public Service1() {
        super();
    }

    @Override
    public void onCreate() {
        Log.i("ZANETA", "Service onCreate");
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("ZANETA", "Service onStartCommand");
        super.onStartCommand(intent, flags, startId);
        SharedPreferences prefs= getSharedPreferences("com.example.androidproekt", MODE_PRIVATE);
        if (prefs.getInt("counter", 0)!=0) {
            counter=prefs.getInt("counter", 0);
        };
        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            Log.i("ZANETA", "Killed by android and restarted");
            StarterClass starterClass = new StarterClass();
            starterClass.launchService(this);
        }
        // make sure you call the startForeground on onStartCommand because otherwise when we hide the notification on onScreen it will nto restart in Android 6 and 7
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }
        startTimer();
        // return start sticky so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }

   @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * it starts the process in foreground. Normally this is done when screen goes off
     * THIS IS REQUIRED IN ANDROID 8 :
     * "The system allows apps to call Context.startForegroundService()
     * even while the app is in the background.
     * However, the app must call that service's startForeground() method within five seconds
     * after the service is created."
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i("ZANETA", "restarting foreground");
            try {
                NotificationManager mNotifyManager= (NotificationManager)
                        getSystemService(NOTIFICATION_SERVICE);
                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("Service notification")
                        .setContentText("This is the service's notification")
                        .setSmallIcon(R.drawable.ic_sleep);
                Notification myNotification = mNotifyBuilder.build();
                startForeground(NOTIFICATION_ID, myNotification);
                startTimer();
            } catch (Exception e) {
                Log.i("ZANETA", "Error in notification " + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i("ZANETA", "onDestroy called");
        super.onDestroy();
        try {
            SharedPreferences prefs= getSharedPreferences("com.example.androidproekt", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("counter", counter);
            editor.apply();
        } catch (NullPointerException e) {
            Log.e("ZANETA", e.getMessage());
        }
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    //this is called when the process is killed by Android
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i("ZANETA", "onTaskRemoved called");
        Intent broadcastIntent = new Intent(RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    //static to avoid multiple timers to be created when the service is called several times
    private static Timer timer;
    private static TimerTask timerTask;

    public void startTimer() {
        Log.i("ZANETA", "Starting timer");
        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        timer = new Timer();
        initializeTimerTask();
        Log.i("ZANETA", "Scheduling...");
        timer.schedule(timerTask, 5000, 5000); //
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If the network is active and the search field is not empty, start a FetchBook AsyncTask.
        if (networkInfo != null && networkInfo.isConnected()) {
            new AsyncTask1().execute();
        }
        //new AsyncTask1().execute();
    }

    public void initializeTimerTask() {
        Log.i("ZANETA", "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter+=5));
            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            Log.i("ZANETA", "Timer was not null and is stopped");
            timer.cancel();
            timer = null;
        }
    }
}