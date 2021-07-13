package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.codepath.asynchttpclient.callback.TextHttpResponseHandler;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = getIntent();

        authToken = i.getStringExtra(AUTH_TOKEN);


//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.put("ids", "0oSGxfWSnnOXhD2fKuz2Gy,3dBVyJ7JuOMt4GE9607Qin");
////        params.put("page", 0);
//        client.get("https://api.spotify.com/v1/artists", params, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Headers headers, JSON json) {
//                // Access a JSON array response with `json.jsonArray`
//                Log.d("DEBUG ARRAY", json.jsonArray.toString());
//                // Access a JSON object response with `json.jsonObject`
//                Log.d("DEBUG OBJECT", json.jsonObject.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
//
//            }
//        });

    }
}