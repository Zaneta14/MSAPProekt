package com.example.androidproekt;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncTask1 extends AsyncTask<Void, Void, Void> {
    //argument na doinbackground
    //loading
    //return na doinbackground, argument na onpostexecute
    public AsyncTask1() {

    }
    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String data = NetworkUtils.getPingInfo();
            JSONArray jsonArray = new JSONArray(data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String host = jsonObject.getString("host");
                int count = jsonObject.getInt("count");
                int packetSize = jsonObject.getInt("packetSize");
                int jobPeriod = jsonObject.getInt("jobPeriod");
                String pingResult = "";
                try {
                    String pingCmd = "ping -s " + packetSize + " -c " + count + " " + host;
                    Runtime r = Runtime.getRuntime();
                    Process p = r.exec(pingCmd);
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(p.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        Log.d("PINGTEST", inputLine);
                        pingResult += inputLine;
                    }
                    in.close();
                    Log.d("PINGTEST", pingResult);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}