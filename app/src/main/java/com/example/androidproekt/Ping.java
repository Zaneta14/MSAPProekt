package com.example.androidproekt;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Ping {
    public static void doPing(JSONArray jsonArray) throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String host = jsonObject.getString("host");
            int count = jsonObject.getInt("count");
            int packetSize = jsonObject.getInt("packetSize");
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
    }
}
