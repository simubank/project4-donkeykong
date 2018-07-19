package com.levelup.td.tdlevelupquest.Utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mikeb on 7/10/2018.
 */

public class NetworkHelper {
    private static NetworkHelper obj;

    private NetworkHelper(){}

    public static NetworkHelper getInstance(){
        if(obj == null){
            obj = new NetworkHelper();
        }
        return obj;
    }

    public void botAPIGetRequest(String url, Context context, final APICallback callback){
        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                callback.onResponse(true,response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onResponse(false,null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", BotsApiKeys.BOTAPITOKEN);
                return headers;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(req);
    }

    public void postRequest(String url,APICallback callback){

    }
}
