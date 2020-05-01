package com.example.androidproekt;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class PingAsyncTask extends AsyncTask<Void, Void, Void> {
    public PingAsyncTask() {
    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            Log.i("ZANETA", "doInBackground");

            String data = NetworkUtils.getPingInfo();
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            int jobPeriod = jsonObject.getInt("jobPeriod");
            int s=(int)(600/jobPeriod)+1;
            Log.i("ZANETA", "ke vrti "+s+" pati");
            for (int i=0; i<=(int)(600/jobPeriod); i++) { //kolku pati da vrti vo ramkite na 10 minuti (600s)
                Ping.doPing(jsonArray);
                try {
                    Thread.sleep(jobPeriod*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}