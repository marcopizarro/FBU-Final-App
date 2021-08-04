package com.marcopizarro.podcast;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.UserPrivate;

public class ProfileActivity extends AppCompatActivity {

    public static final String TAG = "Profile Activity";

    ImageView ivProfileImage;
    TextView tvProfileName;
    TextView tvAverage;
    TextView tvAddList;

    ParseUser parseUser;
    UserPrivate userSpotify = MainActivity.getUserSpotify();

    private RecyclerView rvPostsTop;
    private ProfileAdapter profileAdapterTop;

    private RecyclerView rvPostsRecent;
    private ProfileAdapter profileAdapterRecent;

    private RecyclerView rvLists;
    private ProfileListsAdapter profileListsAdapter;

    public static final int NEW_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_other);

        Intent intent = getIntent();
        parseUser = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvAverage = findViewById(R.id.tvAvgRating);

        tvProfileName.setText(parseUser.getUsername());

        rvPostsTop = findViewById(R.id.rvPostsTop);
        profileAdapterTop = new ProfileAdapter(this, new ArrayList<>());
        rvPostsTop.setAdapter(profileAdapterTop);
        rvPostsTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvPostsRecent = findViewById(R.id.rvPostsRecent);
        profileAdapterRecent = new ProfileAdapter(this, new ArrayList<>());
        rvPostsRecent.setAdapter(profileAdapterRecent);
        rvPostsRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvLists = findViewById(R.id.rvLists);
        profileListsAdapter = new ProfileListsAdapter(this, new ArrayList<>());
        rvLists.setAdapter(profileListsAdapter);
        rvLists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("rating");
        query.setLimit(5);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, parseUser);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    profileAdapterTop.clear();
                    profileAdapterTop.addAll(posts);
                }
            }
        });

        ParseQuery<Post> queryRecent = ParseQuery.getQuery(Post.class);
        queryRecent.addDescendingOrder("createdAt");
        queryRecent.setLimit(5);
        queryRecent.include(Post.KEY_USER);
        queryRecent.whereEqualTo(Post.KEY_USER, parseUser);
        queryRecent.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    profileAdapterRecent.clear();
                    profileAdapterRecent.addAll(posts);
                }
            }
        });

        reloadLists();

        int sum = 0;
        ParseQuery<Post> averageQuery = ParseQuery.getQuery(Post.class);
        averageQuery.whereEqualTo(Post.KEY_USER, parseUser);
        averageQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    int i;
                    double sum = 0;
                    for (i = 0; i < posts.size(); i++) {
                        sum += posts.get(i).getRating();
                    }
                    tvAverage.setText(String.format("Average rating: %1.1f★", sum /= i));
                }
            }
        });


    }


    public void reloadLists() {
        ParseQuery<com.marcopizarro.podcast.List> queryLists = ParseQuery.getQuery(com.marcopizarro.podcast.List.class);
        queryLists.addDescendingOrder("createdAt");
        queryLists.include(Post.KEY_USER);
        queryLists.whereEqualTo(com.marcopizarro.podcast.List.KEY_USER, parseUser);
        queryLists.findInBackground(new FindCallback<com.marcopizarro.podcast.List>() {
            @Override
            public void done(List<com.marcopizarro.podcast.List> lists, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    profileListsAdapter.clear();
                    profileListsAdapter.addAll(lists);
                }
            }
        });
    }

}