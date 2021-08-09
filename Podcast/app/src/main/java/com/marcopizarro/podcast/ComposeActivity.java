package com.marcopizarro.podcast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import kaaes.spotify.webapi.android.models.Show;

public class ComposeActivity extends AppCompatActivity {
    public static final String TAG = "ComposeActivity";

    EditText etCompReview;
    TextView tvCompTitle;
    TextView tvCompPublisher;
    Button btnSubmit;
    RatingBar rbCompRating;
    ImageView ivCompCover;
    ParseGeoPoint postLocation;

    private FusedLocationProviderClient fusedLocationClient;
    public static final int REQUEST_CODE = 31;
    boolean locationGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompReview = findViewById(R.id.etCompReview);
        tvCompTitle = findViewById(R.id.tvCompTitle);
        tvCompPublisher = findViewById(R.id.tvCompPublisher);
        btnSubmit = findViewById(R.id.btnSubmit);
        rbCompRating = findViewById(R.id.rbCompRating);
        ivCompCover = findViewById(R.id.ivCompCover);

        Intent intent = getIntent();
        Show show = (Show) Parcels.unwrap(getIntent().getParcelableExtra("show"));

        Log.i(TAG, show.name);
        tvCompPublisher.setText(show.publisher);
        tvCompTitle.setText(show.name);
        Glide.with(this)
                .load(show.images.get(0).url)
                .into(ivCompCover);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            locationGranted = true;
            getDeviceLocation();
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post();
                post.setCaption(etCompReview.getText().toString());
                post.setUser(ParseUser.getCurrentUser());
                post.setRating(rbCompRating.getRating());
                post.setPodcast(show.id);
                if (postLocation != null) {
                    post.setLocation(postLocation);
                }
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null) {
                            Log.e(TAG, "Error while saving", e);
                            Toast.makeText(ComposeActivity.this, "Error Saving", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "location Permission Granted", Toast.LENGTH_SHORT).show();
                locationGranted = true;
                getDeviceLocation();
            } else {
                Toast.makeText(this, "location Permission Denied", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
            }
        }
    }

    private void getDeviceLocation() {
        try {
            @SuppressLint("MissingPermission") Task<Location> locationResult = fusedLocationClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        postLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}