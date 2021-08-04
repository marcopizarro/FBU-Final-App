package com.marcopizarro.podcast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.parse.ParseException;
import com.parse.ParseFile;
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

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Post post = new Post();
                post.setCaption(etCompReview.getText().toString());
                post.setUser(ParseUser.getCurrentUser());
                post.setRating(rbCompRating.getRating());
                post.setPodcast(show.id);
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
}