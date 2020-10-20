package com.webclient.test.Interfaces;

import org.json.JSONObject;

public interface WeatherCallback {
    void onResponse(JSONObject obj);
    void onError();
}
