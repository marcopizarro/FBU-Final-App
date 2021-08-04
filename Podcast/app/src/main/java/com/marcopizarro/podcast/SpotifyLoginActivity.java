package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;


public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyLoginActivity";

    private static final int REQUEST_CODE = 1337;

    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String CLIENT_ID = "6182f637ef454837b3186cb1a1dfb860";
    public static final String REDIRECT_URL = "oauth://marcopizarro.com";

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spotify_login);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnLogin) {
                    openLoginWindow();
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
                    Intent intent = new Intent(SpotifyLoginActivity.this, PodcastLoginActivity.class);
                    intent.putExtra(AUTH_TOKEN, response.getAccessToken());
                    startActivity(intent);
                    destroy();
                    break;
                case ERROR:
                    Log.e(TAG, "Auth error: " + response.getError());
                    break;
                default:
                    Log.d(TAG, "Auth result: " + response.getType());
            }
        }
    }


    public void destroy() {
        SpotifyLoginActivity.this.finish();
    }

}