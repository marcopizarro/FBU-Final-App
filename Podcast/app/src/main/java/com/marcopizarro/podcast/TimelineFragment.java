package com.marcopizarro.podcast;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.jetbrains.annotations.NotNull;
import org.parceler.Parcels;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Show;
import retrofit.client.Response;

public class TimelineFragment extends Fragment {
    public static final String TAG = "TimelineFragment";

    private RecyclerView rvPosts;
    //    private PostsAdapter postsAdapter;
    private List<Post> allPosts;
    PostsAdapter postsAdapter;

    public TimelineFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        allPosts = new ArrayList<>();
        postsAdapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setAdapter(postsAdapter);
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));
        queryData();
    }

    private void queryData() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.addDescendingOrder("createdAt");
        query.setLimit(20);
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Unable to fetch posts", e);
                    return;
                } else {
//                    swipeContainer.setRefreshing(false);

//                    for (int i = 0; i < posts.size(); i++){
//                        SpotifyApi api = new SpotifyApi();
//                        api.setAccessToken(MainActivity.getAuthToken());
//                        SpotifyService spotify = api.getService();
//                        Post post = posts.get(i);
//                        spotify.getShow(post.getPodcast(), new SpotifyCallback<Show>() {
//                            @Override
//                            public void failure(SpotifyError error) {
//                                Log.i(TAG, "error fetching", error);
//                            }
//
//                            @Override
//                            public void success(Show showSimple, Response response) {
//                                post.setPodObj(showSimple);
////                                Log.i(TAG, showSimple.name);
//                            }
//                        });
//                    }
                    postsAdapter.clear();
                    allPosts.addAll(posts);
                    postsAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
    }
}