package com.marcopizarro.podcast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Show;

public class DetailActivity extends AppCompatActivity {
    public static final String TAG = "DetailActivity";
    public static final int REQUEST_CODE = 40;

    private ReviewTextAdapter reviewTextAdapter;
    private RecyclerView rvReviews;
    private List<Post> allReviews;

    ImageView ivCover;
    TextView tvTitle;
    TextView tvPublisher;
    TextView tvDescription;
    RatingBar rbStars;
    FloatingActionButton btnLog;
    Show show;

    private boolean descExtStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        show = (Show) Parcels.unwrap(getIntent().getParcelableExtra("show"));

        ivCover = findViewById(R.id.ivCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvPublisher = findViewById(R.id.tvPublisher);
        rbStars = findViewById(R.id.rbStars);
        tvDescription = findViewById(R.id.tvDescription);
        btnLog = findViewById(R.id.fabLog);
        rvReviews = findViewById(R.id.rvReviews);

        allReviews = new ArrayList<>();
        reviewTextAdapter = new ReviewTextAdapter(this, allReviews);
        rvReviews.setAdapter(reviewTextAdapter);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        Glide.with(this)
                .load(show.images.get(0).url)
                .into(ivCover);
        ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(show.external_urls.get("spotify")));
                startActivity(i);
                finish();
            }
        });
        queryReviews();
        tvTitle.setText(show.name);
        tvPublisher.setText(show.publisher);
        tvDescription.setText(show.description);

        tvDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descExtStatus){
                    tvDescription.setMaxLines(3);
                    descExtStatus = false;
                } else {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    descExtStatus = true;
                }
            }
        });

        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("show", Parcels.wrap(Parcels.unwrap(getIntent().getParcelableExtra("show"))));
                startActivityForResult(i, REQUEST_CODE);
            }
        });

    }

    private void queryReviews() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("createdAt");
//        query.setLimit(20);
        query.include(Post.KEY_USER);
        query.whereEqualTo("podcast", show.id);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    int i;
                    float averageRating = 0;
                    for (i = 0; i < posts.size(); i++) {
                        Post post = posts.get(i);
                        averageRating += post.getRating();
                    }
                    averageRating /= i;
                    rbStars.setIsIndicator(true);
                    rbStars.setRating(averageRating);
                    reviewTextAdapter.clear();
                    allReviews.clear();
                    allReviews.addAll(posts);
                    reviewTextAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        queryReviews();
    }
}