package com.marcopizarro.podcast;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.UserPrivate;


public class ProfileFragment extends Fragment {

    public static final String TAG = "ProfileFragment";

    ImageView ivProfileImage;
    TextView tvProfileName;
    TextView tvAverage;

    private RecyclerView rvPostsTop;
    private List<Post> topPosts;
    private ProfileAdapter profileAdapterTop;

    private RecyclerView rvPostsRecent;
    private List<Post> recentPosts;
    private ProfileAdapter profileAdapterRecent;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ivProfileImage = getActivity().findViewById(R.id.ivProfileImage);
        tvProfileName = getActivity().findViewById(R.id.tvProfileName);
        tvAverage =getActivity().findViewById(R.id.tvAvgRating);

        UserPrivate userSpotify = MainActivity.getUserSpotify();
        if (userSpotify != null) {
            if (userSpotify.images != null & userSpotify.images.get(0).url != null){
                Glide.with(this)
                        .load(userSpotify.images.get(0).url)
                        .circleCrop()
                        .into(ivProfileImage);
            }
            tvProfileName.setText(userSpotify.display_name);
        }

        rvPostsTop = view.findViewById(R.id.rvPostsTop);
        topPosts = new ArrayList<>();
        profileAdapterTop = new ProfileAdapter(getContext(), topPosts);
        rvPostsTop.setAdapter(profileAdapterTop);
        rvPostsTop.setLayoutManager(new GridLayoutManager(getContext(), 3));

        rvPostsRecent = view.findViewById(R.id.rvPostsRecent);
        recentPosts = new ArrayList<>();
        profileAdapterRecent = new ProfileAdapter(getContext(), recentPosts);
        rvPostsRecent.setAdapter(profileAdapterRecent);
        rvPostsRecent.setLayoutManager(new GridLayoutManager(getContext(), 3));

        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("rating");
        query.setLimit(3);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
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
        queryRecent.setLimit(3);
        queryRecent.include(Post.KEY_USER);
        queryRecent.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        queryRecent.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    profileAdapterRecent.clear();
                    recentPosts.addAll(posts);
                    profileAdapterRecent.notifyDataSetChanged();
                }
            }
        });

        int sum = 0;
        ParseQuery<Post> averageQuery = ParseQuery.getQuery(Post.class);
        averageQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        averageQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
                    int i;
                    double sum = 0;
                    for(i = 0; i < posts.size(); i++){
                        sum += posts.get(i).getRating();
                    }
                    tvAverage.setText(String.format("Average rating: %1.1f★", sum /= i));
                }
            }
        });

    }
}