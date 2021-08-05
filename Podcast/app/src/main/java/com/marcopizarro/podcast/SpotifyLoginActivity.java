package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyLoginActivity";

    private static final int REQUEST_CODE = 1337;

    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String CLIENT_ID = "6182f637ef454837b3186cb1a1dfb860";
    public static final String REDIRECT_URL = "oauth://marcopizarro.com";

    Button btnSpotLogin;
    ImageView spotGreen;
    ImageView spotBlue;
    ImageView continueButton;

    String authToken;

    Button btnLogin;
    Button btnSignUp;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnSpotLogin = findViewById(R.id.btnSpotLogin);
        spotGreen = findViewById(R.id.spotGreen);
        spotBlue = findViewById(R.id.spotBlue);
        continueButton = findViewById(R.id.continueButton);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnSignIn);
        etPassword = findViewById(R.id.etPassword);
        etUsername = findViewById(R.id.etUsername);

        if (ParseUser.getCurrentUser() != null){
//            completeLoginUp();
            ParseUser.logOut();
        }

        btnSpotLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnSpotLogin) {
                    openLoginWindow();
                }
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpotifyLoginActivity.this, MainActivity.class);
                intent.putExtra(AUTH_TOKEN, authToken);
                startActivity(intent);
                finish();
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
                    Toast.makeText(SpotifyLoginActivity.this, "Invalid username/password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SpotifyLoginActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                    completeLoginUp();
                    checkBothCompleted();
                }
            }
        });
    }

    private void signUp(String username, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
//        user.put("spotifyId", spotifyId);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(SpotifyLoginActivity.this, "Logged In!", Toast.LENGTH_SHORT).show();
                    completeLoginUp();
                    checkBothCompleted();
                } else {
                    Log.i(TAG, "Error Signing Up", e);
                    Toast.makeText(SpotifyLoginActivity.this, "Error Signing Up", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openLoginWindow() {
        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URL);
        builder.setScopes(new String[]{"streaming"});
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                case TOKEN:
                    completeSpotifyLogin(response.getAccessToken());
                    checkBothCompleted();
                    break;
                case ERROR:
                    Log.e(TAG, "Auth error: " + response.getError());
                    break;
                default:
                    Log.d(TAG, "Auth result: " + response.getType());
            }
        }
    }

    private void checkBothCompleted() {
        if (ParseUser.getCurrentUser() != null && authToken != null){
            continueButton.setVisibility(View.VISIBLE);
        }
    }

    private void completeLoginUp(){
        spotBlue.setVisibility(View.VISIBLE);
        etUsername.setFocusable(false);
        etPassword.setFocusable(false);
        btnLogin.setClickable(false);
        btnSignUp.setClickable(false);
        btnSignUp.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        btnSignUp.setClickable(false);
        btnLogin.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        btnLogin.setClickable(false);
    }

    private void completeSpotifyLogin(String s){
        spotGreen.setVisibility(View.VISIBLE);
        btnSpotLogin.setBackgroundColor(getResources().getColor(R.color.dark_gray));
        btnSpotLogin.setClickable(false);
        authToken = s;
    }

    private void goMainActivity() {
        Intent intent = new Intent(SpotifyLoginActivity.this, MainActivity.class);
        intent.putExtra(AUTH_TOKEN, authToken);
        startActivity(intent);
        finish();
    }

    public void destroy() {
        SpotifyLoginActivity.this.finish();
    }

}