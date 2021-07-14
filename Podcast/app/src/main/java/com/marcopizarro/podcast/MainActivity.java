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
import kaaes.spotify.webapi.android.models.AlbumSimple;
import kaaes.spotify.webapi.android.models.AlbumsPager;
import kaaes.spotify.webapi.android.models.Show;
import kaaes.spotify.webapi.android.models.ShowSimple;
import kaaes.spotify.webapi.android.models.ShowsPager;
import okhttp3.Call;
import okhttp3.OkHttpClient;
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

        SpotifyApi api = new SpotifyApi();

        // Most (but not all) of the Spotify Web API endpoints require authorisation.
        // If you know you'll only use the ones that don't require authorisation you can skip this step
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();

//        spotify.getShow("61CK08LG8FRIzfiPBl8Oq2", new SpotifyCallback<ShowSimple>() {
//            @Override
//            public void failure(SpotifyError error) {
//                Log.i(TAG, "error fetching", error);
//            }
//
//            @Override
//            public void success(ShowSimple showSimple, Response response) {
//                Log.i(TAG, "succeess");
//                Log.i(TAG, showSimple.name);
//                Log.i(TAG, showSimple.publisher);
//
//                tvMain.setText(showSimple.name);
//                tvArtist.setText(showSimple.publisher);
//                Glide.with(MainActivity.this)
//                        .load(showSimple.images.get(0).url)
//                        .into(ivAlbum);
//            }
//        });

        spotify.searchShows("talking points", new SpotifyCallback<ShowsPager>() {
            @Override
            public void failure(SpotifyError error) {

            }

            @Override
            public void success(ShowsPager showsPager, Response response) {
                Show show =  showsPager.shows.items.get(1);
                tvMain.setText(show.name);
                tvArtist.setText(show.publisher);
                Glide.with(MainActivity.this)
                        .load(show.images.get(0).url)
                        .into(ivAlbum);
            }
        });


    }
}