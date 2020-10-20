package com.webclient.test.Services;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.webclient.test.Interfaces.WeatherCallback;
import com.webclient.test.MainActivity;
import com.webclient.test.R;

import org.json.JSONObject;

public class VolleyService {
    private RequestQueue requestQueue;
    final private Context _context;
    public VolleyService(Context context){
        _context = context;
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap

        Network network = new BasicNetwork(new HurlStack());

        requestQueue = new RequestQueue(cache, network);

        requestQueue.start();
    }


    public void weatherRequestByCityName(String cityName, final WeatherCallback callback){
        String key = _context.getString(R.string.api_key);
        String url = _context.getString(R.string.requestByName).replace("{cityName}", cityName).replace("{appid}", key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            callback.onResponse(response);
                        }catch (Exception e){

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                            callback.onError();
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

}
