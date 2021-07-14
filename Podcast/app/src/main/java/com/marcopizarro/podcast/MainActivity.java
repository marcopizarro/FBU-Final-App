package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Album;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    String authToken;

    private final OkHttpClient mOkHttpClient = new OkHttpClient();

    private Call mCall;
    TextView tvMain;
    TextView tvArtist;
    ImageView ivAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMain = findViewById(R.id.tvMain);
        tvArtist = findViewById(R.id.tvArtist);
        ivAlbum = findViewById(R.id.ivAlbum);

        Intent i = getIntent();
        authToken = i.getStringExtra(AUTH_TOKEN);
        Log.i(TAG, authToken);



//        final Request request = new Request.Builder()
//                .url("https://api.spotify.com/v1/me")
//                .addHeader("Authorization","Bearer " + authToken)
//                .build();
//
//        if (mCall != null) {
//            mCall.cancel();
//        }
//        mCall = mOkHttpClient.newCall(request);
//
//        mCall.enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.i(TAG, "errorr", e);
////                setResponse("Failed to fetch data: " + e);
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    final JSONObject jsonObject = new JSONObject(response.body().string());
//                    Log.i(TAG, response.message());
////                    setResponse(jsonObject.toString(3));
//                } catch (JSONException e) {
//                    Log.i(TAG, "error", e);
////                    setResponse("Failed to parse data: " + e);
//                }
//            }
//        });


        SpotifyApi api = new SpotifyApi();

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();



        spotify.getAlbum("2dIGnmEIy1WZIcZCFSj6i8", new SpotifyCallback<Album>() {
            @Override
            public void success(Album album, retrofit.client.Response response) {
                Log.i(TAG, album.name);
                Log.i(TAG, String.valueOf(album.images.get(0).url));
                tvMain.setText(album.name);
                tvArtist.setText(album.artists.get(0).name);
                Glide.with(MainActivity.this)
                        .load(album.images.get(0).url)
                        .into(ivAlbum);
            }

            @Override
            public void failure(SpotifyError spotifyError) {
                Log.i(TAG, "error fetching", spotifyError);
            }
        });

//        It is also possible to construct the adapter with custom parameters.

//        RestAdapter restAdapter = new RestAdapter.Builder()
//                .setEndpoint(SpotifyApi.SPOTIFY_WEB_API_ENDPOINT)
//                .setRequestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void intercept(RequestFacade request) {
//                        request.addHeader("Authorization", "Bearer " + authToken);
//                    }
//                })
//                .build();

    }
}