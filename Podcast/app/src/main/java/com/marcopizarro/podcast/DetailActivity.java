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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Show;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final String TAG = "DetailActivity";
    public static final int REQUEST_CODE = 40;

    private ReviewTextAdapter reviewTextAdapter;
    private RecyclerView rvReviews;
    private List<Post> allReviews;
    private List<com.marcopizarro.podcast.List> allLists;

    private int chosenList = 0;

    ImageView ivCover;
    TextView tvTitle;
    TextView tvPublisher;
    TextView tvDescription;
    TextView tvAddToList;
    TextView tvLog;
    RatingBar rbStars;
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
        tvLog = findViewById(R.id.tvLog);
        rvReviews = findViewById(R.id.rvReviews);
        tvAddToList = findViewById(R.id.tvAddToList);

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
                if (descExtStatus) {
                    tvDescription.setMaxLines(3);
                    descExtStatus = false;
                } else {
                    tvDescription.setMaxLines(Integer.MAX_VALUE);
                    descExtStatus = true;
                }
            }
        });

        tvLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, ComposeActivity.class);
                i.putExtra("show", Parcels.wrap(Parcels.unwrap(getIntent().getParcelableExtra("show"))));
                startActivityForResult(i, REQUEST_CODE);
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayList<String> listNames = new ArrayList<String>();
        allLists = new ArrayList<com.marcopizarro.podcast.List>();
        ParseQuery<com.marcopizarro.podcast.List> queryLists = ParseQuery.getQuery(com.marcopizarro.podcast.List.class);
        queryLists.addDescendingOrder("createdAt");
        queryLists.include(Post.KEY_USER);
        queryLists.whereEqualTo(com.marcopizarro.podcast.List.KEY_USER, ParseUser.getCurrentUser());
        queryLists.findInBackground(new FindCallback<com.marcopizarro.podcast.List>() {
            @Override
            public void done(List<com.marcopizarro.podcast.List> lists, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    allLists.addAll(lists);
                    for (int i = 0; i < lists.size(); i++) {
                        listNames.add(lists.get(i).getName());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DetailActivity.this, android.R.layout.simple_spinner_dropdown_item, listNames);
                    spinner.setAdapter(adapter);
                    spinner.setVisibility(View.VISIBLE);
                }
            }
        });


        tvAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("List");
                query.getInBackground(allLists.get(chosenList).getObjectId(), new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            object.add("podcasts", show.id);
                            object.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    Toast.makeText(DetailActivity.this, "Saved to List!", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(DetailActivity.this, "No Lists Available", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void queryReviews() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("createdAt");
        query.setLimit(5);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        chosenList = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}