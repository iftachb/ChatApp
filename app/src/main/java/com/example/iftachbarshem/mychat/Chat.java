package com.example.iftachbarshem.mychat;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Chat {
    private static final String TAG = "Chat";
    private String msg;
    private String sender;
    private long timestamp;

    public Chat(String msg, String sender) {
        this.msg = msg;
        this.sender = sender;
        this.timestamp = System.currentTimeMillis();
    }

    public Chat(String jsonMsg) {
        try {
            JSONObject jsonObject = new JSONObject(jsonMsg);
            msg = jsonObject.getString("msg");
            sender = jsonObject.getString("sender");
        } catch (JSONException e) {
            Log.e(TAG, "fail to decode json ", e);
        }
        timestamp = System.currentTimeMillis();
    }

    public String getMsg() {
        return msg;
    }

    public String getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("msg", msg);
            jSONObject.put("sender", sender);
            jSONObject.put("timestamp", timestamp);
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(TAG, "fail to encode json ", e);
        }
        return null;
    }
}