package com.marcopizarro.podcast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.client.Response;

public class PodcastLoginActivity extends AppCompatActivity {
    public static final String TAG = "PodcastLoginActivity";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";

    String authToken;
    String spotifyId;

    Button btnLogin;
    Button btnSignUp;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_podcast_login);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

        Intent i = getIntent();
        authToken = i.getStringExtra(AUTH_TOKEN);
        Log.i(TAG, authToken);

        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(authToken);
        SpotifyService spotify = api.getService();

        spotify.getMe(new SpotifyCallback<UserPrivate>() {
            @Override
            public void failure(SpotifyError error) {

            }

            @Override
            public void success(UserPrivate userPrivate, Response response) {
                spotifyId = userPrivate.id;
            }
        });


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                login(username, password);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                signUp(username, password);
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.i(TAG, "Error Logging in", e);
                    Toast.makeText(PodcastLoginActivity.this, "Invalid username/password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PodcastLoginActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                    finish();
                }
            }
        });
    }

    private void signUp(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.put("spotifyId", spotifyId);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(PodcastLoginActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                    goMainActivity();
                    finish();
                } else {
                    Log.i(TAG, "Error Signing Up", e);
                    Toast.makeText(PodcastLoginActivity.this, "Error Signing Up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(PodcastLoginActivity.this, MainActivity.class);
        intent.putExtra(AUTH_TOKEN, authToken);
        startActivity(intent);
        finish();
    }
}