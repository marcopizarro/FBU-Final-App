package com.marcopizarro.podcast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.ParseUser;
import com.spotify.sdk.android.auth.AuthorizationClient;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;

import retrofit.client.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    final FragmentManager fragmentManager = getSupportFragmentManager();

    public static ParseUser userParse;
    public static UserPrivate userSpotify;
    public static String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        userParse = ParseUser.getCurrentUser();

        Intent i = getIntent();
        authToken = i.getStringExtra(AUTH_TOKEN);
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError error) {
            }

            @Override
            public void success(UserPrivate userPrivate, Response response) {
                userSpotify = userPrivate;
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.btnTimeline:
                        fragment = new TimelineFragment();
                        break;
                    case R.id.btnExplore:
                        fragment = new ExploreFragment();
                        break;
                    case R.id.btnCompare:
                        fragment = new CompareFragment();
                        break;
                    case R.id.btnProfile:
                    default:
                        fragment = new ProfileFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.btnTimeline);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
            default:
                ParseUser.logOut();
                AuthorizationClient.clearCookies(MainActivity.this);
                Intent i = new Intent(MainActivity.this, SpotifyLoginActivity.class);
                startActivity(i);
                finish();
                return true;
        }
    }

    public void signOut() {
        Log.i(TAG, "Logged Out");
    }

    public static String getAuthToken() {
        return authToken;
    }

    public static UserPrivate getUserSpotify() {
        return userSpotify;
    }

    public static ParseUser getUserParse() {
        return userParse;
    }
}
