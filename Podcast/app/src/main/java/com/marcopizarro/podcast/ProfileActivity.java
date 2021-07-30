package com.marcopizarro.podcast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.Show;
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
    private List<Post> topPosts;
    private ProfileAdapter profileAdapterTop;

    private RecyclerView rvPostsRecent;
    private List<Post> recentPosts;
    private ProfileAdapter profileAdapterRecent;

    private RecyclerView rvLists;
    private List<com.marcopizarro.podcast.List> lists;
    private ProfileListsAdapter profileListsAdapter;

    public static final int NEW_LIST = 1; // class variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile_other);

        Intent intent = getIntent();
        parseUser = (ParseUser) Parcels.unwrap(getIntent().getParcelableExtra("user"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        tvProfileName = findViewById(R.id.tvProfileName);
        tvAverage = findViewById(R.id.tvAvgRating);

//        if (userSpotify != null) {
//            if (userSpotify.images != null & userSpotify.images.get(0).url != null) {
//                Glide.with(this)
//                        .load(userSpotify.images.get(0).url)
//                        .circleCrop()
//                        .into(ivProfileImage);
//            }
            tvProfileName.setText(parseUser.getUsername());
//        }

        rvPostsTop = findViewById(R.id.rvPostsTop);
        topPosts = new ArrayList<>();
        profileAdapterTop = new ProfileAdapter(this, topPosts);
        rvPostsTop.setAdapter(profileAdapterTop);
        rvPostsTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvPostsRecent = findViewById(R.id.rvPostsRecent);
        recentPosts = new ArrayList<>();
        profileAdapterRecent = new ProfileAdapter(this, recentPosts);
        rvPostsRecent.setAdapter(profileAdapterRecent);
        rvPostsRecent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvLists = findViewById(R.id.rvLists);
        lists = new ArrayList<>();
        profileListsAdapter = new ProfileListsAdapter(this, lists);
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
                    topPosts.addAll(posts);
                    profileAdapterTop.notifyDataSetChanged();
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