package com.example.miniproject;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonForRequest {
    private static SingletonForRequest instance;
    private RequestQueue requestQueue;
    private static Context applicationContext;

    private SingletonForRequest(Context context) {
        applicationContext = context.getApplicationContext();
        requestQueue = getRequestQueue();
    }

    public static synchronized SingletonForRequest getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonForRequest(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(applicationContext);
        }
        return requestQueue;
    }
}
